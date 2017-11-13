#!/bin/bash

# chkconfig: 2345 80 80
# description: The gameserver.

### BEGIN INIT INFO
# Provides:          game_service.sh
# Required-Start:    hostname $local_fs $syslog $network mysql
# Required-Stop:     hostname $local_fs $syslog $network mysql
# Default-Start:     2 3 4 5
# Default-Stop:      0 1 6
### END INIT INFO

# User must change these parameters to his/her own!
runAs=your_user_name
appHome="/home/${runAs}/l2junity-gameserver/game"
appScript=game_app.sh

# A function to manage your app.
function app() {
	if [ "x$USER" != "x$runAs" ]; then
		su - "$runAs" -c "cd $appHome && ./${appScript} $1"
	else
		"cd ${appHome} && ./${appScript} $1"
	fi
}

# User's choice.
app $1
exit 0