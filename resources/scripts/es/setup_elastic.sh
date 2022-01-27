#!/bin/bash

# Wait for Elasticsearch to start up before doing anything.
until curl -s http://localhost:9200/_cat/health -o /dev/null; do
    echo Waiting for Elasticsearch...
    sleep 1
done

# Wait for Kibana to start up before doing anything.
until curl -s http://localhost:5601/ -o /dev/null; do
    echo Waiting for Kibana...
    sleep 1
done

# see index management
curl -XPUT "http://localhost:9200/_settings" -H 'kbn-xsrf: true' -H 'Content-Type: application/json' -d'
{
  "index" : {
    "number_of_replicas" : 0
  }
}'
echo -e "\nOne single node, so prevent health on all indexes yellow."

# shellcheck disable=SC2046
jq --raw-input -s -nc --slurpfile j ./init/json-extractor-script.json '$j[] | .processors[0].script.source = (input | tostring)' <<< $(cat ./init/json-extractor.script) |
curl -XPUT "http://localhost:9200/_ingest/pipeline/logs-docker" -H 'Content-Type: application/json' -d @-
echo -e "\nLoading pipeline ingest chain..."

curl -XPUT "http://localhost:9200/_ilm/policy/logs-docker" -H 'Content-Type: application/json' -d @./init/logs-docker-ilm.json
echo -e "\nLoading policy ingest chain..."

curl -XPUT "http://localhost:9200/_component_template/logs-docker-settings" -H 'Content-Type: application/json' -d @./init/logs-docker-settings.json
echo -e "\nLoading settings ingest chain..."

curl -XPUT "http://localhost:9200/_component_template/logs-docker-mappings" -H 'Content-Type: application/json' -d @./init/logs-docker-mappings.json
echo -e "\nLoading mapping ingest chain..."

curl -XPUT "http://localhost:9200/_index_template/logs-docker" -H 'Content-Type: application/json' -d @./init/logs-docker-template.json
echo -e "\nLoading template ingest chain..."

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

# Import Canvas
curl -X POST "http://localhost:5601/api/saved_objects/_import?overwrite=true" -H "kbn-xsrf: true" \
 --form file=@./dashboard/camvas-workpad-observability-dashboard.ndjson
