package com.util.fastdfs.controller;

/**
 * @Author DING-PC
 * @Description
 * @Date 2020-05-03
 **/

import com.alibaba.fastjson.JSONObject;
import com.github.tobato.fastdfs.domain.fdfs.ThumbImageConfig;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.util.fastdfs.utils.FastDFSUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/archives/platform/upload")
public class FastDFSController {

    private Logger logger = LogManager.getLogger(FastDFSController.class);

    @Autowired
    private FastFileStorageClient fastFileStorageClient;

    @Autowired
    private ThumbImageConfig thumbImageConfig;

    @Autowired
    private FastDFSUtil fastDFSUtil;

//    /**
//     * 上传图片同时生成默认大小缩略图
//     * @param file
//     */
//    @PostMapping("/img")
//    public void uploadImage(@RequestParam("file") MultipartFile file){
//        if (file.isEmpty()) {
//            logger.error("上传对象【MultipartFile】为空！");
//            throw new WmsException("1222", "请选择有效文件!");
//        }
//        Set<MetaData> metaDataSet = new HashSet<>();
//        metaDataSet.add(new MetaData("author", "ANJI"));
//        metaDataSet.add(new MetaData("createDate", new Date().toString()));
//        try {
//            logger.info("开始上传----->>");
//            StorePath storePath = fastFileStorageClient.uploadImageAndCrtThumbImage(file.getInputStream(), file.getSize(), FilenameUtils.getExtension(file.getOriginalFilename()), metaDataSet);
//            logger.info("上传结束----->>StorePath:【{}】",storePath);
//            Set<MetaData> metadata = fastFileStorageClient.getMetadata(storePath.getGroup(), storePath.getPath());
//            logger.info("元数据----->>Set<MetaData>:【{}】",metadata);
//            String fullPath = storePath.getFullPath();
//            String path = storePath.getPath();
//            logger.info("图片路径----->>fullPath：【{}】--->>path：【{}】",fullPath,path);
//            String thumbImagePath = thumbImageConfig.getThumbImagePath(storePath.getPath());
//            logger.info("缩略图路径----->>thumbImagePath：【{}】",thumbImagePath);
//        } catch (IOException e) {
//            logger.error("上传失败！检查FastDFS服务");
//            e.printStackTrace();
//        }
//    }

//    /**
//     * 上传图片同时生成指定大小缩略图
//     * 指定缩略图大小
//     * @param file
//     */
//    @PostMapping("/imgSingle")
//    public JSONObject uploadimgCustom(@RequestParam("file") MultipartFile file) throws Exception {
//        if (file.isEmpty()) {
//            logger.error("上传对象【MultipartFile】为空！");
//            throw new WmsException("1222", "请选择有效文件！");
//        }
//        JSONObject jsonObject = new JSONObject();
//        try {
//            FastImageFile fastImageFile = crtFastImageFileOnly(file);
//            logger.info("开始上传----->>FastImageFile：【{}】",fastImageFile);
//            StorePath storePath = fastFileStorageClient.uploadImage(fastImageFile);
//            logger.info("上传结束----->>StorePath:【{}】",storePath);
//
//            Set<MetaData> metadata = fastFileStorageClient.getMetadata(storePath.getGroup(), storePath.getPath());
//            logger.info("元数据----->>Set<MetaData>:【{}】",metadata);
//
//            String fullPath = storePath.getFullPath();
//            logger.info("图片路径----->>fullPath：【{}】",fullPath);
//            // 获取缩略图路径
//            //String path = thumbImageConfig.getThumbImagePath(storePath.getPath());
//            jsonObject.put("imgUrl",fullPath);
//        } catch (IOException e) {
//            logger.error("上传失败！检查FastDFS服务");
//            e.printStackTrace();
//        }
//        return jsonObject;
//    }


    /**
     * 上传多个图片
     *
     * @param files
     */
    @PostMapping("/imgMultiple")
    public List<JSONObject> uploadimgCustom(@RequestPart("files") MultipartFile[] files) {
        List<JSONObject> jsonRes = null;
        if (files != null && files.length > 0) {
            jsonRes = fastDFSUtil.uploadimgCustomUtil(files);
        } else {
            logger.error("上传失败！上传对象【MultipartFile】为空！!");
        }
        if (CollectionUtils.isEmpty(jsonRes)) {
            logger.error("上传失败！");
        }
        return jsonRes;
    }

    /**
     * 上传多个文件
     *
     * @param files
     */
    @PostMapping("/fileMultiple")
    public List<JSONObject> uploadFileCustom(@RequestPart("files") MultipartFile[] files) {
        List<JSONObject> jsonRes = null;
        if (files != null && files.length > 0) {
            jsonRes = fastDFSUtil.uploadFileCustomUtil(files);
        } else {
            logger.error("上传失败！上传对象【MultipartFile】为空！!");
        }
        if (CollectionUtils.isEmpty(jsonRes)) {
            logger.error("上传失败！");
        }
        return jsonRes;
    }

//    /**
//     * 上传多个图片
//     *
//     * @param files
//     */
//    @PostMapping("/imgMultiple")
//    @LoggerManage(description = "上传图片")
//    public BaseResponseModel<Object> uploadimgCustom(@RequestPart("files") MultipartFile[] files){
//        BaseResponseModel<Object> response = new BaseResponseModel<>();
//        List<JSONObject> jsonRes = new ArrayList<>();
//        if (files != null && files.length > 0) {
//            try {
//                for (int i = 0; i < files.length; i++) {
//                    MultipartFile mltfile = files[i];
//                    if (!mltfile.isEmpty()) {
//                        FastImageFile fastImageFile = crtFastImageFileOnly(mltfile);
//                        logger.info("开始上传第【{}】张图片----->>FastImageFile：【{}】",i,fastImageFile);
//                        StorePath storePath = fastFileStorageClient.uploadImage(fastImageFile);
//                        logger.info("上传结束----->>StorePath:【{}】",storePath);
//                        //拿到元数据
//                        Set<MetaData> metadata = fastFileStorageClient.getMetadata(storePath.getGroup(), storePath.getPath());
//                        String fullPath = storePath.getFullPath();
//                        // 获取缩略图路径
//                        //String path = thumbImageConfig.getThumbImagePath(storePath.getPath());
//                        JSONObject jsonObject = new JSONObject();
//                        jsonObject.put("imgUrl",fullPath);
//                        jsonRes.add(jsonObject);
//                    }
//                }
//            }catch (Exception e){
//                logger.error("上传失败！检查FastDFS服务");
//                e.printStackTrace();
//            }
//        } else {
//            logger.error("上传失败！上传对象【MultipartFile】为空！!");
//            throw new WmsException("1222", "请选择有效文件！");
//        }
//        response.setRepCode(RespCode.SUCCESS);
//        response.setRepMsg(RespMsg.SUCCESS_MSG);
//        response.setRepData(jsonRes);
//        return response;
//    }

//    /**
//     * 只上传文件
//     *
//     * @return
//     * @throws Exception
//     */
//    private FastImageFile crtFastImageFileOnly(MultipartFile file) throws Exception {
//        InputStream in = file.getInputStream();
//        Set<MetaData> metaDataSet = new HashSet<>();
//        metaDataSet.add(new MetaData("author", "ANJI"));
//        metaDataSet.add(new MetaData("createDate", new Date().toString()));
//        String name = file.getOriginalFilename();
//        String fileExtName = FilenameUtils.getExtension(name);
//        return new FastImageFile.Builder()
//                .withFile(in, file.getSize(), fileExtName)
//                .withMetaData(metaDataSet)
//                .build();
//    }
//
//    @GetMapping("test")
//    public String test() throws Exception {
//        return "Hi FastDFSController";
//    }
}

