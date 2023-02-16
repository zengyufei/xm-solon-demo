package com.xunmo.request.xss.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 跨站脚本攻击(cross site scripting)工具类
 */
public class XssUtil {

    /**
     * 去除标签中所有 on开头的事件
     *
     * @param html
     * @return
     */
    public static String removeEvent(String html) {
        // 第一种情况 onEvent='...'
        // 第二种情况 onEvent="..."
        // 第三种情况 onEvent=...
        String regex = "<[a-zA-Z]+\\s+[\\w\\s\\-%=:;\"\'\\u4e00-\\u9fa5]*(on\\w+=((\'[^>']*\')|(\"[^>\"]*\")|([\\w(),'\"\\[\\];\\u4e00-\\u9fa5]*)))";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(html);
        while (matcher.find()) {
            html = html.substring(0, matcher.start(1)) + html.substring(matcher.end(1));
            matcher = pattern.matcher(html);
        }

        return html;
    }

    /**
     * 去除 script 标签
     *
     * @param html
     * @return
     */
    public static String removeScript(String html) {
        String regex = "</?[sS][cC][rR][iI][pP][tT][^>]*>";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(html);
        if (matcher.find()) {
            html = matcher.replaceAll("");
        }
        return html;
    }

    /**
     * 去除eval相关代码
     * @param html
     * @return
     */
    public static String removeEval(String html){
        String regex = "eval\\([^)]*?\\)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(html);
        if(matcher.find()){
            html = matcher.replaceAll("");
        }
        return html;
    }

    /**
     * 把标签中的 javascript 替换掉
     *
     * @param html
     * @return
     */
    public static String swapJavascript(String html) {
        String regex = "<[a-zA-Z]+\\s+[\\w\\s\\-%=:;\"\'\\u4e00-\\u9fa5]*([jJ][aA][vV][aA][sS][cC][rR][iI][pP][tT][^>]*?)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(html);
        while (matcher.find()) {
            html = html.substring(0, matcher.start(1)) + "java_script" + html.substring(matcher.end(1));
            matcher = pattern.matcher(html);
        }
        return html;
    }

    /**
     * 把标签中的 vbscript 替换掉
     *
     * @param html
     * @return
     */
    public static String swapVbscript(String html) {
        String regex = "<[a-zA-Z]+\\s+[\\w\\s\\-%=:;\"\'\\u4e00-\\u9fa5]*([vV][bB][sS][cC][rR][iI][pP][tT][^>]*?)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(html);
        while (matcher.find()) {
            html = html.substring(0, matcher.start(1)) + "vb_script" + html.substring(matcher.end(1));
            matcher = pattern.matcher(html);
        }
        return html;
    }

    /**
     * 把标签中的 livescript 替换掉
     *
     * @param html
     * @return
     */
    public static String swapLivescript(String html) {
        String regex = "<[a-zA-Z]+\\s+[\\w\\s\\-%=:;\"\'\\u4e00-\\u9fa5]*([lL][iI][vV][eE][sS][cC][rR][iI][pP][tT][^>]*?)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(html);
        while (matcher.find()) {
            html = html.substring(0, matcher.start(1)) + "live_script" + html.substring(matcher.end(1));
            matcher = pattern.matcher(html);
        }
        return html;
    }

    /**
     * 对html转义
      * @param html
     * @return
     */
    public static String encode(String html) {
        // 对 form、iframe、frameset、frame 标签的尖括号转义
        String regex = "</?(([fF][oO][rR][mM])|([iI][fF][rR][aA][mM][eE])|([fF][rR][aA][mM][eE]([sS][eE][tT])?))[^>]*?>";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(html);
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            StringBuilder sb = new StringBuilder();
            if (start > 0) {
                sb.append(html.substring(0, start));
            }
            sb.append("&#60;").append(html.substring(start + 1, end - 1)).append("&#62;");
            sb.append(html.substring(end));

            html = sb.toString();
            matcher = pattern.matcher(html);
        }

        // 对 document. 和 window. 中的 . 转义
        regex = "((document)|(window))\\.";
        pattern = Pattern.compile(regex);
        matcher = pattern.matcher(html);
        while (matcher.find()) {
            int end = matcher.end();
            html = html.substring(0, end - 1) + "&middot;" + html.substring(end);
            matcher = pattern.matcher(html);
        }

        return html;
    }

}
