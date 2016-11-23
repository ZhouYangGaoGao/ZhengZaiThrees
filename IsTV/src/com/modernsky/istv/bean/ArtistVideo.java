/**
 *
 */
package com.modernsky.istv.bean;

import java.util.List;

/**
 * @author： fengqingyun2008
 * @Email： fengqingyun2008@gmail.com
 * @version：1.0
 * @创建时间：2015-4-4 下午3:43:44
 * @类说明：
 */
public class ArtistVideo extends BaseBean {
    private String name;
    private int targetId;
    private String standardPic;
    private List<LatestVideo> data;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStandardPic() {
        return standardPic;
    }

    public void setStandardPic(String standardPic) {
        this.standardPic = standardPic;
    }

    public List<LatestVideo> getData() {
        return data;
    }

    public void setData(List<LatestVideo> data) {
        this.data = data;
    }

    public int getTargetId() {
        return targetId;
    }

    public void setTargetId(int targetId) {
        this.targetId = targetId;
    }

}
