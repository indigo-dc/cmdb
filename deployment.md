# Deployment

In order to deploy cmdb schema please make use of the code located in indigo cmdb repository `indigo-dc/cmdb`:

[https://github.com/indigo-dc/cmdb](https://github.com/indigo-dc/cmdb)

System requirements:

```
nodejs
npm
```

After cloning the repository please install the package:

```
npm install
```

#### Schema definition

To edit the schema itself navigate to `schemas` folder and make neccessary changes.
Sample schema definitions should look like:

##### image.js

```javascript
module.exports = {
   type: "object",
   additionalProperties: true, 
   list: {
      filters: {
         image_name: ["image_name"],
         service: ["service"]
      }, 
      value: {
         image_id: "image_id",
         image_name: "image_name"
      }
   },
   belongs_to: [{
      type: 'service',
      many_name: 'images',
      foreign_key: 'service',
      value: {
         image_id: "image_id", 
         image_name: "image_name",
         service: "service"
      }
   }],
   properties: {
      image_id: { 
         type: "string",
         required: true
      },
      image_name: { 
         type: "string",
         required: true
      },
      architecture: { 
         type: "string",
         required: false
      },
      type: { 
         type: "string",
         required: false
      },
      distribution: { 
         type: "string",
         required: false
      },
      version: { 
         type: "string",
         required: false
      },
      service: {
         type: "string",
         required: true
      }
   }
}
```

##### provider.js

```javascript
module.exports = {
   type: "object",
   additionalProperties: true, 
   list: {
      filters: {
         name: "name"
      }, 
      value: {
         name: "name"
      }
   },
   has_many: [
      { name: 'services',  type: 'service' }
   ],
}
```

##### service.js

```javascript
module.exports = {
   type: "object",
   additionalProperties: true,
   list: {
      filters: {
         sitename: ["sitename"], 
         type: ["type"], 
         sitename_type: ["sitename", "type"],
         provider_id: ["provider_id"]
      },
      value: {
         sitename: "sitename",
         provider_id: "provider_id",
         hostname: "hostname", 
         type: "type"
      }
   },
   has_many: [
      { name: 'images',  type: 'image' }
   ],
   belongs_to: [{
      type: 'provider',
      many_name: 'services',
      foreign_key: 'provider_id', 
      value: {
         sitename: "sitename",
         provider_id: "provider_id",
         hostname: "hostname",
         type: "type"
      }

   }],
   properties: {
      type: {
         type: "string",
         required: true
      },
      provider_id: { 
         type: "string",
         required: false
      }
   }
}
```

###Deployment execution
When the configuration is ready, one can deploy CMDB by running following command 

```
node index.js

```






