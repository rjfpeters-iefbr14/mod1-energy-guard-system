echo "\nStart Order Coffee Process"
curl --location --silent --output --request POST 'http://localhost:8093/resources/process-definition/key/order-coffee/start' \
  --header 'Content-Type: application/json' \
  --data-raw '{
     "variables": {
         "order": {
             "value": "Espresso",
             "type": "String"
        }
    }
}'
