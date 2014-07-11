package com.zxsoft.crawler.parse;


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
