package org.com.code.certificateProcessor.responseHandler;

import com.alibaba.fastjson.JSON;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ResponseHandler {
    public static final int SUCCESS = 200;
    public static final int PROCESSING = 102;
    public static final int BAD_REQUEST = 400;
    public static final int UNAUTHORIZED = 401;
    public static final int NOT_FOUND = 404;
    public static final int SERVER_ERROR = 500;

    private int code;
    private String message;
    private Object data;

    public ResponseHandler(int code, String message){
        this.code = code;
        this.message = message;
        this.data = null;
    }
    public String toJSONString(){
        return JSON.toJSONString(this);
    }
}
