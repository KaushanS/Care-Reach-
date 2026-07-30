import os

def fix_mobile_padding():
    base_dir = r"CareReach-Frontend"
    
    padding_css = '''
        /* Eradicate massive desktop padding on mobile screens */
        @media (max-width: 768px) {
            .content-area {
                padding: 15px 10px !important; 
                width: 100vw !important;
                max-width: 100vw !important;
                overflow-x: hidden !important;
            }
            .metric-card {
                margin-left: 0 !important;
                margin-right: 0 !important;
                width: 100% !important;
            }
        }
    '''
    
    for root, dirs, files in os.walk(base_dir):
        for file in files:
            if file.endswith('.html'):
                file_path = os.path.join(root, file)
                with open(file_path, 'r', encoding='utf-8') as f:
                    content = f.read()

                if "Eradicate massive desktop padding" not in content and "</style>" in content:
                    content = content.replace("</style>", padding_css + "\n</style>")
                    with open(file_path, 'w', encoding='utf-8') as f:
                        f.write(content)

fix_mobile_padding()
