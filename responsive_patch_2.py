import os

CSS_PATCH_2 = '''
        /* CareReach Mobile Box-Sizing & Margin Override */
        @media (max-width: 768px) {
            * {
                box-sizing: border-box !important;
            }
            body, html {
                overflow-x: hidden !important;
                width: 100% !important;
            }
            input, select, textarea, .form-control {
                width: 100% !important;
                max-width: 100% !important;
                box-sizing: border-box !important;
            }
            .form-section, .dashboard-card, .metric-card {
                padding: 15px !important;
                margin: 10px 0 !important;
                width: 100% !important;
            }
            .main-content {
                padding: 10px !important;
                width: 100% !important;
                margin-left: 0 !important;
            }
        }
'''

def patch_html_files():
    base_dir = r"CareReach-Frontend"
    
    for root, dirs, files in os.walk(base_dir):
        for file in files:
            if file.endswith('.html'):
                file_path = os.path.join(root, file)
                with open(file_path, 'r', encoding='utf-8') as f:
                    content = f.read()
                
                if "CareReach Mobile Box-Sizing & Margin Override" in content:
                    continue
                
                if "</style>" in content:
                    updated = content.replace("</style>", CSS_PATCH_2 + "\n    </style>")
                    with open(file_path, 'w', encoding='utf-8') as f:
                        f.write(updated)

patch_html_files()
