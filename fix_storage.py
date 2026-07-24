import os, re

src_dir = "C:/Workspace/JavaProjects/Mangrove/frontend/src"
pattern = re.compile(r"localStorage\.(getItem|setItem|removeItem)\('mangrove_(token|user)'")

count = 0
for root, dirs, files in os.walk(src_dir):
    for f in files:
        if f.endswith(('.vue', '.js')):
            path = os.path.join(root, f)
            try:
                with open(path, 'r', encoding='utf-8') as fp:
                    content = fp.read()
                new = pattern.sub(r"sessionStorage.\1('mangrove_\2'", content)
                if new != content:
                    with open(path, 'w', encoding='utf-8') as fp:
                        fp.write(new)
                    count += 1
                    print(f"  Fixed: {os.path.relpath(path, src_dir)}")
            except:
                pass

print(f"\nTotal files fixed: {count}")
