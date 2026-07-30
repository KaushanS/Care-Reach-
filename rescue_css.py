import os
import re

def rescue_frontend():
    base_dir = r"CareReach-Frontend"
    
    MASTER_CSS = '''
    /* =========================================
       CareReach Master Mobile Responsiveness
       ========================================= */
    @media (max-width: 768px) {
        * { box-sizing: border-box !important; }
        html, body { overflow-x: hidden !important; width: 100% !important; margin: 0 !important; padding: 0 !important; position: relative; }
        .main-content { margin-left: 0 !important; width: 100% !important; }
        .sidebar { transform: translateX(-100%); position: fixed; z-index: 1000; }
        .sidebar.open { transform: translateX(0); }
        
        /* Padding and Layouts */
        .content-area { padding: 15px 10px !important; width: 100% !important; max-width: 100% !important; overflow-x: hidden !important; }
        .metric-card, .panel, .dashboard-card { margin-left: 0 !important; margin-right: 0 !important; width: 100% !important; overflow: visible !important; }
        .panel { padding: 15px !important; margin-bottom: 20px !important; display: flex !important; flex-direction: column !important; }
        .panel-body { padding: 10px 0 !important; width: 100% !important; overflow-x: auto !important; }
        
        /* Modals and Data Grids */
        .modal, .modal-content { width: 95% !important; max-width: 95% !important; margin: 5% auto !important; padding: 15px !important; overflow-y: auto !important; max-height: 90vh !important; }
        .details-grid, .info-grid, .data-grid { display: flex !important; flex-direction: column !important; gap: 15px !important; }
        .detail-item, .info-item { display: flex !important; flex-direction: column !important; width: 100% !important; }
        .modal-body div[style*="display: flex"] { flex-wrap: wrap !important; }
        .map-search-bar { flex-direction: column !important; }
        .map-modal-footer { flex-direction: column-reverse !important; gap: 10px !important; }
        .map-modal-footer .btn { width: 100% !important; margin: 0 !important; }
        .map-modal-header { flex-wrap: nowrap !important; }
        .close-map-btn { width: auto !important; padding: 10px !important; }
        
        /* Forms, Inputs, Buttons */
        .form-group, .panel-header, div[style*="display: flex"], div[style*="display:flex"] { flex-wrap: wrap !important; }
        input, select, textarea, .form-control, .custom-dropdown { width: 100% !important; max-width: 100% !important; margin-left: 0 !important; }
        .form-group { width: 100% !important; max-width: 100% !important; padding: 0 !important; margin: 0 !important; }
        .form-grid { display: flex !important; flex-direction: column !important; width: 100% !important; gap:10px !important; }
        .btn { width: 100% !important; white-space: normal !important; margin-bottom: 8px !important; }
        .panel-body > div[style*="display: flex"], .panel-body > div[style*="display:flex"] { flex-direction: column !important; gap: 15px !important; }
        .panel-body button { width: 100% !important; margin-top: 10px !important; }
        
        /* Tables and Advanced Nav */
        .table-container, .table-responsive { width: 100% !important; max-width: 100% !important; overflow-x: auto !important; -webkit-overflow-scrolling: touch; }
        td, th { white-space: nowrap !important; }
        .filter-tabs { display: flex !important; flex-wrap: wrap !important; gap: 5px !important; }
        .filter-tab { flex: 1 1 auto !important; text-align: center !important; }
        .header-search { display: none !important; }
        .top-header { padding: 0 16px !important; height: auto !important; min-height: 72px !important; display: flex !important; flex-wrap: nowrap !important; justify-content: space-between !important; align-items: center !important; }
        .mobile-toggle { margin-right: 15px !important; display: block !important; flex-shrink: 0 !important; }
        .user-profile { padding-left: 10px !important; border-left: none !important; }
        .notification-wrapper { flex-shrink: 0 !important; }
        .notification-dropdown { position: fixed !important; top: 72px !important; left: 5% !important; width: 90% !important; max-width: none !important; right: auto !important; z-index: 9999 !important; }
    }
'''

    for root, dirs, files in os.walk(base_dir):
        for file in files:
            if file.endswith('.html'):
                path = os.path.join(root, file)
                with open(path, 'r', encoding='utf-8') as f:
                    content = f.read()

                # Step 1: Strip OUT absolutely ALL fragment CSS blocks.
                # Notice we are just matching from /* CareReach Universal Mobile Patch to ANY following closing } that ends the media query.
                content = re.sub(r'/\* CareReach Universal Mobile Patch \*/.*?(?=</style>)', '', content, flags=re.DOTALL)
                content = re.sub(r'/\* CareReach Mobile Box-Sizing.*?(?=</style>)', '', content, flags=re.DOTALL)
                content = re.sub(r'/\* CareReach SUPREME.*?(?=</style>)', '', content, flags=re.DOTALL)
                content = re.sub(r'/\* Table Scroll Override.*?(?=</style>)', '', content, flags=re.DOTALL)
                content = re.sub(r'/\* Global Mobile Fallback.*?(?=</style>)', '', content, flags=re.DOTALL)
                content = re.sub(r'/\* Eradicate massive desktop padding.*?(?=</style>)', '', content, flags=re.DOTALL)
                content = re.sub(r'/\* Map Modal Mobile Enhancements.*?(?=</style>)', '', content, flags=re.DOTALL)

                # Most dangerously, rip out the unclosed rogue string rendering in visual DOM
                # The rogue string literally prints on the screen, meaning it has NO </style> enclosing it!
                content = re.sub(r'/\* CareReach Universal Mobile Patch \*/.*?\}\s*\}\s*\}', '', content, flags=re.DOTALL)
                content = re.sub(r'/\* CareReach Mobile Box-Sizing.*?\}\s*\}', '', content, flags=re.DOTALL)
                content = re.sub(r'/\* CareReach SUPREME.*?\}\s*\}', '', content, flags=re.DOTALL)
                content = re.sub(r'/\* Table Scroll Override.*?\}\s*\}', '', content, flags=re.DOTALL)
                content = re.sub(r'/\* Global Mobile Fallback.*?\}\s*\}', '', content, flags=re.DOTALL)
                content = re.sub(r'/\* Eradicate massive desktop padding.*?\}\s*\}', '', content, flags=re.DOTALL)

                # Step 2: Ensure any empty dangling </style> blocks are removed if they were orphaned
                content = re.sub(r'<style>[\s\n]*</style>', '', content, flags=re.DOTALL)

                # Step 3: Inject the Master block cleanly ONE TIME strictly within the <head> segment
                if "CareReach Master Mobile Responsiveness" not in content and "</head>" in content:
                    content = content.replace("</head>", "<style>\n" + MASTER_CSS + "\n</style>\n</head>")

                with open(path, 'w', encoding='utf-8') as f:
                    f.write(content)

rescue_frontend()
