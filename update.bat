@ECHO OFF 

IF EXIST .git GOTO :git
IF EXIST .svn GOTO :svn
ECHO You are not running this from a Git or SVN folder.
PAUSE
GOTO :eof

:git
ECHO Updating Git...
CALL git stash save
CALL git remote add internal_update https://github.com/powerbot/RSBot.git
CALL git fetch internal_update
CALL git merge internal_update/master -s recursive -X theirs
CALL git stash pop
CALL git gc --prune=now
GOTO :eof

:svn
CALL svn help > NUL 2>NUL
IF NOT "%ERRORLEVEL%"=="0" GOTO :svnerror
ECHO Updating SVN...
svn update
GOTO :eof

:svnerror
ECHO Subversion client not found.
ECHO If you are using TortoiseSVN, right click the folder and select 'SVN Update'.
PAUSE
GOTO :eof
