package com.zxsoft.crawler.storage;

import java.util.Comparator;


public class ForumComparator implements Comparator<Forum>{

    public int compare(Forum o1, Forum o2) {
        if (o1.getReleasedate().after(o2.getReleasedate())) {
            return 1;
        } else if (o1.getReleasedate().before(o2.getReleasedate())) {
            return -1;
        }
        return 0;
    }
}
