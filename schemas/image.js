module.exports = {
   type: "object",
   additionalProperties: false, 
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


