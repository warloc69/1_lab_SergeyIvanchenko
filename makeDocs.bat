echo off
cd src
javadoc.exe -d docs lab lab.model lab.model.bridge lab.model.observer lab.exception lab.conntroller lab.view
cd ..
xcopy /D/E/Y src\docs docs\
rmdir /q/s src\docs
pause