import fs from 'fs';
import path from 'path';
import { fileURLToPath } from 'url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const sourceDir = path.join(__dirname, 'dist');
const destDir = path.join(__dirname, '..', 'src', 'main', 'resources', 'static');

// Ensure destination directory exists
if (!fs.existsSync(destDir)) {
  fs.mkdirSync(destDir, { recursive: true });
}

// Clear destination directory
fs.readdirSync(destDir).forEach(file => {
  const curPath = path.join(destDir, file);
  fs.rmSync(curPath, { recursive: true, force: true });
});

// Copy files
const copyRecursiveSync = (src, dest) => {
  const exists = fs.existsSync(src);
  const stats = exists && fs.statSync(src);
  const isDirectory = exists && stats.isDirectory();
  if (isDirectory) {
    if (!fs.existsSync(dest)) {
      fs.mkdirSync(dest);
    }
    fs.readdirSync(src).forEach((childItemName) => {
      copyRecursiveSync(path.join(src, childItemName), path.join(dest, childItemName));
    });
  } else {
    fs.copyFileSync(src, dest);
  }
};

copyRecursiveSync(sourceDir, destDir);
console.log(`Successfully copied build from ${sourceDir} to ${destDir}`);
