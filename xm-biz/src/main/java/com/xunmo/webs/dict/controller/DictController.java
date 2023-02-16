package com.xunmo.webs.dict.controller;

import com.xunmo.base.move.XmSimpleMoveControllerImpl;
import com.xunmo.utils.AjaxJson;
import com.xunmo.webs.dict.dto.DictDelDTO;
import com.xunmo.webs.dict.dto.DictGetDTO;
import com.xunmo.webs.dict.dto.DictGetPageDTO;
import com.xunmo.webs.dict.dto.DictSaveDTO;
import com.xunmo.webs.dict.dto.DictUpdateDTO;
import com.xunmo.webs.dict.entity.Dict;
import com.xunmo.webs.dict.service.DictService;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Post;
import org.noear.solon.core.handle.UploadedFile;
import org.noear.solon.validation.annotation.Valid;
import org.noear.solon.validation.annotation.Validated;

@Slf4j
@Valid
@Controller
@Mapping("/dict")
public class DictController extends XmSimpleMoveControllerImpl<DictService, Dict> {

    @Post
    @Mapping("/get")
    public AjaxJson get(@Validated DictGetDTO dictGetDTO) throws Exception {
        final Dict dict = service.getById(dictGetDTO.getId());
        return AjaxJson.getSuccessData(dict);
    }

    @Post
    @Mapping("/get/tree")
    public AjaxJson getTree(String id) throws Exception {
        return AjaxJson.getSuccessData(service.getTree(id));
    }

    @Post
    @Mapping("/list")
    public AjaxJson list(@Validated DictGetPageDTO dictGetPageDTO) throws Exception {
        service.getList(dictGetPageDTO);
        return AjaxJson.getPageData();
    }

    //    @NoRepeatSubmit  //重复提交验证
    @Post
    @Mapping("/add")
    public AjaxJson add(@Validated DictSaveDTO dictSaveDTO) throws Exception {
        final Dict dict = dictSaveDTO.toEntity();
        service.addBean(dict);
        return AjaxJson.getSuccessData(dict);
    }

    @Post
    @Mapping("/del")
    public AjaxJson del(@Validated DictDelDTO dictDelDTO) throws Exception {
        service.delById(dictDelDTO.getId());
        return AjaxJson.getSuccess();
    }

    //    @NoRepeatSubmit  //重复提交验证
    @Post
    @Mapping("/update")
    public AjaxJson update(@Validated DictUpdateDTO dictUpdateDTO) throws Exception {
        service.updateBean(dictUpdateDTO);
        return AjaxJson.getSuccess();
    }

    /**
     * 下载模板
     *
     * @throws Exception 异常
     */
    @Post
    @Mapping("/downloadTemplate")
    public void downloadTemplate() throws Exception {
        service.downloadTemplate();
    }


    /**
     * 导入文件
     *
     * @param file 文件
     * @return {@link AjaxJson}<{@link Boolean}>
     * @throws Exception 异常
     */
    @Post
    @Mapping("/import")
    public AjaxJson<Boolean> importFile(UploadedFile file) throws Exception {
        return AjaxJson.getSuccessData(service.importFile(file));
    }
}
