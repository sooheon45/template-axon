



forEach: Policy
fileName: {{namePascalCase}}Saga.java

path: {{boundedContext.name}}/{{{options.packagePath}}}/saga
except: {{#except fieldDescriptors}}{{/except}}

---
package {{options.package}}.saga;

import {{options.package}}.command.*;
import {{options.package}}.event.*;


import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Saga
@ProcessingGroup("{{namePascalCase}}Saga")
public class {{namePascalCase}}Saga {

    @Autowired
    private transient CommandGateway commandGateway;

    {{#fieldDescriptors}}
    private {{className}} {{nameCamelCase}};
    {{/fieldDescriptors}}

    {{#contexts.sagaEvents}}
    {{#if isStartSaga}}@StartSaga{{/if}}{{#if isEndSaga}}@EndSaga{{/if}}
    @SagaEventHandler(associationProperty = "#correlation-key")  
    public void on{{event.namePascalCase}}({{event.namePascalCase}}Event event){
        {{#command}}
        {{namePascalCase}}Command command = new {{namePascalCase}}Command();

        commandGateway.send(command){{#../compensateCommand}}
            .exceptionally(ex -> {

                {{namePascalCase}}Command {{nameCamelCase}}Command = new {{namePascalCase}}Command();
                //
                return commandGateway.send({{nameCamelCase}}Command);

            })
        
        {{/../compensateCommand}};
        {{/command}}
    }
    {{/contexts.sagaEvents}}

}


<function>

var eventByNames = []
var commandByNames = {}
this.outgoingCommandRefs.forEach(
    commandRef => {
        commandByNames[commandRef.name] = commandRef.value
    }
)

var me = this;
var i = 1;
var maxSeq = 0;
this.incomingEventRefs.forEach(
    eventRef => {
        var nameNumberPart = eventRef.name.replace(/\D/g, "");
//        alert(nameNumberPart);
        var sequence = parseInt(nameNumberPart); // i
        if(sequence > maxSeq) maxSeq = sequence;

        var commandSequence = sequence + 1;
        eventByNames[sequence] = {
            event: eventRef.value,
            command: commandByNames[commandSequence],
            compensateCommand: commandByNames[commandSequence+"'"],
            isStartSaga: sequence ==1,
            isEndSaga: false
        };
    }
)

eventByNames[maxSeq].isEndSaga = true;
//alert('x')
//alert(JSON.stringify(commandByNames))

this.contexts.sagaEvents = eventByNames; 

window.$HandleBars.registerHelper('except', function (fieldDescriptors) {
    return (fieldDescriptors && fieldDescriptors.length == 0);
});
</function>
