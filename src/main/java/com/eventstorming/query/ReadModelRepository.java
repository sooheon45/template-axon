forEach: View
fileName: {{namePascalCase}}Repository.java
path: {{boundedContext.name}}/{{{options.packagePath}}}/query
mergeType: template
except: {{contexts.isNotCQRS}}
---
package {{options.package}}.query;

import org.springframework.data.jpa.repository.JpaRepository;

//<<< API / HATEOAS
// import org.springframework.data.rest.core.annotation.RestResource;
// import org.springframework.data.rest.core.annotation.RepositoryRestResource;
//>>> API / HATEOAS


import java.util.List;

//@RepositoryRestResource(path = "{{namePlural}}", collectionResourceRel = "{{namePlural}}")
public interface {{namePascalCase}}Repository extends JpaRepository<{{namePascalCase}}, {{contexts.keyFieldClass}}> {

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

    {{#setFindBy updateRules namePascalCase}}{{/setFindBy}}

    {{#setDeleteBy deleteRules namePascalCase}}{{/setDeleteBy}}
}

<function>

this.contexts.isNotCQRS = this.dataProjection!="cqrs"//(this.dataProjection == "QUERY-FOR-AGGREGATE")

this.contexts.keyFieldClass = "Long";
var me = this;
this.fieldDescriptors.forEach(fd => {if(fd.isKey) me.contexts.keyFieldClass=fd.className});

window.$HandleBars.registerHelper('setFindBy', function (updateRules, name) {
    var text = "";
    try {
        if(updateRules.length > 0) {
            updateRules.forEach(function(rule) {
                if(rule.where.length > 0) {
                    rule.where.forEach(function(obj) {
                        if(!text.includes("findBy" + obj.viewField.namePascalCase)) {
                            if(!obj.viewField.isKey) {
                                text += "List<" + name + "> findBy" + obj.viewField.namePascalCase + "(" + obj.viewField.className + " " + obj.viewField.nameCamelCase + ");\n";
                            }
                        }
                    });
                }
            });
        }
        return text;
    } catch(e) {
        console.log(e)
    }
});

window.$HandleBars.registerHelper('setDeleteBy', function (deleteRules) {
    var text = "";
    try {
        if(deleteRules.length > 0) {
            deleteRules.forEach(function(rule) {
                if(rule.where.length > 0) {
                    rule.where.forEach(function(obj) {
                        if(!text.includes("deleteBy" + obj.viewField.namePascalCase)) {
                            if(!obj.viewField.isKey) {
                                text += "void"  + " deleteBy" + obj.viewField.namePascalCase + "(" + obj.viewField.className + " " + obj.viewField.nameCamelCase + ");\n";
                            }
                        }
                    });
                }
            });
        }
        return text;
    } catch(e) {
        console.log(e)
    }
});
</function>