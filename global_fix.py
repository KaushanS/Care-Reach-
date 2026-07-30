import os

def apply_global_mobile_fixes():
    base_dir = r"CareReach-Frontend"
    
    global_fixes = '''
        /* Global Mobile Fallback & Dropdown Fix */
        @media (max-width: 768px) {
            /* Guarantee ALL notification dropdowns float exactly in the center */
            .notification-dropdown {
                position: fixed !important;
                top: 72px !important;
                left: 5% !important;
                width: 90% !important;
                max-width: none !important;
                right: auto !important;
                z-index: 9999 !important;
            }
            
            /* Guarantee ALL modals take up the full screen */
            .modal-content, .modal {
                width: 95% !important;
                max-width: 95% !important;
                margin: 5% auto !important;
                padding: 15px !important;
            }
            
            /* Any data grid inside a modal must stack horizontally */
            .details-grid, .info-grid, .data-grid {
                display: flex !important;
                flex-direction: column !important;
                gap: 15px !important;
            }
            
            /* Generic fix for internal modal details */
            .detail-item, .info-item {
                display: flex !important;
                flex-direction: column !important;
                width: 100% !important;
            }
            
            /* If they used flex rows for labels and values, force them to wrap */
            .modal-body div[style*="display: flex"] {
                flex-wrap: wrap !important;
            }
        }
    '''
    
    for root, dirs, files in os.walk(base_dir):
        for file in files:
            if file.endswith('.html'):
                file_path = os.path.join(root, file)
                with open(file_path, 'r', encoding='utf-8') as f:
                    content = f.read()

                # Don't double apply
                if "Global Mobile Fallback" not in content and "</style>" in content:
                    content = content.replace("</style>", global_fixes + "\n</style>")
                    with open(file_path, 'w', encoding='utf-8') as f:
                        f.write(content)

apply_global_mobile_fixes()
