module.exports = {
   type: "object",
   additionalProperties: true, 
   list: {
      filters: {
         tenant_name: ["tenant_name"],
         service: ["service"],
         iam_organisation: ["iam_organisation"]
      }, 
      value: {
         tenant_id: "tenant_id",
         tenant_name: "tenant_name",
         iam_organisation: "iam_organisation"
      }
   },
   has_many: [
      { name: 'images',  type: 'image' },
      { name: 'flavors',  type: 'flavor' }
   ],
   belongs_to: [{
      type: 'service',
      many_name: 'tenants',
      foreign_key: 'service',
      value: {
         tenant_id: "tenant_id", 
         tenant_name: "tenant_name",
         service: "service",
         iam_organisation: "iam_organisation"
      }
   }],
   properties: {
      tenant_id: { 
         type: "string",
         required: true
      },
      tenant_name: { 
         type: "string",
         required: true
      },
      service: {
         type: "string",
         required: true
      },
      iam_organisation: {
         type: "string",
         required: false
      }
   }
}


