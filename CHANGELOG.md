# Change Log
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/)
and this project adheres to [Semantic Versioning](http://semver.org/).

Please view this file on the master branch, on stable branches it's out of date.

## [Unreleased]

### Added
- Proxy (`/cmdb/*`) into CMDB based on smiley-http-proxy-servlet and spring boot (@mkasztelnik)
- Authentication integrated with Indigo IAM (@mkasztelnik)
- Hierarchical entity PDP (@mkasztelnik)
- Docker build for cmdb-proxy (@mkasztelnik)
- Proxy (`/cmdb-crud/*`) into raw CMDB CouchDB rest API (@mkasztelnik)
- Other than `GET` requests allowed only for `/cmdb-crud/*` requests (@mkasztelnik)
- CMDB CRUD actions authorization (@mkasztelnik)
- Add CMDB CRUD credentials into CRUD request (@mkasztelnik)
- Current user is added into new created root item when not present (@mkasztelnik)
- Cascade items delete using bulk update (@mkasztelnik)
- Add possibility to pass app properties using docker environment variables (@mkasztelnik)

### Changed

### Deprecated

### Removed

### Fixed

### Security

