package com.modernsky.istv.service;

import java.util.List;

import com.modernsky.istv.bean.MusicInfo;

public class DianTaiService {

    private static DianTaiService dianTai;
    public static String ACTION_SERVICE = "ACTION_SERVICE";
    private static boolean leftOrRight = true;
    private static int marginButtom = 0;
    private boolean shouldPlayInYiDong = false;
    private int marginLeft = 0;

    public int getMarginLeft() {
        return marginLeft;
    }

    public void setMarginLeft(int marginLeft) {
        this.marginLeft = marginLeft;
    }

    public boolean isShouldPlayInYiDong() {
        return shouldPlayInYiDong;
    }

    public void setShouldPlayInYiDong(boolean shouldPlayInYiDong) {
        this.shouldPlayInYiDong = shouldPlayInYiDong;
    }

    public static boolean isLeftOrRight() {
        return leftOrRight;
    }

    public static void setLeftOrRight(boolean leftOrRight) {
        DianTaiService.leftOrRight = leftOrRight;
    }

    public static int getMarginButtom() {
        return marginButtom;
    }

    public static void setMarginButtom(int marginButtom) {
        DianTaiService.marginButtom = marginButtom;
    }

    public static DianTaiService getInstance() {
        return dianTai = dianTai == null ? new DianTaiService() : dianTai;
    }

    private List<MusicInfo> musicList;
    private MusicInfo currentMusicInfo;
    private boolean isplaying;

    public MusicInfo getCurrentMusicInfo() {
        return currentMusicInfo;
    }

    public void setCurrentMusicInfo(MusicInfo currentMusicInfo) {
        this.currentMusicInfo = currentMusicInfo;
    }

    public boolean isIsplaying() {
        return isplaying;
    }

    public void setIsplaying(boolean isplaying) {
        this.isplaying = isplaying;
    }

    public List<MusicInfo> getMusicList() {
        return musicList;
    }

    public void setMusicList(List<MusicInfo> musicList) {
        this.musicList = musicList;
    }

}
