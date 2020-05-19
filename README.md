# hal-liberator-jaeger
Example Jaeger distributed tracing using Liberator and HAL

## Running

```docker-compose up```


## Generating traces

 Discovery: 
- http://localhost:3031/
- http://localhost:3032/

Cross-service span: 
- http://localhost:3031/orders/123


## Jaeger

- Dependencies Graph http://localhost:3007/dependencies
- Traces http://localhost:3007/search
