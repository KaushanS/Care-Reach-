package com.carereach.backend.config;

import com.carereach.backend.models.District;
import com.carereach.backend.models.DivisionalSecretariat;
import com.carereach.backend.models.GramaNiladhariDivision;
import com.carereach.backend.repositories.DistrictRepository;
import com.carereach.backend.repositories.DivisionalSecretariatRepository;
import com.carereach.backend.repositories.GramaNiladhariDivisionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final DistrictRepository districtRepository;
    private final DivisionalSecretariatRepository dsRepository;
    private final GramaNiladhariDivisionRepository gnDivisionRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        if (districtRepository.count() == 0) {
            System.out.println("⏳ Starting automated Geographic Data Seeder from CSV with 3-Tier Hierarchy...");

            try {
                InputStream is = getClass().getResourceAsStream("/locations.csv");
                if (is == null) {
                    File file = new File("src/main/resources/locations.csv");
                    if (!file.exists()) {
                        file = new File("src/main/resources/locations.csv.csv");
                    }
                    if (file.exists()) {
                        is = new FileInputStream(file);
                    }
                }

                if (is == null) {
                    throw new RuntimeException(
                            "Could not locate locations.csv or locations.csv.csv in src/main/resources!");
                }

                BufferedReader br = new BufferedReader(new InputStreamReader(is));

                String line;
                boolean isFirstLine = true;

                // Cache to prevent massive duplicate DB lookups for Districts & DS
                Map<String, District> districtCache = new HashMap<>();
                Map<String, DivisionalSecretariat> dsCache = new HashMap<>();

                while ((line = br.readLine()) != null) {
                    if (isFirstLine) {
                        isFirstLine = false;
                        continue;
                    }

                    String[] columns = line.split(",");
                    if (columns.length >= 3) {
                        String districtName = columns[0].trim().replace("\"", "");
                        String dsName = columns[1].trim().replace("\"", "");
                        String gnDivisionName = columns[2].trim().replace("\"", "");

                        if (districtName.isEmpty() || gnDivisionName.isEmpty())
                            continue;

                        // 1. Get or Create District
                        District district = districtCache.get(districtName);
                        if (district == null) {
                            district = new District();
                            district.setName(districtName);
                            district = districtRepository.save(district);
                            districtCache.put(districtName, district);
                        }

                        // 2. Get or Create Divisional Secretariat
                        // Must combine DS Name + District Name for cache key since multiple districts
                        // might have identical DS names
                        String cacheKey = districtName + "_" + dsName;
                        DivisionalSecretariat ds = dsCache.get(cacheKey);
                        if (ds == null) {
                            ds = new DivisionalSecretariat();
                            ds.setName(dsName);
                            ds.setDistrict(district);
                            ds = dsRepository.save(ds);
                            dsCache.put(cacheKey, ds);
                        }

                        // 3. Create and Save GN Division
                        GramaNiladhariDivision div = new GramaNiladhariDivision();
                        div.setName(gnDivisionName);
                        div.setDivisionalSecretariat(ds);
                        gnDivisionRepository.save(div);
                    }
                }

                br.close();
                System.out.println("✅ Successfully seeded 14,000+ Divisions across 3-Tiers from locations.csv!");

            } catch (Exception e) {
                System.err.println("❌ Failed to parse locations.csv!");
                System.err.println("Error: " + e.getMessage());
            }
        }

        // Automated Cleanup Sequence - Now strips quotes from DS as well
        java.util.List<DivisionalSecretariat> dsList = dsRepository.findAll();
        for (DivisionalSecretariat ds : dsList) {
            if (ds.getName().contains("\"")) {
                ds.setName(ds.getName().replace("\"", ""));
                dsRepository.save(ds);
            }
        }
        java.util.List<GramaNiladhariDivision> divisions = gnDivisionRepository.findAll();
        for (GramaNiladhariDivision div : divisions) {
            if (div.getName().contains("\"")) {
                div.setName(div.getName().replace("\"", ""));
                gnDivisionRepository.save(div);
            }
        }
    }
}
