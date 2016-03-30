var schemaCouch = require('schema-couch');

loaded_callback = function(doc, cb) { 
  doc['rewrites'].unshift({
    "from": "/services",
    "to": "_rewrite/service/list",
    "query": { "include_docs": "true" }
  }); 
  doc['rewrites'].unshift({
    "from": "/sites",
    "to": "_rewrite/site/list",
    "query": { "include_docs": "true" }
  });
  cb(null, doc); 
};

pushed_callback = function(err) { 
  if (err) console.log(err) 
};

schemaCouch(__dirname + '/schemas', 'http://localhost:5984/indigo-cmdb', loaded_callback, pushed_callback);
