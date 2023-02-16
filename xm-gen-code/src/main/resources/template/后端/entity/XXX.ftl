<#assign parentEntityLastIndex=t.entitySuperClassName?last_index_of(".")+1 >
<#assign parentEntityLen=t.entitySuperClassName?length >
<#assign parentEntitySimpleName=t.entitySuperClassName?substring(parentEntityLastIndex,parentEntityLen) >
package ${t.packageName}.${t.moduleName}.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import ${t.entitySuperClassName};
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

/**
* Controller: ${t.tableName} -- ${t.tableComment}
* @author ${t.authorName}
*/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("${t.tableName}")
@EqualsAndHashCode(callSuper = true)
public class ${t.entityName} extends ${parentEntitySimpleName} {

<#list cs as c>
    /**
    * ${c.columnComment}
    */
    private ${c.javaType} ${c.javaField};

</#list>


}
