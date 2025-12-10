package com.finance.controller;

import com.finance.util.AliOssUtil;
import com.finance.util.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/common")
@Slf4j
@Tag(name = "通用接口")
public class UploadController {

    @Autowired
    private AliOssUtil aliOssUtil;

    @PostMapping("/upload")
    @Operation(summary = "文件上传")
    public Result<String> upload(MultipartFile file) {
        log.info("文件上传：{}", file.getOriginalFilename());
        try {
            String fileName = file.getOriginalFilename();
            fileName = UUID.randomUUID() + fileName.substring(fileName.lastIndexOf("."));
            String url = aliOssUtil.upload(file.getBytes(), fileName);
            return Result.success(url);
        } catch (IOException e) {
            log.error("文件上传失败: {}", e.getMessage());
            return Result.error("文件上传失败");
        }
    }
}