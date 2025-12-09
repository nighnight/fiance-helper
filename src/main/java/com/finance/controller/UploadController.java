package com.finance.controller;

import com.finance.exception.BusinessException;
import com.finance.service.FinanceRecordService; // 用于更新凭证URL
import com.finance.util.Result;
import com.finance.util.ResultUtil;
import com.finance.vo.LoginUserVO;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Controller
@RequestMapping("/upload")
public class UploadController {

    @Value("${file.upload-dir}") // 在 application.yml 中配置上传目录
    private String uploadDir;

    @Autowired
    private FinanceRecordService financeRecordService;
    @Autowired
    private HttpSession session;

    private Long getUserId() {
        LoginUserVO loginUser = (LoginUserVO) session.getAttribute("loginUser");
        if (loginUser == null || loginUser.getId() == null) {
            throw new BusinessException("用户未登录或会话已过期");
        }
        return loginUser.getId();
    }

    /**
     * 文件上传API（例如凭证图片）
     * @param file 上传的文件
     * @param recordId 如果是更新记录的凭证，传入记录ID
     * @return JSON结果 (包含文件访问URL)
     */
    @PostMapping("/image")
    @ResponseBody
    public Result<String> uploadImage(@RequestParam("file") MultipartFile file,
                                      @RequestParam(value = "recordId", required = false) Long recordId) {
        if (file.isEmpty()) {
            return ResultUtil.error(400, "上传文件不能为空");
        }
        if (file.getSize() > 5 * 1024 * 1024) { // 限制5MB
            return ResultUtil.error(400, "文件大小不能超过5MB");
        }
        if (!file.getContentType().startsWith("image/")) {
            return ResultUtil.error(400, "只允许上传图片文件");
        }

        try {
            // 确保上传目录存在
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 生成唯一文件名，防止重复覆盖
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
            Path filePath = uploadPath.resolve(uniqueFileName);

            // 保存文件
            Files.copy(file.getInputStream(), filePath);

            // 构建文件访问URL (此处简化，实际可能需要Nginx等配置静态资源访问)
            // 假设可以通过 /uploads/ 作为前缀访问到上传的文件
            String fileUrl = "/uploads/" + uniqueFileName;

            // 如果有 recordId，更新记录的凭证URL
            if (recordId != null) {
                Long userId = getUserId();
                financeRecordService.uploadVoucher(userId, recordId, fileUrl);
            }

            return ResultUtil.success("文件上传成功", fileUrl);
        } catch (IOException e) {
            e.printStackTrace();
            return ResultUtil.error(500, "文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 额外补充：配置图片资源访问路径
     * 在 WebConfig.java 中添加：
     * @Override
     * public void addResourceHandlers(ResourceHandlerRegistry registry) {
     *     registry.addResourceHandler("/static/**")
     *             .addResourceLocations("classpath:/static/");
     *     // 添加对上传文件的访问映射
     *     registry.addResourceHandler("/uploads/**")
     *             .addResourceLocations("file:" + uploadDir); // 注意：file: 前缀
     * }
     *
     * 并在 application.yml 中配置：
     * file:
     *   upload-dir: /path/to/your/upload/directory # 比如: D:/finance_helper_uploads/ 或 /var/finance_helper_uploads/
     *
     * 此外，在实际生产环境中，通常会将上传文件存储到对象存储服务（如OSS、COS）或专门的文件服务器，并通过CDN对外提供访问。
     * 直接映射本地文件系统在分布式部署和高可用性方面存在问题。
     */
}