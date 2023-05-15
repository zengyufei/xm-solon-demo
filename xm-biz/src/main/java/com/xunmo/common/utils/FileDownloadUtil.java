package com.xunmo.common.utils;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.google.common.net.HttpHeaders;
import com.xunmo.core.event.XmLogEvent;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.handle.Context;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FileDownloadUtil {

    public static void fileDownload(InputStream stream, String fileName, Long size) throws Exception {
        final Context ctx = Context.current();
        // 设置response的Header
        ctx.charset("UTF-8");
        ctx.contentType("application/octet-stream");
        // 纯下载方式 文件名应该编码成UTF-8
        // Content-Disposition的作用：告知浏览器以何种方式显示响应返回的文件，用浏览器打开还是以附件的形式下载到本地保存
        // attachment表示以附件方式下载   inline表示在线打开   "Content-Disposition: inline; filename=文件名.mp3"
        // filename表示文件的默认名称，因为网络传输只支持URL编码的相关支付，因此需要将文件名URL编码后进行传输,前端收到后需要反编码才能获取到真正的名称
        ctx.headerSet(HttpHeaders.CONTENT_DISPOSITION, "attachment;"
                + FileDownloadUtil.getOutFileName(fileName));
        // 告知浏览器文件的大小
        ctx.headerSet("Content-Length", "" + size);
        // 发送给客户端的数据
        OutputStream outputStream = ctx.outputStream();
        byte[] buff = new byte[1024];
        BufferedInputStream bis;
        // 读取文件
        bis = new BufferedInputStream(stream);
        int i = bis.read(buff);
        // 只要能读到，则一直读取
        while (i != -1) {
            // 将文件写出
            outputStream.write(buff, 0, buff.length);
            // 刷出
            outputStream.flush();
            i = bis.read(buff);
        }
        outputStream.close();
        ctx.flush();
    }

    public static void fileDownload(File file, String fileName, String etag) throws Exception {
        final Context ctx = Context.current();
        String lastModify = new Date(file.lastModified()).toString();
        ctx.headerSet("Last-Modified", lastModify);
        if (StrUtil.isNotBlank(etag)) {
            ctx.headerSet("Etag", etag);
        }
        // 设置response的Header
        ctx.charset("UTF-8");
        ctx.contentType("application/octet-stream");
        // 纯下载方式 文件名应该编码成UTF-8
        // Content-Disposition的作用：告知浏览器以何种方式显示响应返回的文件，用浏览器打开还是以附件的形式下载到本地保存
        // attachment表示以附件方式下载   inline表示在线打开   "Content-Disposition: inline; filename=文件名.mp3"
        // filename表示文件的默认名称，因为网络传输只支持URL编码的相关支付，因此需要将文件名URL编码后进行传输,前端收到后需要反编码才能获取到真正的名称
        ctx.headerSet(HttpHeaders.CONTENT_DISPOSITION, "attachment;"
                + FileDownloadUtil.getOutFileName(fileName));
        // 告知浏览器文件的大小
        ctx.headerSet("Content-Length", "" + file.length());
        // 发送给客户端的数据
        OutputStream outputStream = ctx.outputStream();
        byte[] buff = new byte[1024];
        BufferedInputStream bis;
        // 读取文件
        bis = new BufferedInputStream(IoUtil.toStream(file));
        int i = bis.read(buff);
        // 只要能读到，则一直读取
        while (i != -1) {
            // 将文件写出
            outputStream.write(buff, 0, buff.length);
            // 刷出
            outputStream.flush();
            i = bis.read(buff);
        }
        outputStream.close();
        ctx.flush();
    }

    public static <T> void downloadExeclTemplate(Class<T> clazz, String fileName) throws Exception {
        List<T> list = new ArrayList<>();
        exportExcel(clazz,fileName,list);
    }

    public static <T> void exportExcel(Class<T> clazz, String fileName,List<T> list) throws Exception {
        final Context ctx = Context.current();
        ctx.contentType("application/vnd.ms-excel");
        ctx.charset("utf-8");
        ctx.headerSet(HttpHeaders.CONTENT_DISPOSITION, "attachment;"
                + FileDownloadUtil.getOutFileName(fileName));
        final String ip = ctx.ip();
        EventBus.push(new XmLogEvent(ip+" 进行下载, 下载文件["+fileName+"]"));
        EasyExcel.write(ctx.outputStream(), clazz).sheet().doWrite(list);
    }


    public static String getOutFileName(String fileName) throws UnsupportedEncodingException {
        final Context ctx = Context.current();
        if (ctx == null) {
            return fileName;
        }
        return getOutFileName(ctx, fileName);
    }

    public static String getOutFileName(Context ctx, String fileName) throws UnsupportedEncodingException {
        String newFileName = URLEncoder.encode(fileName, "UTF8");
        String rtn = "filename*=utf-8'zh_cn'" + newFileName;
        String userAgent = ctx.header("user-agent");
        if (userAgent != null) {
            userAgent = userAgent.toLowerCase();
            // IE浏览器，只能采用URLEncoder编码
            if (userAgent.contains("edg")) {
                rtn = "filename=\"" + newFileName + "\"";
            }
            else if (userAgent.contains("msie")) {
                rtn = "filename=\"" + newFileName + "\"";
            }
            // Opera浏览器只能采用filename*
            else if (userAgent.contains("opera")) {
                rtn = "filename*=UTF-8'zh_cn'" + newFileName;
            }
            // Safari浏览器，只能采用ISO编码的中文输出
            //            else if (userAgent.contains("safari")) {
            //                rtn = "filename=\"" + new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1) + "\"";
            //            }
            // FireFox浏览器，可以使用MimeUtility或filename*或ISO编码的中文输出
            else if (userAgent.contains("mozilla")) {
                rtn = "filename*=UTF-8'zh_cn'" + newFileName;
            }
            // APIFOX
            else if (userAgent.contains("apifox")) {
                rtn = "filename*=utf-8'zh_cn'" + newFileName;
            }
        }
        return rtn;
    }

}
