import os

def fix_destructive_styles():
    base_dir = r"CareReach-Frontend"
    
    for root, dirs, files in os.walk(base_dir):
        for file in files:
            if file.endswith('.html'):
                file_path = os.path.join(root, file)
                with open(file_path, 'r', encoding='utf-8') as f:
                    content = f.read()
                
                # We need to remove max-width: 100% !important; inside * { } injected purely by Patch 3
                if "max-width: 100% !important;" in content and "CareReach SUPREME Mobile Boundary Enforcement" in content:
                    updated = content.replace("max-width: 100% !important;", "/* max-width removed */")
                    
                    # We also add a specific override for notification dropdowns to guarantee they aren't messed up
                    custom_dropdown_fix = '''
        .notification-dropdown {
            width: 300px !important;
            max-width: 90vw !important;
            right: -20px !important;
        }
        .header-search { display: none !important; }
        .top-header {
            padding: 0 16px !important;
            height: auto !important;
            min-height: 72px !important;
            display: flex !important;
            flex-wrap: nowrap !important;
            justify-content: space-between !important;
            align-items: center !important;
        }
        .mobile-toggle {
            margin-right: 15px !important;
            display: block !important;
            flex-shrink: 0 !important;
        }
        .notification-wrapper {
            flex-shrink: 0 !important;
        }
        .user-profile {
            padding-left: 10px !important;
            border-left: none !important;
        }
                    '''
                    
                    updated = updated.replace("/* Stop buttons from colliding */", custom_dropdown_fix + "\n        /* Stop buttons from colliding */")
                    with open(file_path, 'w', encoding='utf-8') as f:
                        f.write(updated)
                        
fix_destructive_styles()
