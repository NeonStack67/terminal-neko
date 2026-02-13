@echo off
cd src\main
javac -d ..\..\out cat\Main.java
cd ..\..\out
java cat.Main
pause