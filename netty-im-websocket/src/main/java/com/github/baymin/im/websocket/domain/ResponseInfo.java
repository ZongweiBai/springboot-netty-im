package com.github.baymin.im.websocket.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 响应的消息信息
 *
 * @author zongwei
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class ResponseInfo extends MessageInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Integer SUCCESS_STATUS = 200;
    private static final Integer ERROR_STATUS = 500;

    /**
     * 响应码
     */
    private Integer status;

    /**
     * 错误类新
     */
    private String error;

    /**
     * 错误描述
     */
    @JsonProperty(value = "error_description")
    private String errorDescription;

    public ResponseInfo() {
        super();
    }

    public ResponseInfo(MessageInfo messageInfo) {
        super(messageInfo);
    }

    public ResponseInfo success() {
        this.status = SUCCESS_STATUS;
        return this;
    }

    public ResponseInfo error(String error) {
        this.status = ERROR_STATUS;
        this.error = error;
        return this;
    }

}
