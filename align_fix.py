import os
import re

def fix_content_alignment():
    base_dir = r"CareReach-Frontend"
    
    for root, dirs, files in os.walk(base_dir):
        for file in files:
            if file.endswith('.html'):
                file_path = os.path.join(root, file)
                with open(file_path, 'r', encoding='utf-8') as f:
                    content = f.read()

                # Fix width: 100vw back to width: 100% to stop it from overflowing the right side
                if "width: 100vw !important;" in content and "content-area" in content:
                    content = re.sub(r'width: 100vw !important;(\s*)max-width: 100vw !important;', r'width: 100% !important;\1max-width: 100% !important;', content)
                    with open(file_path, 'w', encoding='utf-8') as f:
                        f.write(content)

fix_content_alignment()
