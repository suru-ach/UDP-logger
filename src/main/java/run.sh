#!/bin/bash

if [ "$#" -lt 3 ]; then
    echo "Usage: $0 args format <sever_executable> <client_executable> <client_instances>"
    exit 1
fi

server_executable=$1
client_executable=$2
client_instances=$3

echo "Running $server_executable"
java $server_executable &

for((i=1;i<=client_instances;i++)); do
    echo "Running $client_executable $i"
    gnome-terminal -- bash -c "java $client_executable" &
done

