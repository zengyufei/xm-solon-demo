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
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Post;
import org.noear.solon.validation.annotation.Valid;
import org.noear.solon.validation.annotation.Validated;

@Valid
@Controller
public interface I${t.entityName}Controller {

    @Post
    @Mapping("/get")
    AjaxJson get(@Validated ${t.entityName}GetDTO ${t.varName}GetDTO) throws Exception;

    @Post
    @Mapping("/list")
    AjaxJson list(@Validated ${t.entityName}GetPageDTO ${t.varName}GetPageDTO) throws Exception;

    //    @NoRepeatSubmit  //重复提交验证
    @Post
    @Mapping("/add")
    AjaxJson add(@Validated ${t.entityName}SaveDTO ${t.varName}SaveDTO) throws Exception;

    @Post
    @Mapping("/del")
    AjaxJson del(@Validated ${t.entityName}DelDTO ${t.varName}DelDTO) throws Exception;

    //    @NoRepeatSubmit  //重复提交验证
    @Post
    @Mapping("/update")
    AjaxJson update(@Validated ${t.entityName}UpdateDTO ${t.varName}UpdateDTO) throws Exception;

}
