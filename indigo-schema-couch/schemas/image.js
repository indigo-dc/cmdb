module.exports = {
   type: "object",
   additionalProperties: true, 
   list: {
      filters: {
         image_name: ["image_name"],
         tenant: ["tenant"]
      }, 
      value: {
         image_id: "image_id",
         image_name: "image_name"
      }
   },
   belongs_to: [{
      type: 'tenant',
      many_name: 'images',
      foreign_key: 'tenant_id',
      value: {
         image_id: "image_id", 
         image_name: "image_name",
         tenant: "tenant"
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
      tenant_id: {
         type: "string",
         required: true
      },
      tenant_name: {
         type: "string",
         required: false
      },
      service: {
         type: "string",
         required: false
      }
   }
}


