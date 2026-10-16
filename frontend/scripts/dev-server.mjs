import http from 'node:http';
import { readFile } from 'node:fs/promises';
import { resolve, dirname, sep, extname } from 'node:path';
import { fileURLToPath } from 'node:url';
const root=resolve(dirname(fileURLToPath(import.meta.url)),'..');
const port=Number(process.env.FRONTEND_PORT||5173);
const backend=new URL(process.env.BACKEND_URL||'http://127.0.0.1:8080');
const types={'.html':'text/html; charset=utf-8','.css':'text/css; charset=utf-8','.js':'text/javascript; charset=utf-8','.svg':'image/svg+xml'};
http.createServer(async(req,res)=>{
  let pathname;
  try{pathname=decodeURIComponent(new URL(req.url,'http://localhost').pathname);}catch{res.writeHead(400);res.end();return;}
  if(pathname.startsWith('/api/')||pathname==='/actuator/health'){
    const proxy=http.request(new URL(req.url,backend),{method:req.method,headers:{...req.headers,host:backend.host}},upstream=>{
      res.writeHead(upstream.statusCode,upstream.headers);upstream.pipe(res);
    });
    proxy.on('error',()=>{res.writeHead(502,{'Content-Type':'application/json; charset=utf-8'});res.end(JSON.stringify({message:'Inicia el backend Spring Boot en el puerto 8080.'}));});
    req.pipe(proxy);return;
  }
  if(req.method!=='GET'&&req.method!=='HEAD'){res.writeHead(405);res.end();return;}
  const allowed=pathname==='/'||pathname==='/index.html'||pathname==='/styles.css'||pathname==='/favicon.svg'||pathname.startsWith('/src/');
  const file=resolve(root,'.'+(pathname==='/'?'/index.html':pathname));
  if(!allowed||!file.startsWith(root+sep)||!types[extname(file)]){res.writeHead(404);res.end();return;}
  try{
    const body=await readFile(file);
    res.writeHead(200,{'Content-Type':types[extname(file)],'Cache-Control':'no-store','X-Content-Type-Options':'nosniff'});
    res.end(req.method==='HEAD'?undefined:body);
  }catch{res.writeHead(404);res.end('Archivo no encontrado.');}
}).listen(port,'127.0.0.1',()=>console.log(`Frontend COBRA360: http://127.0.0.1:${port} · API → ${backend.origin}`));
