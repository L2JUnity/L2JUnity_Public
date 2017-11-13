#!/bin/bash

# Application constants.
serviceName="l2junity_game"
appJar="l2junity-game.jar"
appLockFile=/tmp/${serviceName}
appOutputLog=./${serviceName}.log

# Color constants.
setColorGreen="\\033[1;32m"
setColorRed="\\033[1;31m"
setColorDefault="\\033[0;39m"

# Safety check, in case user wants to run the script from an inappropriate working directory.
if [ $(ls $(pwd) | grep -ci $appJar) -eq 0 ]; then
	echo "Before you run $appJar you must switch to the directory where it is placed!"
	exit 1
fi

# A general function to start the application.
# & is mandatory for this function, otherwise application won't go into the background.
function startApp() {
    # Linux basics.
    if [ $UID -eq 0 ]; then
		echo "Running the application with root user may cause lethal security issues. Please dedicate a user for your application."
		exit 1
    fi

    # Check whether lock file is present.
    if [ -f $appLockFile ]; then
		echo "Lock file is present, because service '${serviceName}' is already running."
		exit 1
    fi

    # Create lock file.
    touch $appLockFile

    # Inform the user.
    echo "Service '${serviceName}' is started in background."

    # Start the application finally.
    java -Xms1024m -Xmx1536m -jar $appJar > $appOutputLog 2>&1

    # If exit code is restart, then restart the application, otherwise bye-bye!
    if [ $? -eq 2 ]; then
		stopApp
		sleep 1
		startApp &
    else
		stopApp
    fi
}


# A general function to stop the application. Force kill is prohibited here!
function stopApp() {
    # Remove the lock file.
    rm -f $appLockFile

    # Get the application PID.
    local appPID=$(ps x | grep $appJar | grep -v grep | awk '{print $1}')

    # If PID exist, then kill the application.
    if [[ ! -z $appPID ]]; then
		kill $appPID
    fi

    # Inform the user.
    echo "Service '${serviceName}' is stopped."
}

# Check the status running of the application.
function statusApp() {
    # Get the application PID.
    local appPID=$(ps x | grep $appJar | grep -v grep | awk '{print $1}')

    # Inform user whether service is running or not.
    echo -ne "[ "
    if [[ ! -z $appPID ]]; then
		echo -ne $setColorGreen
		echo -ne "ONLINE"
		echo -ne $setColorDefault
		echo " ] Service '${serviceName}' is running."
    else
		echo -ne $setColorRed
		echo -ne "OFFLINE"
		echo -ne $setColorDefault
		echo " ] Service '${serviceName}' is NOT running."
		# In case life happens. Unexpected outage, etc...
		[ -f $appLockFile ] && echo "However the lock file '${appLockFile}' is present, possibly an unexpected shutdown event happened recently. Please use '${0} stop' to cleanup."
    fi
}

# A simple function to show the usage of this service.
function helpApp() {
    echo "Usage: $0 (start|stop|status|view_log)"
}

# User choice below.
case $1 in
    'start')
    	# Yes, as I said above, the & is intentional.
		startApp &
    ;;

    'stop')
		stopApp
    ;;

    'status')
		statusApp
    ;;
    
    'view_log')
    	# Displays the content of the application log.
    	cat $appOutputLog
    ;;
    
    *)
		helpApp
    ;;
esac
