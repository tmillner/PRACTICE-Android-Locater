package com.trevor.showcase.locatractivity;

/**
 * Created by trevormillner on 2/4/18.
 */

public class GalleryItem {

    private String mCaption;
    private String mId;
    private String mUrl;
    private Double mLat;
    private Double mLong;

    public Double getLat() {
        return mLat;
    }

    public void setLat(Double lat) {
        mLat = lat;
    }

    public Double getLong() {
        return mLong;
    }

    public void setLong(Double lon) {
        mLong = lon;
    }

    public String getCaption() {
        return mCaption;
    }

    public void setCaption(String caption) {
        mCaption = caption;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    @Override
    public String toString() {
        return mCaption;
    }
}
