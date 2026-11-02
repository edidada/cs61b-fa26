"""Fetch and verify the exact official course algs4 blob; no local compilation."""
import hashlib, os, subprocess, urllib.request
from pathlib import Path
root=Path(__file__).resolve().parents[1]
blob='e1703efb7a09c244fdfb0ee3d08aa7c28e1b408c'
request=urllib.request.Request('https://raw.githubusercontent.com/Berkeley-CS61B/library-fa26/7d9a380f3d881630a8cbf6382945a2d27618e728/algs4.jar',headers={'User-Agent':'cs61b-course-dependencies'})
with urllib.request.urlopen(request,timeout=60) as response:
    data=response.read()
assert hashlib.sha1(b'blob '+str(len(data)).encode()+b'\0'+data).hexdigest()==blob, 'Official jar hash mismatch'
folder=root/'.course-libs'; folder.mkdir(exist_ok=True)
jar=folder/'algs4.jar'; jar.write_bytes(data)
subprocess.run(['mvn.cmd' if os.name=='nt' else 'mvn','-B','org.apache.maven.plugins:maven-install-plugin:3.1.3:install-file','-Dfile='+str(jar),'-DgroupId=edu.princeton.cs','-DartifactId=algs4','-Dversion=fa26-pinned','-Dpackaging=jar','-DgeneratePom=true'],check=True,cwd=root)
