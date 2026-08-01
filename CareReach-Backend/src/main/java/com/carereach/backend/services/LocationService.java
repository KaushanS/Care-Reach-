package com.carereach.backend.services;

import com.carereach.backend.models.District;
import com.carereach.backend.models.DivisionalSecretariat;
import com.carereach.backend.models.GramaNiladhariDivision;
import com.carereach.backend.repositories.DistrictRepository;
import com.carereach.backend.repositories.DivisionalSecretariatRepository;
import com.carereach.backend.repositories.GramaNiladhariDivisionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import com.carereach.backend.dtos.GnDivisionDto;
import com.carereach.backend.dtos.DsDto;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LocationService {
    private final DistrictRepository districtRepository;
    private final DivisionalSecretariatRepository dsRepository;
    private final GramaNiladhariDivisionRepository gnDivisionRepository;

    public List<District> getAllDistricts() {
        return districtRepository.findAll();
    }

    public List<DivisionalSecretariat> getDsByDistrict(Long districtId) {
        return dsRepository.findByDistrictId(districtId);
    }

    public List<GramaNiladhariDivision> getGnDivisionsByDs(Long dsId) {
        return gnDivisionRepository.findByDivisionalSecretariatId(dsId);
    }

    public List<GnDivisionDto> getAllGnDivisionsWithParents() {
        return gnDivisionRepository.findAll().stream().map(gn -> {
            GnDivisionDto dto = new GnDivisionDto();
            dto.setId(gn.getId());
            dto.setName(gn.getName());
            if (gn.getDivisionalSecretariat() != null) {
                dto.setDsId(gn.getDivisionalSecretariat().getId());
                dto.setDsName(gn.getDivisionalSecretariat().getName());
                if (gn.getDivisionalSecretariat().getDistrict() != null) {
                    dto.setDistrictId(gn.getDivisionalSecretariat().getDistrict().getId());
                    dto.setDistrictName(gn.getDivisionalSecretariat().getDistrict().getName());
                }
            }
            return dto;
        }).collect(Collectors.toList());
    }

    public List<DsDto> getAllDivisionalSecretariats() {
        return dsRepository.findAll().stream().map(ds -> {
            DsDto dto = new DsDto();
            dto.setId(ds.getId());
            dto.setName(ds.getName());
            if (ds.getDistrict() != null) {
                dto.setDistrictId(ds.getDistrict().getId());
                dto.setDistrictName(ds.getDistrict().getName());
            }
            return dto;
        }).collect(Collectors.toList());
    }
}
