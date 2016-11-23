/**
 *
 */
package com.modernsky.istv.bean;

/**
 * @author： fengqingyun2008
 * @Email： fengqingyun2008@gmail.com
 * @version：1.0
 * @创建时间：2015-4-4 下午3:43:44
 * @类说明：
 */
public class AlbumLastVideo extends BaseBean {
    private LatestVideo latestVideo;
    private String name;

    public LatestVideo getLatestVideo() {
        return latestVideo;
    }

    public void setLatestVideo(LatestVideo latestVideo) {
        this.latestVideo = latestVideo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
