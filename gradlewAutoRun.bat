set stage=0
set builded=0
set copied=0
@echo off

:START
cls
echo Gradlew Auto Run w/ Modified gradlew.bat
echo Created by VvSeanGtvV [ V2 ]
if %builded%==0 echo ^> :buildJar [ PROGRESS ]
if %stage%==0 goto buildJar
if %builded%==1 echo ^> :buildJar [ COMPLETE ]
if %copied%==0 echo ^> :copyJar  [ PROGRESS ]
if %stage%==1 goto copyJar
if %copied%==1 echo ^> :copyJar  [ COMPLETE ]
if %stage%==2 goto executeMindustry

:buildJar
echo > :buildJar Started
start /wait /b gradlew jar
set stage=1
set builded=1
goto START

:copyJar
set modsLocation=C:\Users\user\AppData\Roaming\Mindustry\mods
set buildLocation=D:\user\Documents\GitHub\Project-Restoration\build\libs\Project-RestorationDesktop.jar
copy %buildLocation% %modsLocation%
echo set modsLocation=%modsLocation%
echo set buildLocation=%buildLocation%
set stage=2
set copied=1
goto START

:executeMindustry
set mindustry=D:\user\Downloads\mindustry-windows-64-bit\Mindustry.exe
echo autorun to %mindustry%
set stage=3
%mindustry%
exit