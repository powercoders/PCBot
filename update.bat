@ECHO OFF 

IF EXIST .git CALL :git
IF EXIST .svn CALL :svn
GOTO :eof

:git
ECHO Updating git...
CALL git stash save
CALL git remote add internal_update git://github.com/powerbot/RSBot.git
CALL git fetch internal_update
CALL git merge internal_update/master -s recursive -X theirs
CALL git stash pop
CALL git gc
GOTO :eof

:svn
ECHO Subversion update not supported yet
GOTO :eof
