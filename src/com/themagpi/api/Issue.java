package com.themagpi.api;

import java.util.Date;

public class Issue {
	
	private String title;
	private Date date;
	private String imgUrl;
	private String pdfUrl;
	
    public static class Builder {

    	private String title;
    	private Date date;
    	private String imgUrl;
    	private String pdfUrl;
    	
        public Builder title(String title){this.title = title; return this; }
        public Builder date(Date date){this.date = date; return this; }
        public Builder imageUrl(String imgUrl){this.imgUrl = imgUrl; return this; }
        public Builder pdfUrl(String pdfUrl){this.pdfUrl = pdfUrl; return this; }

        public Issue build() {
            return new Issue(this);
        }
    }

    private Issue(Builder builder) {
        title = builder.title;
        date = builder.date;
        imgUrl = builder.imgUrl;
        pdfUrl = builder.pdfUrl;
    }
}
