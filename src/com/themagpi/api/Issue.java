package com.themagpi.api;

import java.util.Date;

public class Issue {
    
    private String id;
    private String title;
    private Date date;
    private String imgUrl;
    private String pdfUrl;
    
    public static class Builder {

        private String id;
        private String title;
        private Date date;
        private String imgUrl;
        private String pdfUrl;
        
        public Builder id(String id){this.id = id; return this; }
        public Builder title(String title){this.title = title; return this; }
        public Builder date(Date date){this.date = date; return this; }
        public Builder imageUrl(String imgUrl){this.imgUrl = imgUrl; return this; }
        public Builder pdfUrl(String pdfUrl){this.pdfUrl = pdfUrl; return this; }

        public Issue build() {
            return new Issue(this);
        }
    }

    private Issue(Builder builder) {
        setTitle(builder.title);
        setDate(builder.date);
        setCoverUrl(builder.imgUrl);
        setPdfUrl(builder.pdfUrl);
        setId(builder.id);
    }

    public String getPdfUrl() {
        return pdfUrl;
    }

    public void setPdfUrl(String pdfUrl) {
        this.pdfUrl = pdfUrl;
    }

    public String getCoverUrl() {
        return imgUrl;
    }

    public void setCoverUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    public String toString() {
        return this.title;
    }
}
