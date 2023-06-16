<#assign mapperLastIndex=t.mapperSuperClassName?last_index_of(".")+1 >
<#assign mapperLen=t.mapperSuperClassName?length >
<#assign mapperSimpleName=t.mapperSuperClassName?substring(mapperLastIndex,mapperLen) >
<#assign serviceLastIndex=t.serviceSuperClassName?last_index_of(".")+1 >
<#assign serviceLen=t.serviceSuperClassName?length >
<#assign serviceSimpleName=t.serviceSuperClassName?substring(serviceLastIndex,serviceLen) >
<#assign serviceImplLastIndex=t.serviceImplSuperClassName?last_index_of(".")+1 >
<#assign serviceImplLen=t.serviceImplSuperClassName?length >
<#assign serviceImplSimpleName=t.serviceImplSuperClassName?substring(serviceImplLastIndex,serviceImplLen) >
package ${t.packageName}.${t.moduleName}.controller;

import com.xunmo.common.utils.AjaxJson;
import ${t.packageName}.${t.moduleName}.dto.${t.entityName}DelDTO;
import ${t.packageName}.${t.moduleName}.dto.${t.entityName}GetDTO;
import ${t.packageName}.${t.moduleName}.dto.${t.entityName}GetPageDTO;
import ${t.packageName}.${t.moduleName}.dto.${t.entityName}SaveDTO;
import ${t.packageName}.${t.moduleName}.dto.${t.entityName}UpdateDTO;
import ${t.packageName}.${t.moduleName}.entity.${t.entityName};
import ${t.packageName}.${t.moduleName}.service.${t.entityName}Service;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.validation.annotation.Valid;
import org.noear.solon.validation.annotation.Validated;

@Slf4j
@Valid
@Controller
@Mapping("/${t.varName}")
public class ${t.entityName}Controller extends XmController<${t.entityName}Service> implements I${t.entityName}Controller {

    @Override
    public AjaxJson get(@Validated ${t.entityName}GetDTO ${t.varName}GetDTO) throws Exception {
        final ${t.entityName} ${t.varName} = service.getById(${t.varName}GetDTO.getId());
        return AjaxJson.getSuccessData(${t.varName});
    }

    @Override
    public AjaxJson list(@Validated ${t.entityName}GetPageDTO ${t.varName}GetPageDTO) throws Exception {
        service.getList(${t.varName}GetPageDTO);
        return AjaxJson.getPageData();
    }

    @Override
    public AjaxJson add(@Validated ${t.entityName}SaveDTO ${t.varName}SaveDTO) throws Exception {
        final ${t.entityName} ${t.varName} = ${t.varName}SaveDTO.toEntity();
        service.add(${t.varName});
        return AjaxJson.getSuccessData(${t.varName});
    }

    @Override
    public AjaxJson del(@Validated ${t.entityName}DelDTO ${t.varName}DelDTO) throws Exception {
        service.del(${t.varName}DelDTO.getId());
        return AjaxJson.getSuccess();
    }

    @Override
    public AjaxJson update(@Validated ${t.entityName}UpdateDTO ${t.varName}UpdateDTO) throws Exception {
        service.updateBean(${t.varName}UpdateDTO);
        return AjaxJson.getSuccess();
    }
}
