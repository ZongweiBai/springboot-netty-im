package com.github.baymin.im.websocketserver.service;

import com.github.baymin.im.websocket.domain.ResponseInfo;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
 * @author zongwei
 */
public interface FileService {

    ResponseInfo upload(MultipartFile file, HttpServletRequest request);
}
