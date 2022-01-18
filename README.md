# Energy Contingency Management System Sample Project

[![Electrical Wires Grid Power](resources/banner/electrical-863402_1920.jpg)](https://pixabay.com/photos/electrical-wires-grid-power-863402/)


This sample project consists of the following functions/components
- Spring Boot: RESTfull API's:
    - Global API Exception Handler using ApiExceptionHandler to customize the API response for errors
    - API Logging using ApiLoggingFilterConfig to log API request and response
    - Consume RESTFul APIs provided by other services using @FeignClient
    - Boilerplate Code generation using Lombok annotations such as @Data, @Value, @ToString, @Getters, and @Setters
    - Unit and Integration Tests for Controller and Service Layer
    - Using media-type versioning on REST API
- Spring Boot: Camunda
  - timed-jobs
  - business handling BPMN Errors
  - kafka integration
  - dynamically creating camunda jobs with variables, based on REST api's
  - integration external rest-calls 
- Spring Cloud Configuration
  - configuration service
- JSON Schema generating:
  - json-schema to pojo
- Messaging Kafka:
  - zookeeper
  - service-registry
  - kafka (publish/subscribe)
- Persistence:
  - postgres (camunda)
  - mongo-database: persistence (grid-contingency-manager)
- Logging and Monitoring by ELK-stack:
  - fluentbit (docker-log-capturing)
  - elasticsearch
  - metric-beat
  - heart-beat
  - apm-server
  - kibana
- TestContainers
  - kafka
  - mongodb
  - postgresql


Prepared not yet implemented
- hashicorp vault: OAUTH secure access
- kubernetes deployment

Additional proof points
- camunda decision matrix
- design the flow for "solution fulfillment" in the component PCM 
- design / build for the component operational-control-registry  
- the use of avro-messages instead of json
- the use of json-ld as payload in REST interface 
- distributed tracing -> open-telemetry (native elastic-apm-java doesn't have support for mongodb and some kafka usage)
- sample kibana dash-boards 
- use oauth2 client flow to secure endpoints
- caching in grid-contingency-manager
- permutation testen
- chaos-engineering testen
- performance testen met gatling
