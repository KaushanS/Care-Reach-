import os

def fix_table_overflow():
    base_dir = r"CareReach-Frontend"
    
    table_css = '''
        /* Table Scroll Override */
        @media (max-width: 768px) {
            .table-container, .table-responsive {
                width: 100% !important;
                max-width: 100% !important;
                overflow-x: auto !important;
                margin-left: 0 !important;
                margin-right: 0 !important;
                padding: 0 !important;
                -webkit-overflow-scrolling: touch;
            }
            .panel {
                overflow: visible !important;
            }
            .panel-body {
                overflow-x: auto !important;
            }
            td, th {
                white-space: nowrap !important;
            }
            
            /* Stop buttons on Volunteer Dashboard from being completely squished */
            .filter-tabs {
                display: flex !important;
                flex-wrap: wrap !important;
                gap: 5px !important;
            }
            .filter-tab {
                flex: 1 1 auto !important;
                text-align: center !important;
            }
        }
    '''
    
    for root, dirs, files in os.walk(base_dir):
        for file in files:
            if file.endswith('.html'):
                file_path = os.path.join(root, file)
                with open(file_path, 'r', encoding='utf-8') as f:
                    content = f.read()

                if "Table Scroll Override" not in content and "</style>" in content:
                    content = content.replace("</style>", table_css + "\n</style>")
                    with open(file_path, 'w', encoding='utf-8') as f:
                        f.write(content)

fix_table_overflow()
