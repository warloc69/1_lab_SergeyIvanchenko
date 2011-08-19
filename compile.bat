rmdir /q/s class
mkdir class
javac.exe -Xlint -d class -sourcepath  src -classpath  sql\sqlite4java.jar  src\TaskManager.java
pause