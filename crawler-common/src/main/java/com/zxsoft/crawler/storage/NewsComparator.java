package com.zxsoft.crawler.storage;

import java.util.Comparator;


public class NewsComparator implements Comparator<News>{

    public int compare(News o1, News o2) {
        if (o1.getReleaseDate().after(o2.getReleaseDate())) {
            return 1;
        } else if (o1.getReleaseDate().before(o2.getReleaseDate())) {
            return -1;
        }
        return 0;
    }
}
