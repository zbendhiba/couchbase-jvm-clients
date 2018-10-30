# TODOs

Loosely keeping track of things:


## Requests


## Endpoint

 - add the negotiated hello UUID to the context, if available
 - implement endpoint logic
 - implement circuit breaker logic
 - test it all in unit tests
 - add integration test for circuit breaker (?)

## IO

 - add the negotiated hello UUID to the context, if available
 - add test coverage for the ever-incrementing opaque functionality
 - complete select bucket handler test case with error cases
 - add sasl handler unit tests for all mechs
 - add sasl client unit tests
 - add collection support with tests
 - add snappy support and then reenable

## Env
 
 - initialize a couchbase scheduler for reactor
 - use the reactor scheduler everyhwere we need it
 
 - make sure all changeable properties have suppliers!
 - allow to configure the env through certain config providers
 - make the UserAgent an actual object with parts and a toString
 - suppliers should be cacheable somehow
 
## Event Bus

 - add filtering mechanisms for the consumer
 - add "measure" every N invocations and if the queue fills up report the 
   latency for slow consumers. threshold?
 
## Logging

 - add log redaction functionality
 
## Integration tests

 - add support for the mock! as another managed cluster
 - Finish work to get an initial managed integration build going
 - add support for N nodes to bootstrap on managed
 - add annotations for at least version
 - add annotation for specific topology type
 
 
## Performance tests

 - integrate jmh with gradle
 - start a basic benchmark for the event bus