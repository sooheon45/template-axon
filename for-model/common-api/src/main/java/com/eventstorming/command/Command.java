


forEach: Command
fileName: {{namePascalCase}}Command.java
path: common-api/{{{options.packagePath}}}/command
---
package {{options.package}}.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;
{{#checkDateType aggregate.aggregateRoot.fieldDescriptors}} {{/checkDateType}}
{{#checkBigDecimal aggregate.aggregateRoot.fieldDescriptors}} {{/checkBigDecimal}}

import lombok.Data;
import lombok.ToString;
import java.util.List;
import {{options.package}}.query.*;

{{importTypes fieldDescriptors}}

@ToString
@Data
public class {{namePascalCase}}Command {

    {{#if (isRepositoryPost this)}}

    {{#aggregate.aggregateRoot.fieldDescriptors}}
    {{#isKey}}    
    //<<< Etc / ID Generation
    private {{className}} {{nameCamelCase}};  // Please comment here if you want user to enter the id directly
    //>>> Etc / ID Generation
    {{else}}
    private {{{className}}} {{nameCamelCase}};
    {{/isKey}}
    {{/aggregate.aggregateRoot.fieldDescriptors}}

    {{else}}

    {{#aggregate.aggregateRoot.fieldDescriptors}}
    {{#isKey}}
    @TargetAggregateIdentifier
    private {{className}} {{nameCamelCase}};
    {{/isKey}}
    {{/aggregate.aggregateRoot.fieldDescriptors}}

    {{#fieldDescriptors}}
        {{#isKey}}
            @Id
            //@GeneratedValue(strategy=GenerationType.AUTO)
        {{/isKey}}
        private {{className}} {{nameCamelCase}};
    {{/fieldDescriptors}}

    {{/if}}
}



<function>

window.$HandleBars.registerHelper('importTypes', function (fieldDescriptors) {
    var imports = "";

    var typeMappings = {
        "Date": "java.util.Date",
        "BigDecimal": "java.math.BigDecimal"
    };

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