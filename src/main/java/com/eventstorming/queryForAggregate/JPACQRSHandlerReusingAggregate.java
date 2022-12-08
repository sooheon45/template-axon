
forEach: View
representativeFor: View
fileName: {{namePascalCase}}CQRSHandlerReusingAggregate.java
path: {{boundedContext.name}}/{{{options.packagePath}}}/query
mergeType: template
except: {{contexts.isNotQueryForAggregate}}
---

package {{options.package}}.query;


import {{options.package}}.event.*;
import {{options.package}}.aggregate.*;

import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import java.io.IOException;

@Service
@ProcessingGroup("{{nameCamelCase}}")
public class {{namePascalCase}}CQRSHandlerReusingAggregate {

//<<< EDA / CQRS
    @Autowired
    private {{aggregate.namePascalCase}}ReadModelRepository repository;

    @QueryHandler
    public List<{{aggregate.namePascalCase}}ReadModel> handle({{namePascalCase}}Query query) {
        return repository.findAll();
    }

    @QueryHandler
    public Optional<{{aggregate.namePascalCase}}ReadModel> handle({{namePascalCase}}SingleQuery query) {
        return repository.findById(query.get{{contexts.keyField}}());
    }

{{#aggregate.events}}
    {{#if isCreateEvent}}
    @EventHandler
    public void when{{namePascalCase}}_then_CREATE ({{namePascalCase}}Event event) throws Exception{
        {{../aggregate.namePascalCase}}ReadModel entity = new {{../aggregate.namePascalCase}}ReadModel();
        {{../aggregate.namePascalCase}}Aggregate aggregate = new {{../aggregate.namePascalCase}}Aggregate();
        aggregate.on(event);

        BeanUtils.copyProperties(aggregate, entity);
        
        repository.save(entity);

    }
        {{else}}

    @EventHandler
    public void when{{namePascalCase}}_then_UPDATE ({{namePascalCase}}Event event) throws Exception{
        repository.findById(event.get{{../contexts.keyField}}())
            .ifPresent(entity -> {

                {{../aggregate.namePascalCase}}Aggregate aggregate = new {{../aggregate.namePascalCase}}Aggregate();
       
                BeanUtils.copyProperties(entity, aggregate);
                aggregate.on(event);
                BeanUtils.copyProperties(aggregate, entity);

                repository.save(entity);

            });

    }
    {{/if}}
{{/aggregate.events}}

//>>> EDA / CQRS
}

<function>
 
this.aggregate = this.boundedContext.aggregates[0];

this.contexts.isNotQueryForAggregate = (this.dataProjection != "query-for-aggregate")

if(!this.contexts.isNotQueryForAggregate){

    this.contexts.keyField = "Long";
    var me = this;
    this.aggregate.aggregateRoot.fieldDescriptors.forEach(fd => {if(fd.isKey) me.contexts.keyField=fd.namePascalCase});
    this.aggregate.events.forEach(event => {
        if(event.incomingCommandRefs)
            event.incomingCommandRefs.forEach(commandRef => {
                if(commandRef.value && commandRef.value.isRestRepository && commandRef.value.restRepositoryInfo.method == "POST"){
                    event.isCreateEvent = true;
                }
            })
    });

}

</function>