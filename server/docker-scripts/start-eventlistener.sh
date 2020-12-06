#!/bin/bash
command="java -jar eventlistener-server.jar"
if [[ -n "$CONF" ]]; then
  command="$command --spring.config.additional-location=$CONF"
fi

echo "Starting eventlistener with command: $command"
eval $command
