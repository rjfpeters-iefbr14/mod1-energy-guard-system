#!/bin/bash

curl --location --request POST 'http://localhost:18093/resources/contingencies' \
--header 'Content-Type: application/json' \
--data-raw '{
    "contingencyId": "6e125579-3e85-4983-b4e8-e680c95435ad",
    "contingencyName": "OS Hoofddorp"
}'


curl --location --request POST 'http://localhost:18093/resources/contingencies' \
--header 'Content-Type: application/json' \
--data-raw '{
    "contingencyId": "15769db8-dc0d-474e-a0bf-ee2a3c5942f8",
    "contingencyName": "DynT OS Hoofddorp - EAN:5241696869556 - CustomerId:30-439723-602764-8"
}'


