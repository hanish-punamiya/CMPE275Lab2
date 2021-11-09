package edu.sjsu.cmpe275.Helper.Error;

public class Response {

    private Error error;

    public Response(String code, String msg) {
        Error Error = new Error(code,msg);
        this.error = Error;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error Error) {
        this.error = Error;
    }
}
