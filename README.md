# term-server-api-test
SNOMED CT Rest API endpoint querying/testing tool for master thesis. Uses threads to simulate many clients.

Supported servers: Snowstorm, SNOW OWL. Easily scalable to support more servers if required.

Supported API endpoint types: SNOMED CT, FHIR.

Supports lookup of concept properties including preferred terms (PTs), fully specified names (FSNs), ancestors, parents and descendants.
Supports searching for concepts and their id tags given a search expression.
Supports the FHIR endpoints of $lookup, $subsumption and $translate. More support is easily added, please submit an issue for any endpoint you would like support for.

There is another thread-less project intended for direct communication between servers and external software. This software can be found in the IntegrationTest zip file. However, it does not receive updates and bugfixes at the same rate as this repository does.
