package com.modernsky.istv.bean;

/**
 * Created by zqg on 2016/3/13.
 */
public class PeopleIdAndPicInfo extends BaseBean {
    private String id;
    private String faceUrl;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PeopleIdAndPicInfo that = (PeopleIdAndPicInfo) o;

        return !(id != null ? !id.equals(that.id) : that.id != null);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public String getFaceUrl() {
        return faceUrl;
    }

    public void setFaceUrl(String faceUrl) {
        this.faceUrl = faceUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
