module.exports = {
   type: "object",
   additionalProperties: true, 
   list: {
      filters: {
         flavor_name: ["flavor_name"],
         service: ["service"]
      }, 
      value: {
         flavor_id: "flavor_id",
         flavor_name: "flavor_name"
      }
   },
   belongs_to: [{
      type: 'service',
      many_name: 'flavors',
      foreign_key: 'service',
      value: {
         flavor_id: "flavor_id", 
         flavor_name: "flavor_name",
         service: "service"
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
      service: {
         type: "string",
         required: true
      }
   }
}


