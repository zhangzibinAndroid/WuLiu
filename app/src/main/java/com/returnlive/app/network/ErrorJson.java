package com.returnlive.app.network;

/**
 * Created by zzb on 2017/4/10.
 */

public class ErrorJson  {

    /**
     * state : error
     * code : -11103
     */

    private String state;
    public String code;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
