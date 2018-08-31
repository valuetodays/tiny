@echo off

echo "starting"

java -cp ../lib/*;./* com.billy.jee.tinytomcat.core.server.SocketServer
pause

