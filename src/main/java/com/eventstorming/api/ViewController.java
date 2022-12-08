




forEach: View
fileName: {{namePascalCase}}QueryController.java
path: {{boundedContext.name}}/{{{options.packagePath}}}/api
_except: {{contexts.isNotCQRS}}

---
package {{options.package}}.api;

import java.util.List;
import java.util.ArrayList;

import java.util.concurrent.CompletableFuture;

import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import {{options.package}}.query.*;


  {{#contexts.target}}
  
@RestController
public class {{@root.namePascalCase}}QueryController {

  private final QueryGateway queryGateway;


  public {{@root.namePascalCase}}QueryController(QueryGateway queryGateway) {
      this.queryGateway = queryGateway;
  }
  

  @GetMapping("/{{namePlural}}")
  public CompletableFuture findAll({{@root.namePascalCase}}Query query) {
      return queryGateway.query(query , ResponseTypes.multipleInstancesOf({{@root.contexts.readModelClass}}.class))
            
             .thenApply(resources -> {
                List modelList = new ArrayList<EntityModel<{{@root.contexts.readModelClass}}>>();
                
                resources.stream().forEach(resource ->{
                    modelList.add(hateoas(resource));
                });

                CollectionModel<{{@root.contexts.readModelClass}}> model = CollectionModel.of(
                    modelList
                );

                return new ResponseEntity<>(model, HttpStatus.OK);
            });
            

  }


  @GetMapping("/{{namePlural}}/{id}")
  public CompletableFuture findById(@PathVariable("id") {{@root.contexts.keyFieldClass}} id) {
    {{@root.namePascalCase}}SingleQuery query = new {{@root.namePascalCase}}SingleQuery();
    query.set{{@root.contexts.keyField}}(id);

      return queryGateway.query(query, ResponseTypes.optionalInstanceOf({{@root.contexts.readModelClass}}.class))
              .thenApply(resource -> {
                if(!resource.isPresent()){
                  return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
                }

                return new ResponseEntity<>(hateoas(resource.get()), HttpStatus.OK);
            }).exceptionally(ex ->{
              throw new RuntimeException(ex);
            });

  }

  EntityModel<{{@root.contexts.readModelClass}}> hateoas({{@root.contexts.readModelClass}} resource){
    EntityModel<{{@root.contexts.readModelClass}}> model = EntityModel.of(
        resource
    );

    model.add(
        Link
        .of("/{{namePlural}}/" + resource.get{{@root.contexts.keyField}}())
        .withSelfRel()
    );

    {{#@root.contexts.isNotCQRS}}
      {{#@root.contexts.target.commands}}
      {{#ifEquals isRestRepository false}}
          model.add(
              Link
              .of("/{{../namePlural}}/" + resource.get{{../../contexts.keyField}}() + "/{{controllerInfo.apiPath}}")
              .withRel("{{controllerInfo.apiPath}}")
          );
      {{/ifEquals}}
      {{/@root.contexts.target.commands}}
    {{/@root.contexts.isNotCQRS}}

    return model;
  }

  {{/contexts.target}}

}

<function>
this.contexts.isNotCQRS = this.dataProjection!="cqrs"

this.contexts.keyField = "Long";
this.contexts.keyFieldClass = "String";
var me = this;

if(this.dataProjection == "query-for-aggregate"){
  this.contexts.target = this.boundedContext.aggregates[0];
  this.contexts.readModelClass = this.contexts.target.namePascalCase + "ReadModel";

  this.contexts.target.aggregateRoot.fieldDescriptors.forEach(fd => {if(fd.isKey) {
      me.contexts.keyField=fd.namePascalCase;
      me.contexts.keyFieldClass=fd.className;
    }
  });

// alert(this.contexts.target.namePascalCase)
}else{
  this.contexts.target = this;
  this.contexts.readModelClass = this.contexts.target.namePascalCase;

  this.contexts.target.fieldDescriptors.forEach(fd => {if(fd.isKey) {
    me.contexts.keyField=fd.namePascalCase;
    me.contexts.keyFieldClass=fd.className;
  }
});

}


</function>