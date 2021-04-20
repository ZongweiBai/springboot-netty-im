package com.github.baymin.im.websocketserver.service.impl;

import com.github.baymin.im.websocket.domain.ResponseInfo;
import com.github.baymin.im.websocketserver.service.FileService;
import com.github.baymin.im.websocketserver.util.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * @author zongwei
 */
@Slf4j
@Service
public class FileServiceImpl implements FileService {

    private final static String SERVER_URL_PREFIX = "http://localhost:8080/WebSocket/";
    private final static String FILE_STORE_PATH = "UploadFile";
    
    @Override
    public ResponseInfo upload(MultipartFile file, HttpServletRequest request) {
        // 重命名文件，防止重名
        String filename = getRandomUUID();
        String suffix = "";
        String originalFilename = file.getOriginalFilename();
        String fileSize = FileUtils.getFormatSize(file.getSize());
        // 截取文件的后缀名
        if (originalFilename.contains(".")) {
            suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        filename = filename + suffix;
//        String prefix = request.getSession().getServletContext().getRealPath("/") + FILE_STORE_PATH;
        String prefix = "D:\\";
        System.out.println("存储路径为:" + prefix + "\\" + filename);
        Path filePath = Paths.get(prefix, filename);
        try {
            Files.copy(file.getInputStream(), filePath);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseInfo().error("文件上传发生错误！");
        }
        ResponseInfo responseInfo = new ResponseInfo().success();
        responseInfo.setOriginalFilename(originalFilename);
        responseInfo.setFileSize(fileSize);
        responseInfo.setFileUrl(SERVER_URL_PREFIX + FILE_STORE_PATH + "\\" + filename);
        return responseInfo;
    }

    private String getRandomUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

}
