package com.util.fastdfs.utils;

import com.alibaba.fastjson.JSONObject;
import com.github.tobato.fastdfs.domain.fdfs.MetaData;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.domain.fdfs.ThumbImageConfig;
import com.github.tobato.fastdfs.domain.upload.FastFile;
import com.github.tobato.fastdfs.domain.upload.FastImageFile;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Component
@RefreshScope
public class FastDFSUtil {
    private Logger logger = LogManager.getLogger(FastDFSUtil.class);

    @Autowired
    private FastFileStorageClient fastFileStorageClient;

    @Autowired
    private ThumbImageConfig thumbImageConfig;

//    private static final String FILE_TYPE = "rar|zip|doc|docx|pdf|xls|xlsx|jpg|jpeg|png|bmp";
//
//    private static final String IMG_TYPE = "pdf|jpg|jpeg|png|bmp";

    private static final String[] FILE_TYPE_ARRAY = {"rar", "zip", "doc", "docx", "pdf", "xls", "xlsx", "jpg", "jpeg", "png", "bmp", "txt"};

    @Value("${fastdfs.storage.ip:http://10.108.2.56}")
    private String storageIps;

    public void uploadImageUtil(MultipartFile file) {
        if (file.isEmpty()) {
            logger.error("上传对象【MultipartFile】为空！");
            return;
        }
        Set<MetaData> metaDataSet = new HashSet<MetaData>();
        metaDataSet.add(new MetaData("author", "ANJI"));
        metaDataSet.add(new MetaData("createDate", new Date().toString()));
        try {
            logger.info("开始上传----->>");
            StorePath storePath = fastFileStorageClient.uploadImageAndCrtThumbImage(file.getInputStream(), file.getSize(), FilenameUtils.getExtension(file.getOriginalFilename()), metaDataSet);
            logger.info("上传结束----->>StorePath:【{}】", storePath);
//            Set<MetaData> metadata = fastFileStorageClient.getMetadata(storePath.getGroup(), storePath.getPath());
//            String fullPath = storePath.getFullPath();
//            String path = storePath.getPath();
//            String thumbImagePath = thumbImageConfig.getThumbImagePath(storePath.getPath());
        } catch (IOException e) {
            logger.error("上传失败！检查FastDFS服务");
            e.printStackTrace();
        }
    }

    public JSONObject uploadimgCustomUtil(MultipartFile file) {
        if (file.isEmpty()) {
            logger.error("上传对象【MultipartFile】为空！");
            return null;
        }
        JSONObject jsonObject = new JSONObject();
        try {
            FastImageFile fastImageFile = crtFastImageFileOnly(file);

            logger.info("开始上传----->>FastImageFile：【{}】", fastImageFile);

            StorePath storePath = fastFileStorageClient.uploadImage(fastImageFile);

            logger.info("上传结束----->>StorePath:【{}】", storePath);

            Set<MetaData> metadata = fastFileStorageClient.getMetadata(storePath.getGroup(), storePath.getPath());
            logger.info("元数据----->>Set<MetaData>:【{}】", metadata);

            String fullPath = storePath.getFullPath();
            logger.info("图片路径----->>fullPath：【{}】", fullPath);
            // 获取缩略图路径
            //String path = thumbImageConfig.getThumbImagePath(storePath.getPath());
            jsonObject.put("imgUrl", fullPath);
        } catch (Exception e) {
            logger.error("上传失败！检查FastDFS服务");
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * 单文件上传
     * 仅提供给后端使用
     */
    public JSONObject uploadFileUtil(MultipartFile mltfile) {
        JSONObject jsonObject = null;
        if (mltfile != null) {
            logger.info("不做任何处理！正常进入上传文件主流程！");
        } else {
            logger.error("上传失败！上传对象【MultipartFile】为空！!");
            return null;
        }
        if (!checkFileSize(mltfile, 50, "M")) {
            logger.error("文件过大上传失败");
            return null;
        }
        try {
            if (!mltfile.isEmpty()) {
                FastFile fastFile = crtFastFileOnly(mltfile);
                String originalFilename = mltfile.getOriginalFilename();
                logger.info("开始上传文件----->>FastImageFile：【{}】,文件名：【{}】", fastFile, originalFilename);
                StorePath storePath = fastFileStorageClient.uploadFile(fastFile);
                logger.info("上传结束----->>StorePath:【{}】", storePath);
                //拿到元数据
                Set<MetaData> metadata = fastFileStorageClient.getMetadata(storePath.getGroup(), storePath.getPath());
                String fullPath = storePath.getFullPath();

                jsonObject = new JSONObject();
                String fileUrl = storageIps + "/" + fullPath;
                jsonObject.put("state", 0);
                jsonObject.put("imgUrl", fileUrl);
                jsonObject.put("fileName", originalFilename);
                jsonObject.put("attName", fileUrl + "?attName=" + originalFilename);
            }
        } catch (Exception e) {
            logger.error("上传失败！检查FastDFS服务:", e);
            jsonObject = new JSONObject();
            jsonObject.put("state", 1);
            jsonObject.put("imgUrl", null);
            jsonObject.put("fileName", null);
            jsonObject.put("attName", null);
        }
        return jsonObject;
    }

    /**
     * 上传多个图片
     *
     * @param files
     */
    public List<JSONObject> uploadimgCustomUtil(MultipartFile[] files) {
        List<JSONObject> jsonRes = new ArrayList<JSONObject>();
        if (files != null && files.length > 0) {
            //不做任何处理！正常进入上传文件主流程
            logger.info("不做任何处理！正常进入上传文件主流程！");
        } else {
            logger.error("上传失败！上传对象【MultipartFile】为空！");
            return null;
        }
        if (!checkFileSize(files, 5, "M")) {
            logger.error("文件过大上传失败");
            return null;
        }
        for (int i = 0; i < files.length; i++) {
            try {
                MultipartFile mltfile = files[i];
                if (!mltfile.isEmpty()) {
                    FastImageFile fastImageFile = crtFastImageFileOnly(mltfile);
                    String originalFilename = mltfile.getOriginalFilename();
                    logger.info("开始上传第【{}】张图片----->>FastImageFile：【{}】,文件名：【{}】", i + 1, fastImageFile, originalFilename);
                    StorePath storePath = fastFileStorageClient.uploadImage(fastImageFile);
                    logger.info("上传结束----->>StorePath:【{}】", storePath);
                    //拿到元数据
                    Set<MetaData> metadata = fastFileStorageClient.getMetadata(storePath.getGroup(), storePath.getPath());
                    String fullPath = storePath.getFullPath();
                    // 获取缩略图路径
                    //String path = thumbImageConfig.getThumbImagePath(storePath.getPath());
                    JSONObject jsonObject = new JSONObject();
                    String fileUrl = storageIps + "/" + fullPath;
                    jsonObject.put("fileOrder", i + 1);
                    jsonObject.put("state", 0);
                    jsonObject.put("imgUrl", fileUrl);
                    jsonObject.put("fileName", originalFilename);
                    jsonObject.put("attName", fileUrl + "?attName=" + originalFilename);
                    jsonRes.add(jsonObject);
                }
            } catch (Exception e) {
                logger.error("上传失败！检查FastDFS服务:", e);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("fileOrder", i + 1);
                jsonObject.put("state", 1);
                jsonObject.put("imgUrl", null);
                jsonObject.put("fileName", null);
                jsonObject.put("attName", null);
                jsonRes.add(jsonObject);
            }
        }
        return jsonRes;
    }

    /**
     * 上传多个文件
     *
     * @param files
     */
    public List<JSONObject> uploadFileCustomUtil(MultipartFile[] files) {
        List<JSONObject> jsonRes = new ArrayList<JSONObject>();
        if (files != null && files.length > 0) {
            //不做任何处理！正常进入上传文件主流程
            logger.info("不做任何处理！正常进入上传文件主流程！");
        } else {
            logger.error("上传失败！上传对象【MultipartFile】为空！!");
            return null;
        }
        if (!checkFileSize(files, 50, "M")) {
            logger.error("文件过大上传失败");
            return null;
        }
        if (!checkFileType(files, FILE_TYPE_ARRAY)) {
            logger.error("非法上传的文件类型");
            return null;
        }
        for (int i = 0; i < files.length; i++) {
            try {
                MultipartFile mltfile = files[i];
                if (!mltfile.isEmpty()) {
                    FastFile fastFile = crtFastFileOnly(mltfile);
                    String originalFilename = mltfile.getOriginalFilename();
                    logger.info("开始上传第【{}】个文件----->>FastImageFile：【{}】,文件名：【{}】", i + 1, fastFile, originalFilename);
                    StorePath storePath = fastFileStorageClient.uploadFile(fastFile);
                    logger.info("上传结束----->>StorePath:【{}】", storePath);
                    //拿到元数据
                    Set<MetaData> metadata = fastFileStorageClient.getMetadata(storePath.getGroup(), storePath.getPath());
                    String fullPath = storePath.getFullPath();

                    JSONObject jsonObject = new JSONObject();
                    String fileUrl = storageIps + "/" + fullPath;
                    jsonObject.put("fileOrder", i + 1);
                    jsonObject.put("state", 0);
                    jsonObject.put("imgUrl", fileUrl);
                    jsonObject.put("fileName", originalFilename);
                    jsonObject.put("attName", fileUrl + "?attName=" + originalFilename);
                    jsonRes.add(jsonObject);
                }
            } catch (Exception e) {
                logger.error("上传失败！检查FastDFS服务:", e);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("fileOrder", i + 1);
                jsonObject.put("state", 1);
                jsonObject.put("imgUrl", null);
                jsonObject.put("fileName", null);
                jsonObject.put("attName", null);
                jsonRes.add(jsonObject);
            }
        }
        return jsonRes;
    }

    /**
     * 上传文件格式校验
     *
     * @param files
     * @return
     */
    private boolean checkFileType(MultipartFile[] files, String[] objectType) {
        Boolean flag = Boolean.TRUE;
        for (MultipartFile file : files) {
            String originalFilename = file.getOriginalFilename();
            String type = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
            List<String> objectTypeArray = Arrays.asList(objectType);
            if (!objectTypeArray.contains(type)) {
                flag = Boolean.FALSE;
            }
        }
        return flag;
    }

    /**
     * 判断多文件总大小
     *
     * @param files
     * @param size
     * @param unit
     * @return
     */
    private boolean checkFileTotalSize(MultipartFile[] files, int size, String unit) {
        Long len = 0L;
        for (MultipartFile file : files) {
            len = len + file.getSize();
        }

        double fileSize = 0;
        if ("B".equals(unit.toUpperCase())) {
            fileSize = (double) len;
        } else if ("K".equals(unit.toUpperCase())) {
            fileSize = (double) len / 1024;
        } else if ("M".equals(unit.toUpperCase())) {
            fileSize = (double) len / 1048576;
        } else if ("G".equals(unit.toUpperCase())) {
            fileSize = (double) len / 1073741824;
        }
        if (fileSize > size) {
            logger.error("上传失败！上传对象过大SIZE：【{}】unit", fileSize);
            return false;
        }
        return true;
    }

    /**
     * 判断单文件大小
     *
     * @param file
     * @param size
     * @param unit
     * @return
     */
    private boolean checkFileSize(MultipartFile file, int size, String unit) {
        Long len = file.getSize();
        double fileSize = 0;
        if ("B".equals(unit.toUpperCase())) {
            fileSize = (double) len;
        } else if ("K".equals(unit.toUpperCase())) {
            fileSize = (double) len / 1024;
        } else if ("M".equals(unit.toUpperCase())) {
            fileSize = (double) len / 1048576;
        } else if ("G".equals(unit.toUpperCase())) {
            fileSize = (double) len / 1073741824;
        }
        if (fileSize > size) {
            logger.error("上传失败！上传对象过大SIZE：【{}】unit", fileSize);
            return false;
        }
        return true;
    }

    /**
     * 判断单文件大小
     *
     * @param files
     * @param size
     * @param unit
     * @return
     */
    private boolean checkFileSize(MultipartFile[] files, int size, String unit) {
        for (MultipartFile file : files) {
            Long len = file.getSize();
            double fileSize = 0;
            if ("B".equals(unit.toUpperCase())) {
                fileSize = (double) len;
            } else if ("K".equals(unit.toUpperCase())) {
                fileSize = (double) len / 1024;
            } else if ("M".equals(unit.toUpperCase())) {
                fileSize = (double) len / 1048576;
            } else if ("G".equals(unit.toUpperCase())) {
                fileSize = (double) len / 1073741824;
            }
            if (fileSize > size) {
                logger.error("上传失败！上传对象过大SIZE：【{}】unit", fileSize);
                return false;
            }
        }

        return true;
    }

    /**
     * 上传文件
     *
     * @param file
     * @return
     * @throws Exception
     */
    private FastFile crtFastFileOnly(MultipartFile file) throws Exception {
        InputStream in = file.getInputStream();
        Set<MetaData> metaDataSet = new HashSet<MetaData>();
        metaDataSet.add(new MetaData("author", "ANJI"));
        metaDataSet.add(new MetaData("createDate", new Date().toString()));
        String name = file.getOriginalFilename();
        String fileExtName = FilenameUtils.getExtension(name);
        return new FastFile.Builder()
                .withFile(in, file.getSize(), fileExtName)
                .withMetaData(metaDataSet)
                .build();
    }

    /**
     * 只上传图片
     *
     * @return
     * @throws Exception
     */
    private FastImageFile crtFastImageFileOnly(MultipartFile file) throws Exception {
        InputStream in = file.getInputStream();
        Set<MetaData> metaDataSet = new HashSet<MetaData>();
        metaDataSet.add(new MetaData("author", "ANJI"));
        metaDataSet.add(new MetaData("createDate", new Date().toString()));
        String name = file.getOriginalFilename();
        String fileExtName = FilenameUtils.getExtension(name);
        return new FastImageFile.Builder()
                .withFile(in, file.getSize(), fileExtName)
                .withMetaData(metaDataSet)
                .build();
    }
}
