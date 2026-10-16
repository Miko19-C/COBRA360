import { readdir, readFile } from 'node:fs/promises';
import { resolve, dirname } from 'node:path';
import { fileURLToPath } from 'node:url';
import { spawnSync } from 'node:child_process';
const root=resolve(dirname(fileURLToPath(import.meta.url)),'..');
async function walk(directory){
  const files=[];
  for(const entry of await readdir(directory,{withFileTypes:true})){
    const file=resolve(directory,entry.name);
    if(entry.isDirectory())files.push(...await walk(file));
    else if(/\.(js|mjs)$/.test(entry.name))files.push(file);
  }
  return files;
}
const files=[...await walk(resolve(root,'src')),...await walk(resolve(root,'scripts'))];
const fileSet=new Set(files);
let checked=0;
const graph=new Map();
const exports=new Map();
const sources=new Map();
for(const file of files){
  const result=spawnSync(process.execPath,['--check',file],{encoding:'utf8'});
  if(result.status!==0)throw new Error(result.stderr);
  const source=await readFile(file,'utf8');
  sources.set(file,source);
  exports.set(file,new Set([...source.matchAll(/export\s+(?:async\s+)?(?:function|const|let|class)\s+([\w$]+)/g)].map(m=>m[1])));
  const dependencies=[];
  for(const match of source.matchAll(/from\s+['"]([^'"]+)['"]/g)){
    if(match[1].startsWith('.')&&!fileSet.has(resolve(dirname(file),match[1])))throw new Error(`Importación no encontrada: ${file} → ${match[1]}`);
    if(match[1].startsWith('.'))dependencies.push(resolve(dirname(file),match[1]));
  }
  checked++;
  graph.set(file,dependencies);
}
for(const [file,source] of sources){
  for(const match of source.matchAll(/import\s*\{([^}]+)\}\s*from\s*['"]([^'"]+)['"]/g)){
    if(!match[2].startsWith('.'))continue;
    const names=match[1].split(',').map(n=>n.trim().split(/\s+as\s+/)[0]).filter(Boolean);
    const target=resolve(dirname(file),match[2]);
    for(const name of names)if(!exports.get(target)?.has(name))throw new Error(`Exportación inexistente: ${name} en ${target}`);
  }
}
const visited=new Set(),visiting=new Set();
function checkCycles(file){
  if(visiting.has(file))throw new Error(`Dependencia circular en el frontend: ${file}`);
  if(visited.has(file))return;
  visiting.add(file);for(const dependency of graph.get(file)||[])checkCycles(dependency);
  visiting.delete(file);visited.add(file);
}
for(const file of files)checkCycles(file);
console.log(`OK: sintaxis, importaciones, exportaciones y ausencia de ciclos en ${checked} módulos JavaScript.`);
