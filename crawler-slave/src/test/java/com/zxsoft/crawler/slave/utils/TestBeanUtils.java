package com.zxsoft.crawler.slave.utils;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.BeanUtils;

public class TestBeanUtils {

        class Foo {
                private String name;
                private int age;
                private long  time;
                public String getName() {
                        return name;
                }
                public void setName(String name) {
                        this.name = name;
                }
                public int getAge() {
                        return age;
                }
                public void setAge(int age) {
                        this.age = age;
                }
                public long getTime() {
                        return time;
                }

                public void setTime(long time) {
                        this.time = time;
                }
                @Override
                public String toString() {
                        return "name:" + name + ", age:" + age + ", time:" + time;
                }
        }
        
        
        @Test
        public void test() {
                Map map = new HashMap();
                map.put("name", "huloo");
                map.put("age", 23);
                map.put("time", System.currentTimeMillis());
                Foo foo = new Foo();
                BeanUtils.copyProperties(map, foo);
                System.out.println(foo.toString());
        }
}
