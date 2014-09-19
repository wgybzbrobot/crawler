package org.thinkingcloud.framework.web.utils;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.type.Type;
import org.springframework.orm.hibernate3.HibernateCallback;

public class HibernateCallbackUtil {
	
	public static HibernateCallback getNativeCallback(final StringBuffer sb,final Map params,final boolean isUpdate,final Map scalars,final ResultTransformer rt){
		return getHibernateCallback(sb,params,true,isUpdate,scalars,rt);
	}
	public static HibernateCallback getNativeCallbackWithPage(StringBuffer sb, Map params, Map scalars, ResultTransformer rt, int pageNo, int pageSize){
		return getHibernateCallbackWithPage(sb,params,true,scalars,rt,pageNo,pageSize);
	}
	
	public static HibernateCallback getCallbackWithPage(StringBuffer sb, Map params, ResultTransformer rt, int pageNo, int pageSize){
		return getHibernateCallbackWithPage(sb,params,false,null,rt,pageNo,pageSize);
	}
	public static HibernateCallback getCallback(StringBuffer sb, Map params, boolean isUpdate, ResultTransformer rt){
		return getHibernateCallback(sb,params,false,isUpdate,null,rt);
	}
	
	
	private static HibernateCallback getHibernateCallbackWithPage(final StringBuffer sb,final Map params,final boolean isNative,final Map scalars,final ResultTransformer rt,final int pageNo,final int pageSize){
		if (sb == null || sb.toString().trim().equals("")) return null;
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = null;
				if (isNative){
					query = session.createSQLQuery(sb.toString());
					SQLQuery sqlQuery = (SQLQuery)query;
					if (scalars != null ){
						Iterator it = scalars.entrySet().iterator();
						while (it.hasNext()){
							Map.Entry e = (Map.Entry)it.next();
							sqlQuery.addScalar((String)e.getKey(), (Type)e.getValue());
						}
					}
				}else{
					query = session.createQuery(sb.toString());
				}
				if (params != null && !params.isEmpty()){
					Iterator it = params.entrySet().iterator();
					while(it.hasNext()){
						Map.Entry e = (Map.Entry)it.next();
						String key = (String)e.getKey();
						Object value = e.getValue();
						if (value instanceof Collection){
							query.setParameterList(key, (Collection)value);
						}
						else{
							query.setParameter(key,value);
						}
					}
				}
				List res = null;
				
				if (rt != null){
					query.setResultTransformer(rt);
				}
				if (pageSize > 0){
					query.setFirstResult(pageSize*(pageNo-1));
					query.setMaxResults(pageSize);
				}
				res =  query.list();
				//
				String countString = "";
				if (isNative){
					countString = "select count(*) from ("+sb+") a";
				}
				else {
					int start = sb.indexOf("from");
					int end = sb.indexOf("order by");
					
					
					if (end != -1){
					 countString =" select  count(*) "+ sb.substring(start, end);
					}
					else {
						 countString =" select  count(*) "+ sb.substring(start);
					}
					countString = countString.replaceAll("fetch", "").replace("fetch all properties", "");
					if (sb.indexOf("distinct") != -1){
						countString = countString.replaceFirst("\\*", " distinct id ");
					}
				}
				if (isNative){
					query = session.createSQLQuery(countString);
				}else{
					query = session.createQuery(countString);
				}
				if (params != null && !params.isEmpty()){
					Iterator it = params.entrySet().iterator();
					while(it.hasNext()){
						Map.Entry e = (Map.Entry)it.next();
						String key = (String)e.getKey();
						Object value = e.getValue();
						if (value instanceof Collection){
							query.setParameterList(key, (Collection)value);
						}
						else{
							query.setParameter(key,value);
						}
					}
				}	
				Number count  = (Number)query.uniqueResult();
				Page page = new Page(count.intValue(),res);
				return page;
			}
			
		};
		return callback;
	}
	
	
	private static HibernateCallback getHibernateCallback(final StringBuffer sb,final Map params,final boolean isNative,final boolean isUpdate,final Map scalars,final ResultTransformer rt){
		if (sb == null || sb.toString().trim().equals("")) return null;
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = null;
				if (isNative){
					query = session.createSQLQuery(sb.toString());
					SQLQuery sqlQuery = (SQLQuery)query;
					if (scalars != null ){
						Iterator it = scalars.entrySet().iterator();
						while (it.hasNext()){
							Map.Entry e = (Map.Entry)it.next();
							sqlQuery.addScalar((String)e.getKey(), (Type)e.getValue());
						}
					}
				}else{
					query = session.createQuery(sb.toString());
				}
				if (params != null && !params.isEmpty()){
					Iterator it = params.entrySet().iterator();
					while(it.hasNext()){
						Map.Entry e = (Map.Entry)it.next();
						String key = (String)e.getKey();
						Object value = e.getValue();
						if (value instanceof Collection){
							query.setParameterList(key, (Collection)value);
						}
						else{
							query.setParameter(key,value);
						}
					}
				}
				Object res = null;
				if (isUpdate){
					res = new Long(query.executeUpdate());
				}
				else{
					if (rt != null){
						query.setResultTransformer(rt);
					}
					res = query.list();
				}
				return res;
			}
			
		};
		return callback;
	}
	

}
