<#assign parentEntityLastIndex=t.entitySuperClassName?last_index_of(".")+1 >
<#assign parentEntityLen=t.entitySuperClassName?length >
<#assign parentEntitySimpleName=t.entitySuperClassName?substring(parentEntityLastIndex,parentEntityLen) >
package ${t.packageName}.${t.moduleName}.entity;

import com.xunmo.common.base.XmDO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public abstract class ${t.entityName}DO<T> extends XmDO<T> {

<#list cs as c>
    /**
    * ${c.columnComment}
    */
    private ${c.javaType} ${c.javaField};

</#list>

}
