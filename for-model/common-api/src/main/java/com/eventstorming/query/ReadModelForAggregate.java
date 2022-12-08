
forEach: View
representativeFor: View
fileName: {{aggregate.namePascalCase}}ReadModel.java
path: common-api/{{{options.packagePath}}}/query
except: {{isNotQueryForAggregate}}
---
package {{options.package}}.query;

import javax.persistence.*;
import java.util.List;
import lombok.Data;
import java.util.Date;
import org.springframework.hateoas.server.core.Relation;


{{#aggregate}}

{{#checkBigDecimal aggregateRoot.fieldDescriptors}}{{/checkBigDecimal}}

@Entity
@Table(name="{{namePascalCase}}_table")
@Data
@Relation(collectionRelation = "{{namePlural}}")
{{#setDiscriminator aggregateRoot.entities.relations nameCamelCase}}{{/setDiscriminator}}
//<<< EDA / Read Model

public class {{namePascalCase}}ReadModel {{#checkExtends aggregateRoot.entities.relations namePascalCase}}{{/checkExtends}} {


    {{#aggregateRoot.fieldDescriptors}}
    {{^isVO}}{{#isKey}}
    @Id
    {{/isKey}}{{/isVO}}
    {{#isLob}}@Lob{{/isLob}}
    {{#if (isPrimitive className)}}{{#isList}}{{/isList}}{{/if}}
    {{#checkRelations ../aggregateRoot.entities.relations className isVO referenceClass ../aggregateRoot.entities}}{{/checkRelations}}
    {{#checkAttribute ../aggregateRoot.entities.relations ../name className isVO}}{{/checkAttribute}}
    private {{{className}}} {{nameCamelCase}};
    {{/aggregateRoot.fieldDescriptors}}


{{#aggregateRoot.operations}}
    {{#setOperations ../commands name}}
    {{#isOverride}}
    @Override
    {{/isOverride}}
    {{^isRootMethod}}
    public {{returnType}} {{name}}(){
        //
    }
    {{/isRootMethod}}
    {{/setOperations}}
{{/aggregateRoot.operations}}

{{/aggregate}}


}
//>>> EDA / Read Model

<function>
this.aggregate = this.boundedContext.aggregates[0];
this.contexts.isNotQueryForAggregate = (this.dataProjection != "query-for-aggregate")

window.$HandleBars.registerHelper('checkDateType', function (fieldDescriptors) {
    for(var i = 0; i < fieldDescriptors.length; i ++ ){
        if(fieldDescriptors[i] && fieldDescriptors[i].className == 'Date'){
        return "import java.util.Date; \n"
        }
    }
});

window.$HandleBars.registerHelper('checkBigDecimal', function (fieldDescriptors) {
    for(var i = 0; i < fieldDescriptors.length; i ++ ){
        if(fieldDescriptors[i] && fieldDescriptors[i].className.includes('BigDecimal')){
            return "import java.math.BigDecimal;";
        }
    }
});

window.$HandleBars.registerHelper('checkAttribute', function (relations, source, target, isVO) {
   try {
       if(typeof relations === "undefined"){
        return;
        }

        if(!isVO){
            return;
        }

        var sourceObj = [];
        var targetObj = [];
        var sourceTmp = {};
        var targetName = null;
        for(var i = 0 ; i<relations.length; i++){
            if(relations[i] != null){
                if(relations[i].sourceElement.name == source){
                    sourceTmp = relations[i].sourceElement;
                    sourceObj = relations[i].sourceElement.fieldDescriptors;
                }
                if(relations[i].targetElement.name == target){
                    targetObj = relations[i].targetElement.fieldDescriptors;
                    targetName = relations[i].targetElement.nameCamelCase;
                }
            }
        }

        var samePascal = [];
        var sameCamel = [];
        for(var i = 0; i<sourceObj.length; i++){
            for(var j =0; j<targetObj.length; j++){
                if(sourceObj[i].name == targetObj[j].name){
                    samePascal.push(sourceObj[i].namePascalCase);
                    sameCamel.push(sourceObj[i].nameCamelCase);
                }
            }
        }

        var attributeOverrides = "";
        for(var i =0; i<samePascal.length; i++){
            var camel = sameCamel[i];
            var pascal = samePascal[i];
            var overrides = `@AttributeOverride(name="${camel}", column= @Column(name="${targetName}${pascal}", nullable=true))\n`;
            attributeOverrides += overrides;
        }

        return attributeOverrides;
    } catch (e) {
       console.log(e)
    }


});

window.$HandleBars.registerHelper('isPrimitive', function (className) {
    if(className.includes("String") || className.includes("Integer") || className.includes("Long") || className.includes("Double") || className.includes("Float")
            || className.includes("Boolean") || className.includes("Date")){
        return true;
    } else {
        return false;
    }
});

window.$HandleBars.registerHelper('checkRelations', function (relations, className, isVO, referenceClass, entities) {
    try {
        if(typeof relations === "undefined") {
            return 
        } else {
            // primitive type
            if(className.includes("String") || className.includes("Integer") || className.includes("Long") || className.includes("Double") || className.includes("Float")
                    || className.includes("Boolean") || className.includes("Date")) {
                if(className.includes("List")) {
                    return "@ElementCollection(fetch = FetchType.EAGER)"
                }
            } else {
                // ValueObject
                if(entities.elements)
                    Object.keys(entities.elements).forEach(key => {
                        var entity = entities.elements[key]
                        if("List<" + entity.namePascalCase + ">"  == className){
                            isVO = entity.isVO;
                        }
                    });

                if(isVO) {
                    if(className.includes("List")) {
                        return "@ElementCollection(fetch = FetchType.EAGER)"
                    } else {
                        return "@Embedded"
                    }
                } else {
                    for(var i = 0; i < relations.length; i ++ ) {
                        if(relations[i] != null) {
                            if(className.includes(relations[i].targetElement.name) && !relations[i].relationType.includes("Generalization")) {
                                // Enumeration
                                if(relations[i].targetElement._type.endsWith('enum') || relations[i].targetElement._type.endsWith('Exception')) {
                                    return
                                }
                                // complex type
                                if(relations[i].sourceMultiplicity == "1" &&
                                        (relations[i].targetMultiplicity == "1..n" || relations[i].targetMultiplicity == "0..n") || className.includes("List")
                                ) {
                                    return "@OneToMany(fetch = FetchType.EAGER)"

                                } else if((relations[i].sourceMultiplicity == "1..n" || relations[i].sourceMultiplicity == "0..n") && relations[i].targetMultiplicity == "1"){
                                    return "@ManyToOne(fetch = FetchType.EAGER)"
                                
                                } else if(relations[i].sourceMultiplicity == "1" && relations[i].targetMultiplicity == "1"){
                                    return "@OneToOne(fetch = FetchType.EAGER)"
                                
                                } else if((relations[i].sourceMultiplicity == "1..n" || relations[i].sourceMultiplicity == "0..n") &&
                                        (relations[i].targetMultiplicity == "1..n" || relations[i].targetMultiplicity == "0..n") || className.includes("List")
                                ) {
                                    return "@ManyToMany(fetch = FetchType.EAGER)"
                                }
                            }
                        }
                    }
                    if(referenceClass) {
                        return "@OneToOne(fetch = FetchType.EAGER)"
                    }
                }
            }
        }
    } catch (e) {
        console.log(e)
    }
});

window.$HandleBars.registerHelper('checkExtends', function (relations, name) {
    try {
        if(typeof relations === "undefined" || name === "undefined"){
            return;
        } else {
            for(var i = 0; i < relations.length; i ++ ){
                if(relations[i] != null){
                    if(relations[i].sourceElement.name == name && relations[i].relationType.includes("Generalization")){
                        var text = "extends " + relations[i].targetElement.name
                        return text
                    }
                }
            }
        }
    } catch(e) {
        console.log(e)
    }
});

window.$HandleBars.registerHelper('setDiscriminator', function (relations, name) {
    try {
        if (typeof relations == "undefined") {
            return 
        } else {
            for (var i = 0; i < relations.length; i ++ ) {
                if (relations[i] != null) {
                    var text = ''
                    if (relations[i].targetElement != "undefined") {
                        if(relations[i].targetElement.name.toLowerCase() == name && relations[i].relationType.includes("Generalization")) {
                            text = '@DiscriminatorColumn(\n' + 
                                '    discriminatorType = DiscriminatorType.STRING,\n' +
                                '    name = "' + name + '_type",\n' +
                                '    columnDefinition = "CHAR(5)"\n' +
                                ')'
                            return text
                        }
                    } else {
                        if(relations[i].toName.toLowerCase() == name && relations[i].relationType.includes("Generalization")) {
                            text = '@DiscriminatorColumn(\n' + 
                                '    discriminatorType = DiscriminatorType.STRING,\n' +
                                '    name = "' + name + '_type",\n' +
                                '    columnDefinition = "CHAR(5)"\n' +
                                ')'
                            return text
                        }
                    }
                    if (relations[i].sourceElement != "undefined") {
                        if (relations[i].sourceElement.name.toLowerCase() == name && relations[i].relationType.includes("Generalization")) {
                            return '@DiscriminatorValue("' + name + '")'
                        }
                    } else {
                        if (relations[i].fromName.toLowerCase() == name && relations[i].relationType.includes("Generalization")) {
                            return '@DiscriminatorValue("' + name + '")'
                        }
                    }
                }
            }
        }
    } catch(e) {
        console.log(e)
    }
});

window.$HandleBars.registerHelper('setOperations', function (commands, name, options) {
    try {
        if(commands == "undefined") {
            return options.fn(this);
        }
        var isCmd = false;
        for (var i = 0; i < commands.length; i ++ ) {
            if(commands[i] != null) {
                if (commands[i].name == name && commands[i].isRestRepository != true) {
                    isCmd = true
                }
            }
        }
        if(isCmd) {
            return options.inverse(this);
        } else {
            return options.fn(this);
        }
    } catch(e) {
        console.log(e)
    }
});


window.$HandleBars.registerHelper('has', function (members) {
    try {
        return (members.length > 0);
    } catch(e) {
        console.log(e)
    }
});


</function>
