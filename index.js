var schemaCouch = require('schema-couch');

schemaCouch(__dirname + '/schemas', 'http://localhost:5984/cmdb-schema-couch', function(err, data){
  console.log(err, data);
});
