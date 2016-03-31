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
            hostname: "hostname", 
            type: "type"
        }
    },
    properties: {
        type: {
            type: "string",
            required: true
        }
    }
}


