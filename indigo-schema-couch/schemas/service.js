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
      { name: 'images',  type: 'image' },
      { name: 'flavors',  type: 'flavor' },
      { name: 'tenants',  type: 'tenant' }
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


