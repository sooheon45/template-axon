
forEach: View
fileName: {{aggregate.namePascalCase}}ReadModelRepository.java
path: {{boundedContext.name}}/{{{options.packagePath}}}/query
mergeType: template
except: {{isNotQueryForAggregate}}
---
package {{options.package}}.query;

import org.springframework.data.jpa.repository.JpaRepository;

//<<< API / HATEOAS
// import org.springframework.data.rest.core.annotation.RestResource;
// import org.springframework.data.rest.core.annotation.RepositoryRestResource;
//>>> API / HATEOAS


import java.util.List;

//@RepositoryRestResource(path = "{{namePlural}}", collectionResourceRel = "{{namePlural}}")
public interface {{aggregate.namePascalCase}}ReadModelRepository extends JpaRepository<{{aggregate.namePascalCase}}ReadModel, {{contexts.keyFieldClass}}> {

//<<< API / HATEOAS
/*
    @Override
    @RestResource(exported = false)
    void delete(OrderStatus entity);

    @Override
    @RestResource(exported = false)
    void deleteAll();

    @Override
    @RestResource(exported = false)
    void deleteById(Long id);

    @Override
    @RestResource(exported = false)
     <S extends OrderStatus> S save(S entity);
*/
//>>> API / HATEOAS

}

<function>

this.aggregate = this.boundedContext.aggregates[0];
this.contexts.isNotQueryForAggregate = (this.dataProjection != "query-for-aggregate")

this.contexts.keyFieldClass = "Long";
var me = this;
this.aggregate.aggregateRoot.fieldDescriptors.forEach(fd => {if(fd.isKey) me.contexts.keyFieldClass=fd.className});

</function>