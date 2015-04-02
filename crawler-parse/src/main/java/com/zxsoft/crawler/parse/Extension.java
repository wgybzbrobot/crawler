package com.zxsoft.crawler.parse;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.zxsoft.crawler.plugin.PluginRuntimeException;


public class Extension {

	private String type;
	private String className;

	public Object getInstance() throws PluginRuntimeException {

		ClassLoader classLoader = ClassLoader.getSystemClassLoader();
		Class<?> clazz;
		try {
			clazz = classLoader.loadClass(className);
			Object object = clazz.newInstance();
			return object;
		} catch (ClassNotFoundException e) {
			throw new PluginRuntimeException(e);
		} catch (InstantiationException e) {
			throw new PluginRuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new PluginRuntimeException(e);
		}
	}

	public Object getInstance(Object[] params, Class[] paramClassArr) throws PluginRuntimeException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException {
	    
	    ClassLoader classLoader = ClassLoader.getSystemClassLoader();
	    Class<?> clazz;
	    try {
	        clazz = classLoader.loadClass(className);
//	        Object object = clazz.newInstance();
	           
            Constructor<?> cons = clazz.getConstructor(paramClassArr);
            Object object = cons.newInstance(params);
	        return object;
	        
	    } catch (ClassNotFoundException e) {
	        throw new PluginRuntimeException(e);
	    } catch (InstantiationException e) {
	        throw new PluginRuntimeException(e);
	    } catch (IllegalAccessException e) {
	        throw new PluginRuntimeException(e);
	    }
	}

	
	public Extension(String type, String className) {
        super();
        this.type = type;
        this.className = className;
    }

    public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

}
