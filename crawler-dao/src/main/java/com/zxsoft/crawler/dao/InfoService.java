//package com.zxsoft.crawler.dao;
//
//import org.springframework.dao.DuplicateKeyException;
//import org.springframework.jdbc.datasource.DataSourceTransactionManager;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import com.zxsoft.crawler.storage.Forum;
//import com.zxsoft.crawler.storage.News;
//import com.zxsoft.crawler.storage.Reply;
//import com.zxsoft.crawler.storage.Seed;
//
//
//@Service
//public class InfoService {
//
//	private InfoDao infoMapper;
//	private DataSourceTransactionManager transactionManager;
//	
//	/**
//	 * 获取种子，若是原始种子，则获取后更新；否则获取后删除
//	 */
//	@Transactional
//	public Seed getSeed() {
//		Seed seed = infoMapper.getSeed();
//		return seed;
//	}
//	
//	public void updateSeed(Seed seed) {
//		infoMapper.updateSeed(seed);
//	}
//	
//	public void deleteSeed (Seed seed) {
//		infoMapper.delSeed(seed);
//	}
//	
//	public void delAllSeed () {
//		infoMapper.delAllSeed();
//	}
//	public void addSeed(Seed seed) {
//		
//		try {
//			infoMapper.addSeed(seed);
//		} catch (DuplicateKeyException e) {
//			return;
//		}
//	}
//
///*	public void addSeed(List<Seed> seeds) {
//		infoMapper.addSeed(seeds);
//	}*/
//	
//	
//	/**
//	 * 新增论坛主帖
//	 */
//	public void addForum(Forum forum) {
//		Forum fo = infoMapper.getForum(forum.getUrl());
//		if (fo != null) {
//			infoMapper.updateForum(forum);
//			return;
//		}
//		try {
//			infoMapper.addForum(forum);
//		} catch (DuplicateKeyException e) {
//			return;
//		}
//	}
//
//	public void addReply(Reply reply) {
//		infoMapper.addReply(reply);
//	}
//	
//	/**
//	 * 添加资讯
//	 */
//	public boolean addNews(News news) {
//	    try {
//	        infoMapper.addNews(news);
//	    } catch (DuplicateKeyException ex) {
//	        return false;
//	    }
//	    return true;
//	}
//	
//	public InfoDao getInfoMapper() {
//		return infoMapper;
//	}
//
//	public void setInfoMapper(InfoDao infoMapper) {
//		this.infoMapper = infoMapper;
//	}
//
//
//	public DataSourceTransactionManager getTransactionManager() {
//		return transactionManager;
//	}
//
//
//	public void setTransactionManager(DataSourceTransactionManager transactionManager) {
//		this.transactionManager = transactionManager;
//	}
//
//}
