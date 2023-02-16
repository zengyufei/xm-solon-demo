<#assign mapperLastIndex=t.mapperSuperClassName?last_index_of(".")+1 >
<#assign mapperLen=t.mapperSuperClassName?length >
<#assign mapperSimpleName=t.mapperSuperClassName?substring(mapperLastIndex,mapperLen) >
<#assign serviceLastIndex=t.serviceSuperClassName?last_index_of(".")+1 >
<#assign serviceLen=t.serviceSuperClassName?length >
<#assign serviceSimpleName=t.serviceSuperClassName?substring(serviceLastIndex,serviceLen) >
<#assign serviceImplLastIndex=t.serviceImplSuperClassName?last_index_of(".")+1 >
<#assign serviceImplLen=t.serviceImplSuperClassName?length >
<#assign serviceImplSimpleName=t.serviceImplSuperClassName?substring(serviceImplLastIndex,serviceImplLen) >
package ${t.packageName}.${t.moduleName}.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import ${t.serviceSuperClassName};
import ${t.serviceImplSuperClassName};
import ${t.packageName}.${t.moduleName}.dto.${t.entityName}GetPageDTO;
import ${t.packageName}.${t.moduleName}.dto.${t.entityName}UpdateDTO;
import ${t.packageName}.${t.moduleName}.entity.${t.entityName};
import ${t.packageName}.${t.moduleName}.mapper.${t.entityName}Mapper;
import org.noear.solon.aspect.annotation.Service;
import org.noear.solon.data.annotation.Tran;

import java.util.List;

/**
* Service: ${t.tableName} -- ${t.tableComment}
* @author ${t.authorName}
*/
@Service
public class ${t.entityName}Service extends ${serviceImplSimpleName}<${t.entityName}Mapper, ${t.entityName}> implements ${serviceSimpleName}<${t.entityName}> {

    /**
    * 获取列表
    *
    * @param ${t.varName}GetPageDTO ${t.varName}获取页面dto
    * @return {@link List}<{@link ${t.entityName}}>
    */
    public List<${t.entityName}> getList(${t.entityName}GetPageDTO ${t.varName}GetPageDTO) {
        final ${t.entityName} ${t.varName} = ${t.varName}GetPageDTO.toEntity();
        return startPage(() -> this.baseMapper.selectList(Wrappers.lambdaQuery(${t.varName})));
    }

    /**
    * 添加
    *
    * @param ${t.varName} ${t.varName}
    * @return {@link ${t.entityName}}
    */
    @Tran
    public ${t.entityName} add(${t.entityName} ${t.varName}) throws Exception {
        this.baseMapper.insert(${t.varName});
        return ${t.varName};
    }

    /**
    * 删除
    *
    * @param id id
    * @return boolean
    */
    @Tran
    public boolean del(String id) {
        this.baseMapper.deleteById(id);
        return true;
    }

    /**
    * 更新
    *
    * @param ${t.varName}UpdateDTO ${t.varName}更新dto
    * @return boolean
    */
    @Tran
    public boolean updateBean(${t.entityName}UpdateDTO ${t.varName}UpdateDTO) throws Exception {
        checkAndGet(${t.varName}UpdateDTO.getId());
        final ${t.entityName} input${t.entityName} = toBean(${t.varName}UpdateDTO, ${t.entityName}.class);
        this.baseMapper.updateNotNullById(input${t.entityName});
        return true;
    }


}
