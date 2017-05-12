package com.returnlive.app.bean;

/**
 * Created by 王建法 on 2017/3/27.
 */

public class RoutDriver {
    private String startName;
    private String endName;
    private int clunmber;

    public RoutDriver(String startName, String endName) {
        this.startName = startName;
        this.endName = endName;
    }

    public String getStartName() {
        return startName;
    }

    public void setStartName(String startName) {
        this.startName = startName;
    }

    public String getEndName() {
        return endName;
    }

    public void setEndName(String endName) {
        this.endName = endName;
    }

    public int getClunmber() {
        return clunmber;
    }

    public void setClunmber(int clunmber) {
        this.clunmber = clunmber;
    }
}
