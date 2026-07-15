module.exports = {
   type: "object",
   additionalProperties: true,
   list: {
      filters: {
         tenant_name: ["tenant_name"],
         service: ["service"],
         user_group: ["user_group"]
      },
      value: {
         tenant_id: "tenant_id",
         tenant_name: "tenant_name",
         user_group: "user_group"
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
         user_group: "user_group"
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
      user_group: {
         type: "string",
         required: false
      }
   }
}


