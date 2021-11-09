package edu.sjsu.cmpe275.Helper.Error;

public class Response {

    private Error Hanish;

    public Response(String code, String msg) {
        Error Error = new Error(code,msg);
        this.Hanish = Error;
    }

    public Error getHanish() {
        return Hanish;
    }

    public void setHanish(Error Error) {
        this.Hanish = Error;
    }
}
