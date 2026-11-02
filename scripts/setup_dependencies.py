"""Fetch and verify the exact official course algs4 blob; no local compilation."""
import base64, hashlib, json, os, subprocess, urllib.request
from pathlib import Path
root=Path(__file__).resolve().parents[1]
blob='e1703efb7a09c244fdfb0ee3d08aa7c28e1b408c'
request=urllib.request.Request('https://api.github.com/repos/Berkeley-CS61B/library-fa26/git/blobs/'+blob,headers={'User-Agent':'cs61b-course-dependencies'})
with urllib.request.urlopen(request,timeout=60) as response:
    data=base64.b64decode(json.load(response)['content'])
assert hashlib.sha1(b'blob '+str(len(data)).encode()+b'\0'+data).hexdigest()==blob, 'Official jar hash mismatch'
folder=root/'.course-libs'; folder.mkdir(exist_ok=True)
jar=folder/'algs4.jar'; jar.write_bytes(data)
subprocess.run(['mvn.cmd' if os.name=='nt' else 'mvn','-B','org.apache.maven.plugins:maven-install-plugin:3.1.3:install-file','-Dfile='+str(jar),'-DgroupId=edu.princeton.cs','-DartifactId=algs4','-Dversion=fa26-pinned','-Dpackaging=jar','-DgeneratePom=true'],check=True,cwd=root)
