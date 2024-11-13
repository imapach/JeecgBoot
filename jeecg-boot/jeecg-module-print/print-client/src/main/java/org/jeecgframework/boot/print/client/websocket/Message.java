package org.jeecgframework.boot.print.client.websocket;


import cn.hutool.json.JSONUtil;
import lombok.Data;

@Data
public class Message<T> {
    private Type type;
    private T data;

    public Message(Type type, T data) {
        this.type = type;
        this.data = data;
    }


    @Override
    public String toString() {

       return JSONUtil.toJsonStr(this);

    }

    public enum Type {
        UPLOAD_PRINTER,
    }
}
