package com.themagpi.api;

public class News {
	
	private String date;
	private String content;
	
	public News(String date, String content) {
		this.setDate(date);
		this.setContent(content);
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	

}
