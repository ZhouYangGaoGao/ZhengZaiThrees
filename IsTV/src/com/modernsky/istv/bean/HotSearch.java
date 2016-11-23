/**
 *
 */
package com.modernsky.istv.bean;

import java.util.List;

/**
 * @author： fengqingyun2008
 * @Email： fengqingyun2008@gmail.com
 * @version：1.0
 * @创建时间：2015-6-19 下午4:07:33
 * @类说明：
 */
public class HotSearch extends BaseBean {

    /**
     *
     */
    private static final long serialVersionUID = 1804295731410816691L;

    private String name;
    private String type;
    private List<HotKey> data;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<HotKey> getData() {
        return data;
    }

    public void setData(List<HotKey> data) {
        this.data = data;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

}
