#!/usr/bin/env bash
cd "$( dirname "${BASH_SOURCE[0]}" )"
RED='\033[0;31m'
GREEN='\033[1;32m'
NC='\033[0m'
TICK="${GREEN}\xE2\x9C\x94${NC}"
CROSS="${RED}\xE2\x9D\x8C${NC}"
for file in *
do
  if [ ${file#*.} == "uml" ] 
  then
    java -jar plantuml.jar $file
    if [[ $? != 0 ]]
    then
      echo -e "$CROSS $file"
      exit 0
    else
      echo -e "$TICK $file"
    fi
  fi
done
echo done