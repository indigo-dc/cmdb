# monitoring-probes

This folder includes probes for monitoring the CMDB service.

## Simple health check

This probe tries to contact the CMDB.
The usage of the script is:
```
   health-check.sh [-h] -u URL [-t TIMEOUT] [-v]
```
where

 * -h :           prints this help
 * -u URL :       mandatory URL of the CMDB (e.g. https://indigo-paas.cloud.ba.infn.it/cmdb/)
 * -t TIMEOUT :   optional timeout after which the probe will be terminated
 * -v :           turns on the verbose mode


### Exit codes

The script returns

 * 0 if the request to the endpoint returns the expected 200 HTTP code
 * 2 in case of errors or exceeded timeout (default: 15 sec)

### Usage example

```
$ ./health-check.sh -u https://indigo-paas.cloud.ba.infn.it/cmdb/ -t 10 -v
```