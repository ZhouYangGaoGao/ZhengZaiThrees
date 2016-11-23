package com.modernsky.istv.bean;

public class MusicInfo extends BaseBean {
    private String albumId;
    private String albumName;
    private String bigPic;
    private String description;
    private String id;
    private String name;
    private String shortName;
    private String smallPic;
    private String starringIds;
    private String starringNames;
    private String islike;

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getBigPic() {
        return bigPic;
    }

    public void setBigPic(String bigPic) {
        this.bigPic = bigPic;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getSmallPic() {
        return smallPic;
    }

    public void setSmallPic(String smallPic) {
        this.smallPic = smallPic;
    }

    public String getStarringIds() {
        return starringIds;
    }

    public void setStarringIds(String starringIds) {
        this.starringIds = starringIds;
    }

    public String getStarringNames() {
        return starringNames;
    }

    public void setStarringNames(String starringNames) {
        this.starringNames = starringNames;
    }

    public String getIslike() {
        return islike;
    }

    public void setIslike(String islike) {
        this.islike = islike;
    }

    public MusicInfo(String albumId, String albumName, String bigPic,
                     String description, String id, String name, String shortName,
                     String smallPic, String starringIds, String starringNames,
                     String islike) {
        super();
        this.albumId = albumId;
        this.albumName = albumName;
        this.bigPic = bigPic;
        this.description = description;
        this.id = id;
        this.name = name;
        this.shortName = shortName;
        this.smallPic = smallPic;
        this.starringIds = starringIds;
        this.starringNames = starringNames;
        this.islike = islike;
    }

    public MusicInfo() {
        super();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MusicInfo other = (MusicInfo) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}
