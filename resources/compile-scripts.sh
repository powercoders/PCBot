cd ~/RSBot
if [ ! -e "Settings/path.txt" ]; then
	echo "Path file does not exist. Please run RSBot and try again."
	exit
fi
for file in Scripts/Sources/*.java
do
    if [ ! -e "${file}" ]; then
    	echo "No .java script source files found."
    	exit
    fi
done
echo "Compiling scripts"
for file in Scripts/Sources/*.class
do
    if [ -e "${file}" ]; then
    	rm -R Scripts/Sources/*.class
    	break
    fi
done
"javac" -cp "/$(cat Settings/path.txt)" Scripts/Sources/*.java