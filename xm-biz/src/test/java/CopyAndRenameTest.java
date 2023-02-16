import cn.hutool.core.util.ArrayUtil;

import java.io.BufferedReader;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class CopyAndRenameTest {

    public static void main(String[] args) {
        String path = "D:\\dowork\\ssss\\src\\main\\java\\com\\xunmo\\webs\\dict";
        String[] sourceNameAttr = {"dict"};
        String[] targetNameAttr = {"user"};

        rename(sourceNameAttr, targetNameAttr, path);
    }

    private static void rename(String[] sourceNameAttr, String[] targetNameAttr, String sourcePath) {
        if (ArrayUtil.isEmpty(targetNameAttr)) {
            throw new IllegalArgumentException("targetName is Null or Empty");
        }
        if (sourcePath == null || "".equals(sourcePath)) {
            System.err.println(sourcePath);
            throw new IllegalArgumentException("sourcePath is Null or Empty");
        }
        boolean isError = true;
        String errorMsg = "";
        for (String sourceName : sourceNameAttr) {
            final int matchIndex = sourcePath.lastIndexOf(sourceName);
            if (matchIndex == -1) {
                errorMsg = sourceName;
            } else {
                isError = false;
            }
        }
        if (isError) {
            System.err.println(errorMsg);
            throw new IllegalArgumentException("sourcePath not find sourceName");
        }
        final File sourceFile = new File(sourcePath);
        if (!sourceFile.exists()) {
            throw new IllegalArgumentException("sourcePath is not exists");
        }

        String sourceName = sourceNameAttr[0];
        String targetName = targetNameAttr[0];
        String sourceDirName = "";
        String targetDirName = "";
        if (sourceNameAttr.length > 1 && targetNameAttr.length > 1) {
            sourceDirName = sourceNameAttr[1];
            targetDirName = targetNameAttr[1];
        } else if (sourceNameAttr.length > 1) {
            sourceDirName = sourceNameAttr[1];
            targetDirName = targetNameAttr[0];
        } else if (targetNameAttr.length > 1) {
            sourceDirName = sourceNameAttr[0];
            targetDirName = targetNameAttr[1];
        } else {
            sourceDirName = sourceNameAttr[0];
            targetDirName = targetNameAttr[0];
        }
        final int matchIndex = sourcePath.lastIndexOf(sourceDirName);
        final String targetPath = sourcePath.substring(0, matchIndex) + targetDirName;
        copyDir(sourceName, targetName, sourceDirName, targetDirName, sourcePath, targetPath);
    }

    //文件夹的拷贝
    public static void copyDir(String sourceName, String targetName, String sourceDirName, String targetDirName, String sourcePath, String newPath) {
        File start = new File(sourcePath);
        File end = new File(newPath);
        String[] filePath = start.list();        //获取该文件夹下的所有文件以及目录的名字
        if (!end.exists()) {
            end.mkdir();
        }
        for (String temp : filePath) {
            String newTemp = temp.replace(upperFirst(sourceName), upperFirst(targetName));
            //查看其数组中每一个是文件还是文件夹
            if (new File(sourcePath + File.separator + newTemp).isDirectory()) {
                //为文件夹，进行递归
                copyDir(sourceName, targetName, sourceDirName, targetDirName, sourcePath + File.separator + temp, newPath + File.separator + newTemp);
            } else {
                //为文件则进行拷贝
                copyFile(sourceName, targetName, sourceDirName, targetDirName, sourcePath + File.separator + temp, newPath + File.separator + newTemp);
            }
        }
    }

    //文件的拷贝
    public static void copyFile(String sourceName, String targetName,String sourceDirName,String targetDirName, String sourcePath, String newPath) {
        File start = new File(sourcePath);
        File end = new File(newPath);
        try (FileReader bis = new FileReader(start);
             FileWriter bos = new FileWriter(end);
             BufferedReader br = new BufferedReader(bis);
             // 内存流, 作为临时流
             CharArrayWriter tempStream = new CharArrayWriter();) {
            // 替换
            String line;
            while ((line = br.readLine()) != null) {
                line = line.replace(upperFirst(sourceName), upperFirst(targetName));
                line = line.replace(sourceName, targetName);
                line = line.replace(sourceDirName, targetDirName);
                // 将该行写入内存
                tempStream.write(line);
                // 添加换行符
                tempStream.append(System.getProperty("line.separator"));
            }
            // 将内存中的流 写入 文件
            tempStream.writeTo(bos);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String upperFirst(String source) {
        if (null == source || "".equals(source)) {
            return source;
        }
        if (source.length() == 1) {
            return source.toUpperCase();
        }
        return source.substring(0, 1).toUpperCase() + source.substring(1);
    }


}
