# CMDB documentation

Documentation of CMDB service 

This work is co-funded by the [EOSC-hub project](http://eosc-hub.eu/) (Horizon 2020) under Grant number 777536.
<img src="https://wiki.eosc-hub.eu/download/attachments/1867786/eu%20logo.jpeg?version=1&modificationDate=1459256840098&api=v2" height="24">
<img src="https://wiki.eosc-hub.eu/download/attachments/18973612/eosc-hub-web.png?version=1&modificationDate=1516099993132&api=v2" height="24">

## [Deployment](./deployment.md)

## [Data migration](./data-migration.md)

=======
# CMDB [![Build Status](https://travis-ci.org/indigo-dc/cmdb.svg?branch=master)](https://travis-ci.org/indigo-dc/cmdb)

Change management database (CMDB) is an REST API for managing information about business entities.
It is integrated with indigo iam, thus all requests need to pass Bearer JWT tokent in the authorization
header.

## Requirements

  - JVM >= 11

## Setting up a development instance

* Install Java by invoking the following:

```
sudo add-apt-repository ppa:webupd8team/java
sudo apt update
sudo apt install oracle-java9-installer
```

* Navigate to the project's directory
* Adjust local property overrides in `src/main/resources/config/application.properties`
* Run the following to execute a local instance of `cmdb`:

```
./mvnw spring-boot:run
```

## Configuring init.d start script

The final jar is init.d script compliant and can be used to manage application
deployment. This can be achieved by doing the following steps:

* copy the jar file to a known location `/path/cmdb.jar`,
* create a symlink in the `/etc/init.d` by executing
  `sudo ln -s /path/cmdb.jar /etc/inid.d/cmdb`,
* make sure that the owner of the jar file is a regular user.

Any properties can be overridden by placing an `application.properties`
file next to the jar file.

## Docker

Application can be also packed and started as docker container. To build docker image run following command:

```
./mvnw package docker:build
```

As a result `indigodatacloud/cmdb` will be build. Next, it can be started using following command:

```
docker run -e CMDB_CRUD_USERNAME=XXX -e CMDB_CRUD_PASSWORD=XXX -p 8080:8080 indigodatacloud/cmdb

```

Username and password parameters are mandatory, additional parameters which can be passed into
started container are as follow:

```
CMDB_TARGET_URL=http://indigo.cloud.plgrid.pl/cmdb
CMDB_CRUD_TARGET_URL=http://couch.cloud.plgrid.pl/indigo-cmdb-v2
CMDB_CRUD_USERNAME=FIXME
CMDB_CRUD_PASSWORD=FIXME
CMDB_CRUD_ADMIN_GROUP=cmdb-dev-admins
OIDC_USERINFO=https://iam-test.indigo-datacloud.eu/userinfo
```
