#!/bin/bash

if [ -z "$(ps -ef | grep java | grep -v grep)" ]
then
	echo "Application not running"
else 
	kill `ps -ef | grep java | grep -v grep | awk '{ print $2 }'` > /dev/null 2>&1
fi
