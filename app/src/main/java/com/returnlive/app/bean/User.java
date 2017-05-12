package com.returnlive.app.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by 王建法 on 2017/4/27.
 */

public class User implements Serializable{


    /**
     * state : success
     * id : 7
     * z_session_7 : 1a4b040f0ec0dfbe3785088fb1157cc8
     */

    private String state;
    private String id;

    private String z_session_id;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getZ_session_id() {
        return z_session_id;
    }

    public void setZ_session_id(String z_session_7) {
        this.z_session_id = z_session_7;
    }
}


