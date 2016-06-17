module.exports = {
   type: "object",
   additionalProperties: false, 
   list: {
      filters: {
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
         required: false
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
      }
   }
}

