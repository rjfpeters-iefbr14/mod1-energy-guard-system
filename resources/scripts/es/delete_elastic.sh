#!/bin/bash

curl -XDELETE "http://localhost:5601/api/index_patterns/index_pattern/logstash" -H 'kbn-xsrf: true'

curl -XDELETE "http://localhost:9200/_index_template/logs-docker"

curl -XDELETE "http://localhost:9200/_component_template/logs-docker-settings"

curl -XDELETE "http://localhost:9200/_component_template/logs-docker-mappings"

curl -XDELETE "http://localhost:9200/_ingest/pipeline/json_extractor"

curl -XDELETE "http://localhost:9200/_ilm/policy/logs-docker"
