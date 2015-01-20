package com.zxsoft.crawler.api;

public enum JobType {
        NETWORK_SEARCH(1), NETWORK_INSPECT(2), NETWORK_FOCUS(3);

        JobType(int value) {
                this.value = value;
        }

        private final int value;

        public int getValue() {
                return value;
        }
}
