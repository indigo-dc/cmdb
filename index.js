var schemaCouch = require('schema-couch-roles');
var c = null;

try {
    auth = require('./config.js')
} catch (ex) {
}

loaded_callback = function(doc, cb) { 
  cb(null, doc); 
};

pushed_callback = function(err) { 
  if (err) console.log(err) 
};

schemaCouch(__dirname + '/schemas', config.url, loaded_callback, pushed_callback);
