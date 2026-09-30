const fs = require('fs');
const path = require('path');

const root = __dirname;
const www = path.join(root, 'www');

if (!fs.existsSync(www)) {
  fs.mkdirSync(www, { recursive: true });
}

function copyFile(src, dst) {
  fs.copyFileSync(src, dst);
  console.log('Copied:', path.relative(root, src), '->', path.relative(root, dst));
}

function copyDir(srcDir, dstDir) {
  if (!fs.existsSync(dstDir)) fs.mkdirSync(dstDir, { recursive: true });
  for (const item of fs.readdirSync(srcDir)) {
    const s = path.join(srcDir, item);
    const d = path.join(dstDir, item);
    if (fs.statSync(s).isDirectory()) {
      copyDir(s, d);
    } else {
      copyFile(s, d);
    }
  }
}

// Copy single files
['index.html', 'manifest.json', 'sw.js'].forEach(f => {
  const p = path.join(root, f);
  if (fs.existsSync(p)) copyFile(p, path.join(www, f));
});

// Copy icons directory
const iconsDir = path.join(root, 'icons');
if (fs.existsSync(iconsDir)) {
  copyDir(iconsDir, path.join(www, 'icons'));
}

console.log('www directory prepared successfully.');
