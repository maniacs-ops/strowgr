#!/usr/bin/env bash
CWD="$(dirname "$0")"
docker build -t 'strowgr/sidekick:0.2.4' $CWD/src/main/docker/sidekick
