package com.zxsoft.crawler.store.impl;

import java.io.Serializable;
import java.util.List;

import com.zxsoft.crawler.storage.RecordInfo;

public class PostData implements Serializable {

        private static final long serialVersionUID = 3183580989697121542L;

        private int num;
        private List<RecordInfo> records;

        public int getNum() {
                return num;
        }

        public void setNum(int num) {
                this.num = num;
        }

        public List<RecordInfo> getRecords() {
                return records;
        }

        public void setRecords(List<RecordInfo> records) {
                this.records = records;
        }
}
