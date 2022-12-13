forEach: Model
fileName: README.md
path: for-model
---
## How to run

- Run axon server and mysql firstly

```
cd infra
docker-compose up
```

## Build common API & Run each service

'''
cd common-api
mvn install
cd ..

{{#boundedContexts}}
cd {{name}}
mvn spring-boot:run
cd ..

{{/boundedContexts}}
'''

## Test RSocket APIs

- Download RSocket client
```
wget -O rsc.jar https://github.com/making/rsc/releases/download/0.4.2/rsc-0.4.2.jar
```
- Subscribe the stream
```
{{#boundedContexts}}
java -jar rsc.jar --stream  --route {{namePlural}}.all ws://localhost:8088/rsocket/{{namePlural}}

{{/boundedContexts}}
```

<function>

window.$HandleBars.registerHelper('ifEquals', function (arg1, arg2, options) {
    return (arg1 == arg2) ? options.fn(this) : options.inverse(this);
});

window.$HandleBars.registerHelper('ifContains', function (jsonPath, value, options) {
    
    var evaluatedVal = window.jp.query(this, jsonPath);
    if( evaluatedVal == value || evaluatedVal.includes(value)
        //(Array.isArray(evaluatedVal) && evaluatedVal.includes(value))
        //|| (typeof evaluatedVal === 'string' && evaluatedVal.con)    -->
    ){
        return options.fn(this)
    }else{
        return options.inverse(this)
    }

});


window.$HandleBars.registerHelper('importTypes', function (fieldDescriptors) {
    var imports = "";

    var typeMappings = {
        "Date": "java.util.Date",
        "BigDecimal": "java.math.BigDecimal"
    };

    if(fieldDescriptors)
    for(var i = 0; i < fieldDescriptors.length; i ++ ){
        if(fieldDescriptors[i]){
            var fullTypeName = typeMappings[fieldDescriptors[i].className];

            if(fullTypeName){
                imports += "import " + fullTypeName + ";\n";
                typeMappings[fieldDescriptors[i].className] = null;
            }
        } 
    }

    return imports;
});

  window.$HandleBars.registerHelper('websocketPort', function (httpPort) {
    return parseInt(httpPort) - 1000;
  })

  window.$HandleBars.registerHelper('breakpoint', function (value, value2) {
    if(value == value2)
        debugger;
  })


</function>