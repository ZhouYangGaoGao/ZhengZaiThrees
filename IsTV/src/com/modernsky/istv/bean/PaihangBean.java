package com.modernsky.istv.bean;

public class PaihangBean extends BaseBean {
    private String id;
    private int mbCount;
    UserEntity user;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getMbCount() {
        return mbCount;
    }

    public void setMbCount(int mbCount) {
        this.mbCount = mbCount;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }
}
