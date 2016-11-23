package com.modernsky.istv.bean;

/**
 * Created by zqg on 2016/3/4.
 */
//{
//        "total": 1,
//        "message": "成功",
//        "data": {
//        "video_unique": "c286b021a6",
//        "token": "swTUNdPh5Zm9Ox9gHbLLC2wTZvhq9dOvBIJCX5yUwiK73iQ6rG8uuGzizv0IXrmv0gi3GdkRTEnydRTrwgFMtkPlzbeqfmSRi0bgaRXMkPtOoZZjKkqtjjJumLUvTeQixvm_LE-5ZJ7BEwM7oIqC3Q94BZPCJnvgJyRUzyG53KdaEMBgJLwtGFgYAWg-kDlMlcprrBzzqnGj0734N-2Ho3RW4vG_-44m3TlICMru-zbwfC75CfXwXvW5idvd1UOPBcoqb2H58jAW40IPitBVJQEheFjWtfqmflCGngsPPfMosA46Bfvkke1-CLmRikAN4ygWV4kM4f02UxK8RVp-Ksq-mmpNi3HzhATlpVMEGfntpsAattb-jEVYwySFjpdlhFVXJOBGqqShGND--iMmSClxblI~",
//        "progress_url": "http://115.238.137.221/api/uploadprogress?token=swTUNdPh5Zm9Ox9gHbLLC2wTZvhq9dOvBIJCX5yUwiK73iQ6rG8uuGzizv0IXrmv0gi3GdkRTEnydRTrwgFMtkPlzbeqfmSRi0bgaRXMkPtOoZZjKkqtjjJumLUvTeQixvm_LE-5ZJ7BEwM7oIqC3Q94BZPCJnvgJyRUzyG53KdaEMBgJLwtGFgYAWg-kDlMlcprrBzzqnGj0734N-2Ho3RW4vG_-44m3TlICMru-zbwfC75CfXwXvW5idvd1UOPBcoqb2H58jAW40IPitBVJQEheFjWtfqmflCGngsPPfMosA46Bfvkke1-CLmRikAN4ygWV4kM4f02UxK8RVp-Ksq-mmpNi3HzhATlpVMEGfntpsAattb-jEVYwySFjpdlhFVXJOBGqqShGND--iMmSClxblI~&fmt=cjson",
//        "uploadtype": 0,
//        "isdrm": 0,
//        "upload_url": "http://115.238.137.221/api/fileupload?offset=0&token=swTUNdPh5Zm9Ox9gHbLLC2wTZvhq9dOvBIJCX5yUwiK73iQ6rG8uuGzizv0IXrmv0gi3GdkRTEnydRTrwgFMtkPlzbeqfmSRi0bgaRXMkPtOoZZjKkqtjjJumLUvTeQixvm_LE-5ZJ7BEwM7oIqC3Q94BZPCJnvgJyRUzyG53KdaEMBgJLwtGFgYAWg-kDlMlcprrBzzqnGj0734N-2Ho3RW4vG_-44m3TlICMru-zbwfC75CfXwXvW5idvd1UOPBcoqb2H58jAW40IPitBVJQEheFjWtfqmflCGngsPPfMosA46Bfvkke1-CLmRikAN4ygWV4kM4f02UxK8RVp-Ksq-mmpNi3HzhATlpVMEGfntpsAattb-jEVYwySFjpdlhFVXJOBGqqShGND--iMmSClxblI~&fmt=cjson",
//        "video_id": "24027885"
//        },
//        "code": 0
//        }
public class VideoUpdateInfo extends BaseBean {

    private int total;
    private String message;
    private int code;
    private Data data;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data extends BaseBean {
        private String video_unique;
        private String token;
        private String progress_url;
        private String upload_url;
        private String video_id;
        private int uploadtype;
        private int isdrm;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getVideo_unique() {
            return video_unique;
        }

        public void setVideo_unique(String video_unique) {
            this.video_unique = video_unique;
        }

        public String getProgress_url() {
            return progress_url;
        }

        public void setProgress_url(String progress_url) {
            this.progress_url = progress_url;
        }

        public String getUpload_url() {
            return upload_url;
        }

        public void setUpload_url(String upload_url) {
            this.upload_url = upload_url;
        }

        public String getVideo_id() {
            return video_id;
        }

        public void setVideo_id(String video_id) {
            this.video_id = video_id;
        }

        public int getUploadtype() {
            return uploadtype;
        }

        public void setUploadtype(int uploadtype) {
            this.uploadtype = uploadtype;
        }

        public int getIsdrm() {
            return isdrm;
        }

        public void setIsdrm(int isdrm) {
            this.isdrm = isdrm;
        }
    }
}
