<#assign mapperLastIndex=t.mapperSuperClassName?last_index_of(".")+1 >
<#assign mapperLen=t.mapperSuperClassName?length >
<#assign mapperSimpleName=t.mapperSuperClassName?substring(mapperLastIndex,mapperLen) >
package ${t.packageName}.${t.moduleName}.mapper;

import ${t.mapperSuperClassName};
import ${t.packageName}.${t.moduleName}.entity.${t.entityName};
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ${t.entityName}Mapper extends ${mapperSimpleName}<${t.entityName}> {


}
