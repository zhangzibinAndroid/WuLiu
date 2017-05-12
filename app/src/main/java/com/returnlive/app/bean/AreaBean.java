package com.returnlive.app.bean;

public class AreaBean {
	public String name;
	public String code;
	public boolean isChoose;
	public AreaBean(String name, String code) {
		super();
		this.name = name;
		this.code = code;
	}

	/**
	 * 为了添加“不限”
	 * @param name
     */
	public AreaBean(String name) {
		super();
		this.name = name;

	}
}