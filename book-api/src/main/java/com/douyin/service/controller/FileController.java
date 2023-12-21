package com.douyin.service.controller;

import com.douyin.service.MinIOConfig;
import com.douyin.service.grace.GraceJSONResult;
import com.douyin.service.utils.MinIOUtils;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Api(tags = "FileController文件上传接口测试")
@RestController
@Slf4j
public class FileController {

    @Autowired
    private MinIOConfig minIOConfig;

    @PostMapping("upload")
    public GraceJSONResult upload(MultipartFile file) throws Exception{
        // 得到文件名
        String filename = file.getOriginalFilename();

        MinIOUtils.uploadFile(minIOConfig.getBucketName(), filename, file.getInputStream());
        String imgUrl = minIOConfig.getFileHost()
                            + "/" + minIOConfig.getBucketName()
                            + "/" + filename;
        return GraceJSONResult.ok(imgUrl);
    }
}
