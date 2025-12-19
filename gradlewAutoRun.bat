set stage=0
set builded=0
set copied=0
set modinternalName=Project-Restoration
set MindustryDir=D:\gombo\Documents\Mindustry
set MindustryVersion=154_2

set defLaunchFail=launchid.dat
set defLocation=%~dp0

set modsLocation=%APPDATA%\Mindustry\mods
set mindustry=%MindustryDir%\%MindustryVersion%.jar
@echo off


:START
cls
echo Loading from ^> %defLocation%
title Gradlew Auto Run w/ Modified gradlew.bat
echo Gradlew Auto Run w/ Modified gradlew.bat
echo Created by VvSeanGtvV [ 2.16 ]

echo Internal Mod Name ^> %modinternalName%
echo Mindustry Directory ^> %MindustryDir% 
echo Version Loading to ^> %MindustryVersion%

cd /d "%APPDATA%\Mindustry"
if exist "%defLaunchFail%" goto checkList
cd /d "%defLocation%"

if %builded%==0 echo ^> :buildJar [ PROGRESS ]
if %stage%==0 goto buildJar
if %builded%==1 echo ^> :buildJar [ COMPLETE ]
if %copied%==0 echo ^> :copyJar  [ PROGRESS ]
if %stage%==1 goto copyJar
if %copied%==1 echo ^> :copyJar  [ COMPLETE ]
if %stage%==2 goto executeMindustry

:checkList
set /p "choice=Mindustry has failed to launch | Force launch Mindustry? (Y/N): "

if /I "%choice%"=="Y" goto YES
if /I "%choice%"=="N" goto NO
goto START

:YES
del %defLaunchFail%
goto START

:NO
echo Aborting Batch process...
timeout /t 5 >nul
exit

:buildJar
echo > :buildJar Started
title [Gradlew]
START /WAIT gradlew jar
set stage=1
set builded=1
goto START

:copyJar
echo located %modsLocation%

cd build\libs\
copy %modinternalName%Desktop.jar %modsLocation%
if exist %modinternalName%Desktop.jar del %modinternalName%Desktop.jar
cd ..\..\

set stage=2
set copied=1
goto START

:executeMindustry
title Mindustry
echo autorun %mindustry%
set stage=3
%mindustry%
exit