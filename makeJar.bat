echo off
cd class
jar.exe -cvf TaskManager.jar TaskManager.class lab 
cd ..
copy /Y class\TaskManager.jar TaskManager.jar
del class\TaskManager.jar
pause