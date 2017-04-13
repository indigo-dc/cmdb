# CMDB Proxy [![Build Status](https://travis-ci.org/mkasztelnik/cmdb-proxy.svg?branch=master)](https://travis-ci.org/mkasztelnik/cmdb-proxy)

CMDB Proxy is a proxy to CMDB which adds authentication and authorization for
incoming requests. In the future more CMDB features can be added here (than most
probably this project will be renamed from `CMDB Proxy` into `CMDB`).

## Requirements

  - JVM >= 1.8

## Setting up a development instance

* Install Java by invoking the following:

```
sudo add-apt-repository ppa:webupd8team/java
sudo apt update
sudo apt install oracle-java8-installer
```

* Navigate to the project's directory
* Adjust local property overrides in `src/main/resources/config/application.properties`
* Run the following to execute a local instance of `cmdb-proxy`:

```
./mvnw spring-boot:run
```

## Configuring init.d start script

The final jar is init.d script compliant and can be used to manage application
deployment. This can be achieved by doing the following steps:

* copy the jar file to a known location `/path/cmdb-proxy.jar`,
* create a symlink in the `/etc/init.d` by executing
  `sudo ln -s /path/cmdb-proxy.jar /etc/inid.d/cmdb-proxy`,
* make sure that the owner of the jar file is a regular user.

Any properties can be overridden by placing an `application.properties`
file next to the jar file.
