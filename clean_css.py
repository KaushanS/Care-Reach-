import os
import re

def rewrite_css_perfectly():
    for root, dirs, files in os.walk('CareReach-Frontend'):
        for file in files:
            if file.endswith('.html'):
                path = os.path.join(root, file)
                with open(path, 'r', encoding='utf-8') as f:
                    content = f.read()

                # Find all our injected blocks! They all are inside @media (max-width: 768px) { ... }
                # Actually, there are multiple injections.
                # Let's completely wipe EVERYTHING starting from /* CareReach Universal Mobile Patch */ 
                # up to </style>! But wait, if they are nested, we need a robust regex.
                
                # Because the injections always happen right before </style>, they pile up block by block.
                # Let's purge them!
                original_content = re.sub(r'/\* CareReach Universal Mobile Patch \*/.*?(?=</style>)', '', content, flags=re.DOTALL)
                original_content = re.sub(r'/\* CareReach Mobile Box-Sizing.*?override \*/.*?\}', '', original_content, flags=re.DOTALL) # older patches if any
                original_content = re.sub(r'/\* CareReach SUPREME.*?(?=</style>)', '', original_content, flags=re.DOTALL)
                original_content = re.sub(r'/\* Table Scroll Override.*?(?=</style>)', '', original_content, flags=re.DOTALL)
                original_content = re.sub(r'/\* Global Mobile Fallback.*?(?=</style>)', '', original_content, flags=re.DOTALL)
                original_content = re.sub(r'/\* Eradicate massive desktop padding.*?(?=</style>)', '', original_content, flags=re.DOTALL)
                
                # Some rogue CSS might be visible on screen because it's after HTML or body due to string replacement bug!
                # If there's literally RAW text /* CareReach Universal ... in the body, let's purge it everywhere in the file!
                content = re.sub(r'/\* CareReach Universal Mobile Patch \*/.*?\}[\s]*\}[\s]*}', '', content, flags=re.DOTALL) # Wipe aggressive rogue CSS blocks everywhere
                
                # Let's just create ONE clean master block.
                MASTER_CSS = '''
    /* =========================================
       CareReach Master Mobile Responsiveness
       ========================================= */
    @media (max-width: 768px) {
        * { box-sizing: border-box !important; }
        html, body { overflow-x: hidden !important; width: 100% !important; margin: 0 !important; padding: 0 !important; }
        .main-content { margin-left: 0 !important; width: 100% !important; }
        .sidebar { transform: translateX(-100%); position: fixed; z-index: 1000; }
        .sidebar.open { transform: translateX(0); }
        
        .content-area { padding: 15px 10px !important; width: 100% !important; max-width: 100% !important; }
        .metric-card, .panel, .dashboard-card { margin-left: 0 !important; margin-right: 0 !important; width: 100% !important; overflow: visible !important; }
        .panel { padding: 15px !important; margin-bottom: 20px !important; display: flex !important; flex-direction: column !important; }
        .panel-body { padding: 10px 0 !important; width: 100% !important; overflow-x: auto !important; }
        
        /* Modals */
        .modal, .modal-content { width: 95% !important; max-width: 95% !important; margin: 5% auto !important; padding: 15px !important; overflow-y: auto !important; max-height: 90vh !important; }
        .details-grid, .info-grid, .data-grid { display: flex !important; flex-direction: column !important; gap: 15px !important; }
        .detail-item, .info-item { display: flex !important; flex-direction: column !important; width: 100% !important; }
        .modal-body div[style*="display: flex"] { flex-wrap: wrap !important; }
        .map-search-bar { flex-direction: column !important; }
        .map-modal-footer { flex-direction: column-reverse !important; gap: 10px !important; }
        .map-modal-footer .btn { width: 100% !important; margin: 0 !important; }
        .map-modal-header { flex-wrap: nowrap !important; }
        
        /* Tables */
        .table-container, .table-responsive { width: 100% !important; max-width: 100% !important; overflow-x: auto !important; -webkit-overflow-scrolling: touch; }
        td, th { white-space: nowrap !important; }
        
        /* Form Wrapping */
        .form-group, .panel-header, div[style*="display: flex"], div[style*="display:flex"] { flex-wrap: wrap !important; }
        input, select, textarea, .form-control, .custom-dropdown { width: 100% !important; max-width: 100% !important; margin-left: 0 !important; }
        .form-group { width: 100% !important; max-width: 100% !important; padding: 0 !important; margin: 0 !important; }
        .form-grid { display: flex !important; flex-direction: column !important; width: 100% !important; }
        
        /* Buttons */
        .btn { width: 100% !important; white-space: normal !important; margin-bottom: 8px !important; }
        .close-map-btn { width: auto !important; padding: 10px !important; }
        .filter-tabs { display: flex !important; flex-wrap: wrap !important; gap: 5px !important; }
        .filter-tab { flex: 1 1 auto !important; text-align: center !important; }
        .panel-body > div[style*="display: flex"], .panel-body > div[style*="display:flex"] { flex-direction: column !important; gap: 15px !important; }
        .panel-body button { width: 100% !important; margin-top: 10px !important; }
        
        /* Headers & Nav */
        .header-search { display: none !important; }
        .top-header { padding: 0 16px !important; height: auto !important; min-height: 72px !important; display: flex !important; flex-wrap: nowrap !important; justify-content: space-between !important; align-items: center !important; }
        .mobile-toggle { margin-right: 15px !important; display: block !important; flex-shrink: 0 !important; }
        .user-profile { padding-left: 10px !important; border-left: none !important; }
        .notification-wrapper { flex-shrink: 0 !important; }
        .notification-dropdown { position: fixed !important; top: 72px !important; left: 5% !important; width: 90% !important; max-width: none !important; right: auto !important; z-index: 9999 !important; }
    }
'''

                # Strategy: 
                # 1. We wipe all previous CSS patches based on the starting signature /* CareReach Universal Mobile Patch */ dynamically.
                # Actually, we can just use git checkout to restore ALL HTML files, then inject this master block EXACTLY ONCE!
                # That is 1000x safer and guaranteed to permanently kill the rogues.
                pass

rewrite_css_perfectly()
