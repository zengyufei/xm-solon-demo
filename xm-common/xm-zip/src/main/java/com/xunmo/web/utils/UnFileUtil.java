package com.xunmo.web.utils;

import fr.opensagres.xdocreport.core.io.IOUtils;
import net.lingala.zip4j.ZipFile;
import net.sf.sevenzipjbinding.ExtractOperationResult;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;
import net.sf.sevenzipjbinding.simple.ISimpleInArchive;
import net.sf.sevenzipjbinding.simple.ISimpleInArchiveItem;
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/*
*
        <!-- 7z 依赖 begin -->
        <dependency>
            <groupId>org.tukaani</groupId>
            <artifactId>xz</artifactId>
            <version>1.9</version>
        </dependency>
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-compress</artifactId>
            <version>1.21</version>
        </dependency>
        <dependency>
            <groupId>fr.opensagres.xdocreport</groupId>
            <artifactId>xdocreport</artifactId>
            <version>1.0.6</version>
        </dependency>
        <!-- 7z 依赖 end -->
        <!-- zip 依赖 -->
        <dependency>
            <groupId>net.lingala.zip4j</groupId>
            <artifactId>zip4j</artifactId>
            <version>2.9.0</version>
        </dependency>
        <!-- rar 依赖 begin -->
        <dependency>
            <groupId>net.sf.sevenzipjbinding</groupId>
            <artifactId>sevenzipjbinding</artifactId>
            <version>16.02-2.01</version>
        </dependency>
        <dependency>
            <groupId>net.sf.sevenzipjbinding</groupId>
            <artifactId>sevenzipjbinding-all-platforms</artifactId>
            <version>16.02-2.01</version>
        </dependency>
        <!-- rar 依赖 end -->
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
            <version>1.7.30</version>
        </dependency>
*
*
* */
public class UnFileUtil {

    private final static Logger log = LoggerFactory.getLogger(UnFileUtil.class);

    public static Map<String, Object> unFile(String rootPath, String sourcePath, String destDirPath, String passWord) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        String result = "";
        if (sourcePath.toLowerCase().endsWith(".zip")) {
            //Wrong password!
            result = unZip(rootPath, sourcePath, destDirPath, passWord);
        } else if (sourcePath.toLowerCase().endsWith(".rar")) {
            //java.security.InvalidAlgorithmParameterException: password should be specified
            result = unRar(rootPath, sourcePath, destDirPath, passWord);
        } else if (sourcePath.toLowerCase().endsWith(".7z")) {
            //PasswordRequiredException: Cannot read encrypted content from G:\ziptest\11111111.7z without a password
            result = un7z(rootPath, sourcePath, destDirPath, passWord);
        }

        resultMap.put("resultMsg", 1);
        if (isNotBlank(result)) {
            if (result.contains("password")) {
                resultMap.put("resultMsg", 2);
            }
            if (!result.contains("password")) {
                resultMap.put("resultMsg", 3);
            }
        }
        resultMap.put("files", null);
        return resultMap;
    }

    public static Map<String, Object> unFile(String filePath, String destDirPath, String passWord) {
        final File file = new File(filePath);
        return unFile(file, destDirPath, passWord);
    }

    public static Map<String, Object> unFile(File file, String destDirPath, String passWord) {
        Map<String, Object> resultMap = new HashMap<>();
        String result = "";
        if ("zip".equals(getFileType(file))) {
            //Wrong password!
            result = unZip(file, destDirPath, passWord);
        } else if ("rar".equals(getFileType(file))) {
            //java.security.InvalidAlgorithmParameterException: password should be specified
            result = unRar(file, destDirPath, passWord);
        } else if ("7z".equals(getFileType(file))) {
            //PasswordRequiredException: Cannot read encrypted content from G:\ziptest\11111111.7z without a password
            result = un7z(file, destDirPath, passWord);
        }

        resultMap.put("resultMsg", 1);
        if (isNotBlank(result)) {
            if (result.contains("password")) {
                resultMap.put("resultMsg", 2);
            }
            if (!result.contains("password")) {
                resultMap.put("resultMsg", 3);
            }
        }
        resultMap.put("files", null);
        return resultMap;

    }


    private static String un7z(File srcFile, String destDirPath, String passWord) {
        try {
            // 判断源文件是否存在
            if (!srcFile.exists()) {
                throw new Exception(srcFile.getPath() + "所指文件不存在");
            }
            //开始解压
            SevenZFile zIn = null;
            if (isNotBlank(passWord)) {
                zIn = new SevenZFile(srcFile, passWord.toCharArray());
            } else {
                zIn = new SevenZFile(srcFile);
            }

            SevenZArchiveEntry entry;
            File file;
            while ((entry = zIn.getNextEntry()) != null) {
                if (!entry.isDirectory()) {
                    file = new File(destDirPath, entry.getName());
                    if (!file.exists()) {
                        new File(file.getParent()).mkdirs();//创建此文件的上级目录
                    }
                    OutputStream out = new FileOutputStream(file);
                    BufferedOutputStream bos = new BufferedOutputStream(out);
                    int len = -1;
                    byte[] buf = new byte[1024];
                    while ((len = zIn.read(buf)) != -1) {
                        bos.write(buf, 0, len);
                    }
                    // 关流顺序，先打开的后关闭
                    bos.close();
                    out.close();
                }
            }

        } catch (Exception e) {
            log.error("un7z is error", e);
            return e.getMessage();
        }
        return "";
    }


    private static String un7z(String rootPath, String sourceRarPath, String destDirPath, String passWord) {
        File srcFile = new File(rootPath + sourceRarPath);//获取当前压缩文件
        return un7z(srcFile, destDirPath, passWord);
    }


    private static String unRar(String rootPath, String sourceRarPath, String destDirPath, String passWord) {
        String rarDir = rootPath + sourceRarPath;
        return unRar(new File(rarDir), destDirPath, passWord);
    }

    private static String unRar(File file, String destDirPath, String passWord) {
        String outDir = destDirPath + File.separator;
        RandomAccessFile randomAccessFile = null;
        IInArchive inArchive = null;
        ISimpleInArchive simpleInArchive = null;
        try {
            // 第一个参数是需要解压的压缩包路径，第二个参数参考JdkAPI文档的RandomAccessFile
            randomAccessFile = new RandomAccessFile(file, "r");
            if (isNotBlank(passWord)) {
                inArchive = SevenZip.openInArchive(null, new RandomAccessFileInStream(randomAccessFile), passWord);
            } else {
                inArchive = SevenZip.openInArchive(null, new RandomAccessFileInStream(randomAccessFile));
            }

            simpleInArchive = inArchive.getSimpleInterface();
            for (final ISimpleInArchiveItem item : simpleInArchive.getArchiveItems()) {
                final int[] hash = new int[]{0};
                if (!item.isFolder()) {
                    ExtractOperationResult result;
                    final long[] sizeArray = new long[1];

                    File outFile = new File(outDir + item.getPath());
                    File parent = outFile.getParentFile();
                    if ((!parent.exists()) && (!parent.mkdirs())) {
                        continue;
                    }
                    final FileOutputStream fileOutputStream = new FileOutputStream(outFile, true);
                    if (isNotBlank(passWord)) {
                        result = item.extractSlow(data -> {
                            try {
                                IOUtils.write(data, fileOutputStream);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            } finally {
                                try {
                                    fileOutputStream.close();
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            hash[0] ^= Arrays.hashCode(data); // Consume data
                            sizeArray[0] += data.length;
                            return data.length; // Return amount of consumed
                        }, passWord);
                    } else {
                        result = item.extractSlow(data -> {
                            try {
                                IOUtils.write(data, fileOutputStream);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            } finally {
                                try {
                                    fileOutputStream.close();
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            hash[0] ^= Arrays.hashCode(data); // Consume data
                            sizeArray[0] += data.length;
                            return data.length; // Return amount of consumed
                        });
                    }

                    if (result == ExtractOperationResult.OK) {
                        log.error("解压rar成功...." + String.format("%9X | %10s | %s", hash[0], sizeArray[0], item.getPath()));
                    } else if (isNotBlank(passWord)) {
                        log.error("解压rar成功：密码错误或者其他错误...." + result);
                        return "password";
                    } else {
                        return "rar error";
                    }
                }
            }
        } catch (Exception e) {
            log.error("unRar error", e);
            return e.getMessage();
        } finally {
            if (simpleInArchive != null) {
                try {
                    simpleInArchive.close();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            if (inArchive != null) {
                try {
                    inArchive.close();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            if (randomAccessFile != null) {
                try {
                    randomAccessFile.close();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return "";
    }

    @SuppressWarnings("resource")
    private static String unZip(String rootPath, String sourceRarPath, String destDirPath, String passWord) {
        String filePath = rootPath + sourceRarPath;
        return unZip(new File(filePath), destDirPath, passWord);
    }

    @SuppressWarnings("resource")
    private static String unZip(File file, String destDirPath, String passWord) {
        ZipFile zipFile;
        String result = "";
        try {
            if (isNotBlank(passWord)) {
                zipFile = new ZipFile(file, passWord.toCharArray());
            } else {
                zipFile = new ZipFile(file);
            }
            zipFile.setCharset(Charset.forName("GBK"));
            zipFile.extractAll(destDirPath);
        } catch (Exception e) {
            log.error("unZip error", e);
            return e.getMessage();
        }
        return result;
    }

    /**
     * 获取文件真实类型
     *
     * @param file 要获取类型的文件。
     * @return 文件类型枚举。
     */
    public static String getFileType(File file) {
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            byte[] head = new byte[4];
            if (-1 == inputStream.read(head)) {
                return null;
            }
            int headHex = 0;
            for (byte b : head) {
                headHex <<= 8;
                headHex |= b;
            }
            switch (headHex) {
                case 0x504B0304:
                    return "zip";
                case -0x51:
                    return "7z";
                case 0x52617221:
                    return "rar";
                default:
                    return null;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static boolean isBlank(String str){
        return str == null || "".equals(str);
    }
    private static boolean isNotBlank(String str){
        return !isBlank(str);
    }

}
