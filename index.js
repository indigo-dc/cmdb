var schemaCouch = require('schema-couch');
var auth = null;

try {
    auth = require('./auth.js')
} catch (ex) {
}

loaded_callback = function(doc, cb) { 
  cb(null, doc); 
};

pushed_callback = function(err) { 
  if (err) console.log(err) 
};

//var url = "http://localhost:5984/indigo-cmdb-v2"
var url  = "http://" + (auth ? (auth.username + ":" + auth.password + "@" ) : "") + "couch.cloud.plgrid.pl/indigo-cmdb-v2"
schemaCouch(__dirname + '/schemas', url, loaded_callback, pushed_callback);
