forEach: View
representativeFor: View
fileName: {{namePascalCase}}.java
path: common-api/{{{options.packagePath}}}/query
except: {{contexts.isNotCQRS}}
---
package {{options.package}}.query;

import org.springframework.hateoas.server.core.Relation;

import javax.persistence.*;
import java.util.List;
import java.util.Date;
import lombok.Data;
{{#checkBigDecimal fieldDescriptors}}{{/checkBigDecimal}}

//<<< EDA / CQRS
@Entity
@Table(name="{{namePascalCase}}_table")
@Data
@Relation(collectionRelation = "{{namePlural}}")
public class {{namePascalCase}} {

{{#fieldDescriptors}}
    {{#isKey}}
        @Id
        //@GeneratedValue(strategy=GenerationType.AUTO)
    {{/isKey}}
        private {{className}} {{nameCamelCase}};
{{/fieldDescriptors}}


}

<function>
this.contexts.isNotCQRS = this.dataProjection!="cqrs"//(this.dataProjection == "QUERY-FOR-AGGREGATE")

window.$HandleBars.registerHelper('checkBigDecimal', function (fieldDescriptors) {
    for(var i = 0; i < fieldDescriptors.length; i ++ ){
        if(fieldDescriptors[i] && fieldDescriptors[i].className.includes('BigDecimal')){
            return "import java.math.BigDecimal;";
        }
    }
});
</function>
//>>> EDA / CQRS