import os

CSS_PATCH_3 = '''
    /* CareReach SUPREME Mobile Boundary Enforcement */
    @media (max-width: 768px) {
        * {
            box-sizing: border-box !important;
            max-width: 100% !important;
        }
        html, body {
            overflow-x: hidden !important;
            width: 100% !important;
            margin: 0 !important;
            padding: 0 !important;
        }
        
        .panel {
            flex-direction: column !important;
            width: 100% !important;
            overflow: hidden !important;
            padding: 15px !important;
            margin-bottom: 20px !important;
        }
        
        .panel-body {
            padding: 10px 0 !important;
            width: 100% !important;
        }

        /* Force any side-by-side flexbox elements to wrap */
        .form-group, 
        .panel-header, 
        div[style*="display: flex"],
        div[style*="display:flex"] {
            flex-wrap: wrap !important;
        }

        /* Stop buttons from colliding */
        .btn, button {
            width: 100% !important;
            white-space: normal !important;
            margin-bottom: 8px !important;
        }

        input, select, textarea, .custom-dropdown {
            width: 100% !important;
            max-width: 100% !important;
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
                
                if "SUPREME Mobile Boundary" in content:
                    continue
                
                if "</style>" in content:
                    updated = content.replace("</style>", CSS_PATCH_3 + "\n    </style>")
                    with open(file_path, 'w', encoding='utf-8') as f:
                        f.write(updated)
                        
patch_html_files()
