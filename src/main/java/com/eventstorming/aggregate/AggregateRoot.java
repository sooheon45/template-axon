.forEach: Aggregate
fileName: {{namePascalCase}}Aggregate.java
path: {{boundedContext.name}}/{{{options.packagePath}}}/aggregate
---
package {{options.package}}.aggregate;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import static org.axonframework.modelling.command.AggregateLifecycle.*;
import org.axonframework.spring.stereotype.Aggregate;

import org.springframework.beans.BeanUtils;
import java.util.List;
import java.util.UUID;

import lombok.Data;
import lombok.ToString;

{{#checkDateType aggregateRoot.fieldDescriptors}} {{/checkDateType}}
{{#checkBigDecimal aggregateRoot.fieldDescriptors}} {{/checkBigDecimal}}

import {{options.package}}.command.*;
import {{options.package}}.event.*;
import {{options.package}}.query.*;


//<<< DDD / Aggregate Root
@Aggregate
@Data
@ToString
public class {{namePascalCase}}Aggregate {

    {{#aggregateRoot.fieldDescriptors}}
    {{#isKey}}
    @AggregateIdentifier
    {{/isKey}}
    private {{{className}}} {{nameCamelCase}};
    {{/aggregateRoot.fieldDescriptors}}

    public {{namePascalCase}}Aggregate(){}

    {{#commands}}
    @CommandHandler
    {{#if (isRepositoryPost this)}}
    public {{../namePascalCase}}Aggregate({{namePascalCase}}Command command){
    {{else}}
    public void handle({{namePascalCase}}Command command){
    {{/if}}

        {{#triggerByCommand}}
        {{eventValue.namePascalCase}}Event event = new {{eventValue.namePascalCase}}Event();
        BeanUtils.copyProperties(command, event);     

        {{#if (isRepositoryPost ../this)}}
        //TODO: check key generation is properly done
        if(event.get{{@root.aggregateRoot.keyFieldDescriptor.namePascalCase}}()==null)
            event.set{{@root.aggregateRoot.keyFieldDescriptor.namePascalCase}}(createUUID());
        {{/if}}

        apply(event);

        {{#relationCommandInfo}}
        {{#commandValue}}
        //Following code causes dependency to external APIs
        // it is NOT A GOOD PRACTICE. instead, Event-Policy mapping is recommended.


   

        {{options.package}}.external.{{aggregate.namePascalCase}} {{aggregate.nameCamelCase}} = new {{options.package}}.external.{{aggregate.namePascalCase}}();
        // mappings goes here
        {{relationCommandInfo.boundedContext.namePascalCase}}Application.applicationContext.getBean({{options.package}}.external.{{aggregate.namePascalCase}}Service.class)
        .{{nameCamelCase}}({{aggregate.nameCamelCase}});
        {{/commandValue}}
        {{/relationCommandInfo}}
        {{/triggerByCommand}}
    }

    {{/commands}}

//<<< Etc / ID Generation
    private String createUUID() {
        return UUID.randomUUID().toString();
    }
//>>> Etc / ID Generation

    {{#policies}}

//<<< Clean Arch / Port Method
    
    @CommandHandler
    public void handle({{namePascalCase}}Command command){
        {{#triggerByCommand}}
        {{eventValue.namePascalCase}}Event event = new {{eventValue.namePascalCase}}Event();
        BeanUtils.copyProperties(this, event);     
        apply(event);

        {{#relationCommandInfo}}
        {{#commandValue}}
        //Following code causes dependency to external APIs
        // it is NOT A GOOD PRACTICE. instead, Event-Policy mapping is recommended.

        {{options.package}}.external.{{aggregate.namePascalCase}} {{aggregate.nameCamelCase}} = new {{options.package}}.external.{{aggregate.namePascalCase}}();
        // mappings goes here
        {{relationCommandInfo.boundedContext.namePascalCase}}Application.applicationContext.getBean({{options.package}}.external.{{aggregate.namePascalCase}}Service.class)
        .{{nameCamelCase}}({{aggregate.nameCamelCase}});
        {{/commandValue}}
        {{/relationCommandInfo}}
        {{/triggerByCommand}}
    }
//>>> Clean Arch / Port Method

    {{/policies}}



//<<< EDA / Event Sourcing

    {{#events}}
    
    @EventSourcingHandler
    public void on({{namePascalCase}}Event event) {

        {{#isCreationEvent}}
        BeanUtils.copyProperties(event, this);
        {{/isCreationEvent}}

        //TODO: business logic here

    }

    {{/events}}
//>>> EDA / Event Sourcing


}
//>>> DDD / Aggregate Root


<function>



window.$HandleBars.registerHelper('jp', function (jsonPath) {
    var evaluatedVal = window.jp.query(this, jsonPath);

    return evaluatedVal;
});

window.$HandleBars.registerHelper('isCreationEvent', function (options) {
    if(this.incomingCommandRefs)
    for(var i=0; i<this.incomingCommandRefs.length; i++)
        if(checkCommandIsRepositoryPost(this.incomingCommandRefs[i].value)) return options.fn(this);

    return options.inverse(this);
});

window.$HandleBars.registerHelper('checkDateType', function (fieldDescriptors) {
    for(var i = 0; i < fieldDescriptors.length; i ++ ){
        if(fieldDescriptors[i] && fieldDescriptors[i].className == 'Date'){
            return "import java.util.Date; \n"
        }
    }
});

window.$HandleBars.registerHelper('keys', function (data) {
    return (Object.keys(data).join(", "));

});


window.$HandleBars.registerHelper('checkBigDecimal', function (fieldDescriptors) {
    for(var i = 0; i < fieldDescriptors.length; i ++ ){
        if(fieldDescriptors[i] && fieldDescriptors[i].className.includes('BigDecimal')){
            return "import java.math.BigDecimal;";
        }
    }
});

// changed

  var checkCommandIsRepositoryPost = function (command) {
    return (command.isRestRepository && command.restRepositoryInfo.method == "POST")
  };

  window.$HandleBars.registerHelper('isRepositoryPost', checkCommandIsRepositoryPost);
</function>
