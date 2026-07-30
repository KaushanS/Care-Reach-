import os

def fix_modal_regression():
    base_dir = r"CareReach-Frontend"
    
    for root, dirs, files in os.walk(base_dir):
        for file in files:
            if file.endswith('.html'):
                file_path = os.path.join(root, file)
                with open(file_path, 'r', encoding='utf-8') as f:
                    content = f.read()

                # Remove the destructive global utton 100% width
                if ".btn, button {" in content:
                    content = content.replace(".btn, button {", ".btn {")
                    
                # Inject precise fix for map modal on mobile
                map_modal_fix = '''
        /* Map Modal Mobile Enhancements */
        .map-search-bar {
            flex-direction: column !important;
        }
        .map-modal-footer {
            flex-direction: column-reverse !important;
            gap: 10px !important;
        }
        .map-modal-footer .btn {
            width: 100% !important;
            margin: 0 !important;
        }
        /* Stop the header from getting squished by button rules */
        .map-modal-header {
            flex-wrap: nowrap !important;
        }
        .close-map-btn {
            width: auto !important;
            padding: 10px !important;
        }
                '''
                
                # Check if it was already injected
                if "Map Modal Mobile Enhancements" not in content and "</style>" in content:
                    content = content.replace("</style>", map_modal_fix + "\n</style>")
                    
                with open(file_path, 'w', encoding='utf-8') as f:
                    f.write(content)

fix_modal_regression()
