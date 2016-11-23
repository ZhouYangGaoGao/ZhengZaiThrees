/**
 *
 */
package com.modernsky.istv.bean;

/**
 * @author： fengqingyun2008
 * @Email： fengqingyun2008@gmail.com
 * @version：1.0
 * @创建时间：2015-6-9 下午4:39:37
 * @类说明：
 */
public class OrderBean extends BaseBean {

    /**
     * id String	订单id
     * body	String	订单内容
     * videoPic	String	封面
     * showTime	long	开始时间
     * totalFee	String	钱数
     * type	String	类型 MB VIDEO
     * count	int	mb的数量
     * successTime	long	订单成功的时间
     */
    private static final long serialVersionUID = 8607135119327005690L;

    private String id;
    private int count;
    private String body;
    private String totalFee;
    private String videoPic;
    private long showTime;
    private long successTime;
    private String type;//类型 MB VIDEO


    public long getShowTime() {
        return showTime;
    }

    public void setShowTime(long showTime) {
        this.showTime = showTime;
    }

    public long getSuccessTime() {
        return successTime;
    }

    public void setSuccessTime(long successTime) {
        this.successTime = successTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(String totalFee) {
        this.totalFee = totalFee;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getVideoPic() {
        return videoPic;
    }

    public void setVideoPic(String videoPic) {
        this.videoPic = videoPic;
    }
}
