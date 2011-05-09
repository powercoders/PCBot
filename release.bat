@ECHO OFF

SETLOCAL

SET Sz=7z
SET advz=advzip

CALL "%Sz%" > NUL 2>NUL
IF NOT "%ERRORLEVEL%"=="0" GOTO :error
CALL "%advz%" > NUL 2>NUL
IF NOT "%ERRORLEVEL%"=="0" GOTO :error

CALL make.bat
ECHO Compressing release

SET name=RSBot
SET dist=%name%.jar
FOR /F %%G IN (resources\version.txt) DO SET version=%%G
SET tmp=temp
SET Path=%Path%;%ProgramFiles%\7-Zip

RMDIR /S /Q "%tmp%" 2>NUL
MKDIR "%tmp%"
CD "%tmp%"
CALL "%Sz%" x "..\%dist%" > NUL
DEL /F /Q "..\%dist%"
CALL "%Sz%" a -tzip "%dist%" . -mx=9 -mtc=off > NUL
CALL "%advz%" -z -4 "%dist%" > NUL
CD ..
MOVE /Y "%tmp%\%dist%" "%name%-%version%.jar" > NUL
RMDIR /S /Q "%tmp%" 2>NUL
GOTO :eof

:error
ECHO You do not have 7-zip installed and/or advzip available.
GOTO :eof
