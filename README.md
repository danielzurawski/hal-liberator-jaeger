# hal-liberator-jaeger
Example Jaeger distributed tracing using Liberator and HAL

## Running

```docker-compose up```


## Generating traces

 Discovery endpoints: 
- http://localhost:3031/
- http://localhost:3032/

Cross-service span: 
- http://localhost:3031/orders/123


## Jaeger 

- Traces http://localhost:3007/search

Trace                                           |  Timeline 
:-----------------------------------------------------:|:-------------------------:
![Trace Example](/trace.png?raw=true "Trace Example")  |  ![Timeline Example](/timeline.png?raw=true "Timeline Example")


- Dependencies Graph http://localhost:3007/dependencies
 
![Jaeger Graph](/graph.png?raw=true "Jaeger Graph")