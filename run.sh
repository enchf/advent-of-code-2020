#!/bin/bash

KT_CLASSPATH=

while IFS= read -r -d '' ktfile; do KT_CLASSPATH="${KT_CLASSPATH} ${ktfile}"; done < <(find . -name "*.kt" -print0)

echo "${KT_CLASSPATH}"
