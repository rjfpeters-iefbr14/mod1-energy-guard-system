#!/bin/sh

export JAVA_TOOL_OPTIONS="-javaagent:elastic-apm-agent.jar"
export ELASTIC_APM_SERVER_URL=${ELASTIC_APM_SERVER_URL:-http://apm-server:8200}

exec java "${JAVA_OPTS}" \
     -Delastic.apm.service_name=@project.name@ \
     -Djava.security.egd=file:/dev/./urandom -jar app.jar "$@"
exit $?
