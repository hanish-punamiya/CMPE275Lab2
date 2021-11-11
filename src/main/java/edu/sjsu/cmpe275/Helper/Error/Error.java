package edu.sjsu.cmpe275.Helper.Error;

/**
 * This class is used to generate the given error output
 */
public class Error {

    private String code;
    private String msg;

    public Error(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Error() {
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
