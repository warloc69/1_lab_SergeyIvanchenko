echo off
cd src
javadoc.exe -d docs lab lab.model lab.bridge lab.controller lab.view
cd ..
xcopy /D/E/Y src\docs docs\
rmdir /q/s src\docs
pause