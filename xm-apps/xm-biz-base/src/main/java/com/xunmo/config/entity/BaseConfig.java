package com.xunmo.config.entity;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.system.OsInfo;
import cn.hutool.system.SystemUtil;
import lombok.Data;
import org.noear.solon.annotation.Init;

import java.io.File;

/**
 * Title: BaseConfig.java
 *
 * @author zxc
 * @time 2018/5/10 下午4:54
 */
@Data
public class BaseConfig {

    private String type;
    private String windowUploadDir;
    private String linuxUploadDir;

    /**
     * 文件服务的上传地址
     */
    private String fileServerUploadUrl;

    /**
     * 文件服务下载地址
     */
    private String fileServerDownloadUrl;

    /**
     * 文件预览服务地址
     */
    private String fileServerPreviewUrl;

    /**
     * 专门用于文件预览服务的文件下载地址
     * 预览接口不同于下载接口。会被base64编码
     */
    private String fileServerPreviewApi;


    @Init
    public void init() {
        final OsInfo osInfo = SystemUtil.getOsInfo();
        if (osInfo.isWindows()) {
            if (isLocal()) {
                File file = new File(windowUploadDir);
                if (!file.exists()) {
                    file.mkdirs();
                }
            }
        }
        if (osInfo.isLinux()) {
            if (isLocal()) {
                File file = new File(linuxUploadDir);
                if (!file.exists()) {
                    file.mkdirs();
                }
            }
        }
    }

    public boolean isLocal() {
        return StrUtil.equalsIgnoreCase(type, "local");
    }

    public File getTodayUploadDirFile() {
        String today = DateUtil.today();
        File t = null;
        final OsInfo osInfo = SystemUtil.getOsInfo();
        if (osInfo.isWindows()) {
            t = new File(windowUploadDir, today);
            if (!t.exists()) {
                t.mkdirs();
            }
        }
        if (osInfo.isLinux()) {
            t = new File(linuxUploadDir, today);
            if (!t.exists()) {
                t.mkdirs();
            }
        }
        return t;
    }
}
