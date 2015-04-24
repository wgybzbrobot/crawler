package com.zxsoft.crawler.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "reptile", catalog = "crawler")
public class Reptile implements Serializable {

    /**
      * 
      */
    private static final long serialVersionUID = 5555988404863174213L;

    private Integer id;
    private String name;
    private String redis;
    private String master;
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    
    @Column(name = "name", unique = true, nullable = false, length = 45)
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
    @Column(name = "redis",  nullable = false, length = 45)
    public String getRedis() {
        return redis;
    }
    public void setRedis(String redis) {
        this.redis = redis;
    }
    
    @Column(name = "master",  nullable = false, length = 45)
    public String getMaster() {
        return master;
    }
    public void setMaster(String master) {
        this.master = master;
    }
    
    
    
}
