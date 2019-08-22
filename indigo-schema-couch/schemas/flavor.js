module.exports = {
   type: "object",
   additionalProperties: true, 
   list: {
      filters: {
         flavor_name: ["flavor_name"],
         tenant: ["tenant"]
      }, 
      value: {
         flavor_id: "flavor_id",
         flavor_name: "flavor_name"
      }
   },
   belongs_to: [{
      type: 'tenant',
      many_name: 'flavors',
      foreign_key: 'tenant_id',
      value: {
         flavor_id: "flavor_id", 
         flavor_name: "flavor_name",
         tenant: "tenant"
      }
   }],
   properties: {
      flavor_id: { 
         type: "string",
         required: true
      },
      flavor_name: { 
         type: "string",
         required: true
      },
      ram: { 
         type: "integer",
         required: false
      },
      disk: { 
         type: "integer",
         required: false
      },
      num_vcpus: { 
         type: "integer",
         required: false
      },
      num_gpus: { 
         type: "integer",
         required: false
      },
      gpu_model: { 
         type: ["string", "null"],
         required: false
      },
      gpu_vendor: { 
         type: ["string", "null"],
         required: false
      },
      tenant_id: { 
         type: "string",
         required: true
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


