module.exports = {
   type: "object",
   additionalProperties: true,
   list: {
      filters: {
         sitename: ["sitename"], 
         type: ["type"], 
         sitename_type: ["sitename", "type"]
      },
      value: {
         sitename: "sitename",
         site_id: "site_id",
         hostname: "hostname", 
         type: "type"
      }
   },
   belongs_to: [{
      name: 'site',
      type: 'site',
      many_name: 'services',
      foreign_key: 'site_id', 
      value: {
         sitename: "sitename",
         site_id: "site_id",
         hostname: "hostname",
         type: "type"
      }

   }],
   properties: {
      type: {
         type: "string",
         required: true
      }
   }
}

