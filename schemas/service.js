module.exports = {
    type: "object",
    additionalProperties: true,
    list: {
        filters: {
            sitename: ["sitename"]
        },
        value: {
            sitename: "sitename",
            hostname: "hostname", 
        }
    },
    properties: {
        type: {
            type: "string",
            required: true
        }
    }
}


