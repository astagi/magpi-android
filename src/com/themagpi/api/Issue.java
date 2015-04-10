package com.themagpi.api;

import android.os.Parcel;
import android.os.Parcelable;

public class Issue implements Parcelable {
    
    private String id;
    private String title;
    private String date;
    private String imgUrl;
    private String pdfUrl;
    private String editorial;
    private String issuuUrl;
    
    public static class Builder {

        private String id;
        private String title;
        private String date;
        private String imgUrl;
        private String pdfUrl;
        private String editorial;
        private String issuuUrl;
        
        public Builder id(String id){this.id = id; return this; }
        public Builder title(String title){this.title = title; return this; }
        public Builder date(String date){this.date = date; return this; }
        public Builder imageUrl(String imgUrl){this.imgUrl = imgUrl; return this; }
        public Builder pdfUrl(String pdfUrl){this.pdfUrl = pdfUrl; return this; }
        public Builder editorial(String editorial){this.editorial = editorial; return this; }
        public Builder issuuUrl(String issuuUrl){this.issuuUrl = issuuUrl; return this; }
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
        setEditorial(builder.editorial);
        setISSUU(builder.issuuUrl);
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(pdfUrl);
        dest.writeString(imgUrl);
        dest.writeString(date);
        dest.writeString(editorial);
        dest.writeString(issuuUrl);
    }
    
    public static final Parcelable.Creator CREATOR =
            new Parcelable.Creator() {
                    public Issue createFromParcel(Parcel in) {
                        return new Issue(in);
                    }
         
                    public Issue[] newArray(int size) {
                        return new Issue[size];
                    }
                };
    
    private void readFromParcel(Parcel in) {
        id = in.readString();
        title = in.readString();
        pdfUrl = in.readString();
        imgUrl = in.readString();
        date = in.readString();
        editorial = in.readString();
        setISSUU(in.readString());
    }
    
    public Issue(Parcel in) {
        readFromParcel(in);
    }
    
    public String toString() {
        return this.title;
    }

	public String getEditorial() {
		return editorial;
	}

	public void setEditorial(String editorial) {
		this.editorial = editorial;
	}

	public String getISSUU() {
		return issuuUrl;
	}

	public void setISSUU(String issuuUrl) {
		this.issuuUrl = issuuUrl;
	}
}
