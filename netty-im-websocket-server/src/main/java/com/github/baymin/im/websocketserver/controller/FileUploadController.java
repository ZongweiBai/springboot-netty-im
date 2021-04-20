package com.github.baymin.im.websocketserver.controller;

import com.github.baymin.im.websocket.domain.ResponseInfo;
import com.github.baymin.im.websocketserver.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * @author zongwei
 */
@Controller
@RequestMapping("/file")
public class FileUploadController {

    @Autowired
    private FileService fileService;
    
    @RequestMapping(value = "/upload", method = POST)
    @ResponseBody
    public ResponseInfo upload(@RequestParam(value = "file") MultipartFile file, HttpServletRequest request) {
        return fileService.upload(file, request);
    }
}
