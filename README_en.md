# Summary

Tesla: An open-source high-performance microservice API gateway based on Netty.  Tesla offers an integrated and safe entry point for RESTful services, with all kinds of flexible and configurable plugins to fulfill various business requirements.

The project consists of two systems:  tesla-ops(the adminitration console) and tesla-gateway(tesla API gateway).  Developers and operations can publish APIs and configure endpoints on tesla-ops console.

* API publishing: logon to tesla-ops, find "API management" menu and configure your APIs to publish, so that the cleints can access your service endpoints.
* Client configuration：API clients can access published APIs through tesla-gateway. Logon to tesla-ops, find "Client Management" menu to register your client and setup access control.

tesla is fully integrated with "summerframework".  It supports "API sharding" across the service call chains, useful in gray-launch scenarios.

# Features

* Supports Dubbo RPC endpoints，convert HTTP requests to Dubbo service calls.
* Supports routing based-on SpringCloud service discovery, as well as direct service URLs.
* Supports Rate Limiting、Oauth2 authentication、Jwt authenticatio, Access Key authentication，IP blacklist, Cookie blacklist、URL blacklist，User-Agent filtering, and various other plugins
* Support circuitbreaker and fallback by resilience4j
* Supports API monitoring, integrated with micrometer to export API metrics to influxDB and grafana UI.
* Supports dynamic API routing rules
* Supports service parameter manipulation and URL rewrite
* With easy to use Admin dashboard(tesla-ops)
* Does not rely to Web container, developed based on Netty framework, out-performs Zuul, according to our benchmark
* Supports user-defined plugins
* Suppors Docker deployment

# List of Available Filters
* HTTP Header Manipulation Plugin: customize your request and response headers
* Authentication and Authorization Plugins: Supports JWT and Oauth2 authentication
* Rate Limiting Plugin: Set up rate limiting for each API, base on distributed-token-bucket algorithm
* API request body validation Plugin: validates request body using self-defined json-schema
* Groovy script Plugin: Inject groovy script at runtime, to execute extra business logic
* Mock API Plugin: Mock any API response. with this plug-in, requests are not routed to endpoints. The plugin renders response content with configured freemarker template.
* RPC protocol conversion Plugin: Convert an HTTP rquest to dubbo/GRPC backend service call, including service discovery, parameter transformation.
* URL-Rewrite Plugin: This plugin offers URL-rewriting rules by extracting incoming URL parameters and replace placeholders on endpoint URL
* Token Generatior Plugin: Generates JWT token from response content
* Custom Jar Upload Plugin: Hot deploy custom plugins at runtime.  When out-of-the-box plugins doesn't meet your requirements, use this filter to implement your own business logic.
* Service Aggregation Plugin: This plugin aggregates results from multiple endpoints into one response, in order to reduce client-server traffic.
* Auth Bypassing Plugin: bypass authentication for public APIs, such as /login, /logout, etc
* Signature Verification Plugin: Using request siging tools to validate the API signature, to ensure API request is not altered in transit. 
* Caching Plugin: return cached API results, according to provided caching rules
* API Body Transform Plugin: transform API request and response body, according to client spec, using freemarker scripts

For more plugin documentation: please refer to [PDF文档](https://github.com/ke-finance/tesla/blob/master/tesla-ops/src/main/resources/static/doc/TESLA%E6%8E%A5%E5%85%A5%E6%96%87%E6%A1%A3.pdf)

# Installation

 * mvn packaging and images
 
 ```
   # Tesla projects depends on SDK provided by tesla-auth project，so please install tesla-auth first
   > cd tesla-auth
   > mvn clean -DskipTests install
   
   # try it out with tesla-sample, an sample service that simulates a backend endpoint
   > cd ../tesla-sample
   > mvn clean -DskipTests package
   
   #package tesla
   > cd ..
   > mvn clean package
 ```
 
 * Deploy as docker containers
   under root directory
 
 ```
 docker-compose up -d
 ```
 
 ![avatar](docker.jpg)
 
 * Logon to tesla-ops console, default admin password is：admin/Password@1, to manage your APIs
 http://localhost:8080
 
 ![avatar](ops.jpg)
 
 * access tesla gateway and be routed to your backend services
  http://localhost:9000
