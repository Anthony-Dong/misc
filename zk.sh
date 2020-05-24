#!/usr/bin/env bash

# run  docker zk，最好是4.0起步，可以使用container
docker run  -d -p2181:2181 --rm --name myzk zookeeper:3.4.13