"""The course explicitly asks TestJankyOomage to fail; verify its actual assertion failure."""
import subprocess,sys,xml.etree.ElementTree as ET
from pathlib import Path
root=Path(__file__).resolve().parents[1]
if sys.argv[1]=='maven':
    command=['mvn','-B','-pl','lab10','-Dtest=TestJankyOomage','-DexpectedFailureExclude=__no_exclusion__','test']
    report=root/'lab10/target/surefire-reports/TEST-TestJankyOomage.xml'
else:
    command=['gradle',':lab10:expectedFailure','--no-daemon']
    report=root/'lab10/build/test-results/expectedFailure/TEST-TestJankyOomage.xml'
result=subprocess.run(command,cwd=root)
suite=ET.parse(report).getroot()
assert suite.attrib['tests']=='1' and suite.attrib['failures']=='1' and suite.attrib['errors']=='0',suite.attrib
assert suite.find('testcase/failure') is not None
if sys.argv[1]=='maven': assert result.returncode != 0
else: assert result.returncode == 0
print('Expected pedagogical Janky assertion failure verified; no assertion was weakened')
