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

