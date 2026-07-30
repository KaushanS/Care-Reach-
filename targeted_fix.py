import os
import re

def fix_volunteer_pages():
    pages = [
        "CareReach-Frontend/pages/volunteer/report-patient.html",
        "CareReach-Frontend/pages/officer/patients.html",
        "CareReach-Frontend/pages/admin/form.html"
    ]
    
    for path in pages:
        if not os.path.exists(path): continue
        with open(path, 'r', encoding='utf-8') as f:
            content = f.read()

        # Fix 1: Stop dropdown from going off-screen to the left on mobile
        dropdown_fix = '''
        @media (max-width: 768px) {
            .notification-dropdown {
                position: fixed !important;
                top: 72px !important;
                left: 5% !important;
                width: 90% !important;
                max-width: none !important;
                right: auto !important;
            }
            
            /* FORCE wrapping on Cancel/Submit and GPS */
            .panel-body > div[style*="display: flex"],
            .panel-body > div[style*="display:flex"] {
                flex-direction: column !important;
                gap: 15px !important;
            }
            .panel-body button {
                width: 100% !important;
                margin-top: 10px !important;
            }
            
            /* Ensure the text inputs strictly fit */
            .form-group {
                width: 100% !important;
                max-width: 100% !important;
                padding: 0 !important;
                margin: 0 !important;
            }
            .form-grid {
                display: flex !important;
                flex-direction: column !important;
                width: 100% !important;
            }
            input, select, textarea {
                width: 100% !important;
                max-width: 100% !important;
            }
        }
        '''
        
        # Inject this specific targeted mobile fix
        if "FORCE wrapping on Cancel/Submit" not in content:
            content = content.replace("</style>", dropdown_fix + "\n</style>")
            
        # Fix 2: Remove double target icons!
        # Specifically, look for the 'bi-crosshair' inside the input div, and remove it.
        content = re.sub(r'<i class="bi bi-crosshair"[^>]*></i>', '', content)
        
        # Save
        with open(path, 'w', encoding='utf-8') as f:
            f.write(content)
            print(f"Patched {path}")

fix_volunteer_pages()
