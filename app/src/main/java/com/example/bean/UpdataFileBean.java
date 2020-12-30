package com.example.bean;


public class UpdataFileBean {


    /**
     * data : {"tmp_path":"tmp_uploads/f63b390da060f2d4c02cee96e1171ea0.jpg","url":"https://www.liulongbin.top:8888/tmp_uploads/f63b390da060f2d4c02cee96e1171ea0.jpg"}
     * meta : {"msg":"上传成功","status":200}
     */

    private DataDTO data;
    private MetaDTO meta;

    public UpdataFileBean(DataDTO data, MetaDTO meta) {
        this.data = data;
        this.meta = meta;
    }

    public DataDTO getData() {
        return data;
    }

    public void setData(DataDTO data) {
        this.data = data;
    }

    public MetaDTO getMeta() {
        return meta;
    }

    public void setMeta(MetaDTO meta) {
        this.meta = meta;
    }

    public static class DataDTO {
        /**
         * tmp_path : tmp_uploads/f63b390da060f2d4c02cee96e1171ea0.jpg
         * url : https://www.liulongbin.top:8888/tmp_uploads/f63b390da060f2d4c02cee96e1171ea0.jpg
         */

        private String tmpPath;
        private String url;

        public DataDTO(String tmpPath, String url) {
            this.tmpPath = tmpPath;
            this.url = url;
        }

        public String getTmpPath() {
            return tmpPath;
        }

        public void setTmpPath(String tmpPath) {
            this.tmpPath = tmpPath;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public static class MetaDTO {
        /**
         * msg : 上传成功
         * status : 200
         */

        private String msg;
        private Integer status;

        public MetaDTO(String msg, Integer status) {
            this.msg = msg;
            this.status = status;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }
    }
}
