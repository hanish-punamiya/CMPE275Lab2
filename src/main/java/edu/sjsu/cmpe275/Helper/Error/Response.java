package edu.sjsu.cmpe275.Helper.Error;

public class Response {

    private Error badRequest;

    public Response(String code, String msg) {
        Error Error = new Error(code,msg);
        this.badRequest = Error;
    }

    public Error getBadRequest() {
        return badRequest;
    }

    public void setBadRequest(Error Error) {
        this.badRequest = Error;
    }
}
