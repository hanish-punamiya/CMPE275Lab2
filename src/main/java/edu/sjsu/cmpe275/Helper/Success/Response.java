package edu.sjsu.cmpe275.Helper.Success;

/**
 * This class is used to wrap the  success response for the APIs
 */
public class Response {
    private String code;
    private String msg;

    public Response(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Response() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
