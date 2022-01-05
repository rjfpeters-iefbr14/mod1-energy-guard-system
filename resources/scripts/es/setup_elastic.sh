#!/bin/bash

# Wait for Elasticsearch to start up before doing anything.
until curl -s http://elasticsearch:9200/_cat/health -o /dev/null; do
    echo Waiting for Elasticsearch...
    sleep 1
done

# Wait for Kibana to start up before doing anything.
until curl -s http://kibana:5601/ -o /dev/null; do
    echo Waiting for Kibana...
    sleep 1
done

# shellcheck disable=SC2046
# not always stable, then just copy/paste
jq -Rn --slurpfile j ./init/json-extractor-script.json '$j[] | .processors[0].script.source = input' <<< $(tr -d '\n' < ./init/json-extractor.script) |
curl -XPUT "http://localhost:9200/_ingest/pipeline/json_extractor" -H 'Content-Type: application/json' -d @-
echo "Loading pipeline ingest chain..."

curl -XPUT "http://localhost:9200/_ilm/policy/logs-docker" -H 'Content-Type: application/json' -d @./init/logs-docker-ilm.json
echo "Loading policy ingest chain..."

curl -XPUT "http://localhost:9200/_component_template/logs-docker-settings" -H 'Content-Type: application/json' -d @./init/logs-docker-settings.json
echo "Loading settings ingest chain..."

curl -XPUT "http://localhost:9200/_component_template/logs-docker-mappings" -H 'Content-Type: application/json' -d @./init/logs-docker-mappings.json
echo "Loading mapping ingest chain..."

curl -XPUT "http://localhost:9200/_index_template/logs-docker" -H 'Content-Type: application/json' -d @./init/logs-docker-template.json
echo "Loading template ingest chain..."

# Create Kibana index pattern
curl -X POST "localhost:5601/api/index_patterns/index_pattern" -H 'kbn-xsrf: true' -H 'Content-Type: application/json' -d'
{
    "refresh_fields": true,
    "index_pattern": {
        "id" : "logstash",
        "title": "logstash-*",
        "timeFieldName": "@timestamp"
    }
}'

# Set default Kibana index pattern
curl -X POST "localhost:5601/api/index_patterns/default" -H 'kbn-xsrf: true' -H 'Content-Type: application/json' -d'
{
    "index_pattern_id": "logstash"
}
'
