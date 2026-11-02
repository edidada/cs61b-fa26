"""Exercise the actual course visualizer on a disposable local CI process."""
import json,subprocess,time,urllib.request,urllib.parse
from pathlib import Path
root=Path(__file__).resolve().parents[1]
log=root/'lab09/results/server.log';log.parent.mkdir(exist_ok=True)
with log.open('w',encoding='utf-8') as output:
    proc=subprocess.Popen(['mvn','-B','org.codehaus.mojo:exec-maven-plugin:3.5.0:java','-Dexec.mainClass=lab9main.Main','-Djava.awt.headless=true'],cwd=root/'lab09',stdout=output,stderr=subprocess.STDOUT)
    try:
        base='http://127.0.0.1:4567'
        for _ in range(120):
            if proc.poll() is not None: raise RuntimeError('Graph server exited; see server.log')
            try:
                with urllib.request.urlopen(base,timeout=2) as r:
                    assert r.status==200
                break
            except OSError: time.sleep(0.5)
        else: raise RuntimeError('Graph server did not become ready')
        def get(path):
            with urllib.request.urlopen(base+path,timeout=5) as r: return json.load(r)
        bfs=get('/order/'+urllib.parse.quote('Lab9BFS order')+'?start=Berkeley')
        assert bfs['order'][0]=='Berkeley' and len(bfs['order'])==15
        tree=get('/spt/Berkeley')
        assert tree['dists']['Boston']==3575
        assert tree['paths']['Seattle']==['Berkeley','Sacramento','Portland','Seattle']
        (log.parent/'http-results.json').write_text(json.dumps(dict(bfs=bfs,tree=tree),indent=2))
        print('Actual visualizer HTTP graph endpoints passed')
    finally:
        proc.terminate()
        try: proc.wait(timeout=10)
        except subprocess.TimeoutExpired: proc.kill();proc.wait()
