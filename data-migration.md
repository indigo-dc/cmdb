#Data migration 

In order to migrate database between successive CMDB versions please use `couch-migrate` located in github:

[https://github.com/bwilk/couch-migrate](https://github.com/bwilk/couch-migrate)


###System requirements:
```
ruby
gem
bundler
```

###Installation
```
git clone https://github.com/bwilk/couch-migrate.git
cd couch-migrate
bundle install
gem build couch_migrate.gemspec
gem install couch_migrate-*.gem
```

###Execution

In order to get command line options please run following command

```
couch_migrate -h
```

######Example 1
Migration from `database_v2` to `database_v3` located on the same machine and accessible via default couchdb port: 5984.
```
couch_migrate -i database_v2 -o database_v3
```
######Example 2
Migration from `database_v2` to `database_v3` between `machine_1` and `machine_2`

```
couch_migrate -i database_v2 -o database_v3 -s http://machine_1:5984 -t http://machine_2:5984
```











