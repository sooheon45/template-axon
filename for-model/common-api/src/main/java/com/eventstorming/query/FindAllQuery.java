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

