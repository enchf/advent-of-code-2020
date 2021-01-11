#!/bin/bash

KT_CLASSPATH=
while IFS= read -r -d '' ktfile; do KT_CLASSPATH="${KT_CLASSPATH} ${ktfile}"; done < <(find . -name "*.kt" -print0)

CLASS="_$1Kt"

# shellcheck disable=SC2086
kotlinc $KT_CLASSPATH -d execution.jar
# shellcheck disable=SC2086
kotlin -classpath execution.jar $CLASS
