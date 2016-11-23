/**
 *
 */
package com.modernsky.istv.bean;

/**
 * @author： fengqingyun2008
 * @Email： fengqingyun2008@gmail.com
 * @version：1.0
 * @创建时间：2015-6-19 下午4:07:33
 * @类说明：
 */
public class HotKey extends BaseBean {

    /**
     *
     */
    private static final long serialVersionUID = 1804295731410816691L;

    private int dataDictId;
    private String name;
    private int status;
    private String type;
    private String description;

    public int getDataDictId() {
        return dataDictId;
    }

    public void setDataDictId(int dataDictId) {
        this.dataDictId = dataDictId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
