import os
import glob

# Universal mobile CSS patch targeting tables, modals, action bars, and inputs
# We ensure !important is used to override any desktop-first styling
CSS_PATCH = '''
        /* CareReach Universal Mobile Patch */
        @media (max-width: 768px) {
            /* Fix overflowing tables */
            .table-container {
                overflow-x: auto !important;
                -webkit-overflow-scrolling: touch;
                width: 100vw !important;
                margin-left: -20px !important;
                margin-right: -20px !important;
                padding: 0 20px !important;
                border-radius: 0 !important;
            }
            .table-container table {
                min-width: 800px; /* Ensure table keeps format, users can swipe sideways */
            }

            /* Fix Modals being cut off */
            .modal-content {
                width: 95% !important;
                margin: 5% auto !important;
                padding: 20px !important;
                max-height: 90vh !important;
                overflow-y: auto !important;
            }

            /* Fix Page Headers (Title + Buttons) */
            .page-header {
                flex-direction: column !important;
                align-items: flex-start !important;
                gap: 15px !important;
            }
            .page-header .btn {
                width: 100% !important;
                justify-content: center !important;
            }

            /* Fix Search and Filtering Bars */
            .filter-bar {
                flex-direction: column !important;
            }
            .filter-bar .search-box, 
            .filter-bar .filter-select {
                width: 100% !important;
                max-width: 100% !important;
            }

            /* Fix Form Grids */
            .form-grid {
                grid-template-columns: 1fr !important;
            }
            
            /* Main Content Padding adjustments */
            .main-content {
                padding: 15px !important;
            }
            
            /* Metric Cards Grid */
            .metrics-grid {
                grid-template-columns: 1fr !important;
            }
        }
'''

def patch_html_files():
    base_dir = r"CareReach-Frontend"
    
    html_files = []
    for root, dirs, files in os.walk(base_dir):
        for file in files:
            if file.endswith('.html'):
                html_files.append(os.path.join(root, file))
                
    count = 0
    for file_path in html_files:
        with open(file_path, 'r', encoding='utf-8') as f:
            content = f.read()
            
        if "CareReach Universal Mobile Patch" in content:
            continue
            
        if "</style>" in content:
            updated = content.replace("</style>", CSS_PATCH + "\n    </style>")
            with open(file_path, 'w', encoding='utf-8') as f:
                f.write(updated)
            count += 1
            print(f"Patched: {file_path}")
            
    print(f"Successfully patched {count} files!")

patch_html_files()
