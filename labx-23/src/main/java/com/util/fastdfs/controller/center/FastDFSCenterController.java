package com.util.fastdfs.controller.center;

import com.alibaba.fastjson.JSONObject;

import com.util.fastdfs.controller.FastDFSController;
import com.util.fastdfs.utils.FastDFSUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/payment/center/upload")
public class FastDFSCenterController {

    private Logger logger = LogManager.getLogger(FastDFSController.class);

    @Autowired
    private FastDFSUtil fastDFSUtil;

    /**
     * 上传多个图片
     *
     * @param files
     */
    @PostMapping("/imgMultiple")
    public List<JSONObject>uploadimgCustom(@RequestPart("files") MultipartFile[] files) {
        List<JSONObject> jsonRes = null;
        if (files != null && files.length > 0) {
            jsonRes = fastDFSUtil.uploadimgCustomUtil(files);
        } else {
            logger.error("上传失败！上传对象【MultipartFile】为空！!");
            throw new RuntimeException();
        }
        if (CollectionUtils.isEmpty(jsonRes)) {
            logger.error("上传失败！");
            throw new RuntimeException();
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
            throw new RuntimeException();
        }
        if (CollectionUtils.isEmpty(jsonRes)) {
            logger.error("上传失败！");
            throw new RuntimeException();
        }
        return jsonRes;
    }

    @GetMapping("/echo")
    public String echo(String name) {
        return "provider:" + name;
    }

}
