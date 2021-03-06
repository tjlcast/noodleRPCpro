package com.tjlcast.rpc_common.bean;

/**
 * Created by tangjialiang on 2018/5/5.
 */
public class RpcResponse {
    private String requestId ;
    private Exception exception ;
    private Object result ;


    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public boolean hashException() { return this.exception!=null; }
}
