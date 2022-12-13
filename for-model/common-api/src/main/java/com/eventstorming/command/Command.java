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


