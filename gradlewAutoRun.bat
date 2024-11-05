@echo off
echo Gradlew Auto Run w/ Modified gradlew.bat
echo Created by VvSeanGtvV
start /wait gradlew jar
echo Build Complete!
set modsLocation=C:\Users\user\AppData\Roaming\Mindustry\mods
set buildLocation=D:\user\Documents\GitHub\Project-Restoration\build\libs\Project-RestorationDesktop.jar
set mindustry=D:\user\Downloads\mindustry-windows-64-bit\Mindustry.exe
echo set modsLocation=%modsLocation%
echo set buildLocation=%buildLocation%
copy %buildLocation% %modsLocation%
echo Replacement Complete!
echo autorun to %mindustry%
%mindustry%
exit