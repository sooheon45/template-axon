forEach: View
representativeFor: View
fileName: {{namePascalCase}}Query.java
path: common-api/{{{options.packagePath}}}/query
---
package {{options.package}}.query;

{{#ifContains "queryParameters[*].className" "Date"}}
import org.springframework.format.annotation.DateTimeFormat;
{{/ifContains}}

{{importTypes queryParameters}}

public class {{namePascalCase}}Query {

    {{#queryParameters}}
    {{#ifEquals className "Date"}}
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    {{/ifEquals}}
    {{{className}}} {{nameCamelCase}};
    {{/queryParameters}}
    
}

<function>


window.$HandleBars.registerHelper('importTypes', function (fieldDescriptors) {
    var imports = "";
debugger; 
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



</function>