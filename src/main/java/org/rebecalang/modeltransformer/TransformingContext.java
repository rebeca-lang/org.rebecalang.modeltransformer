package org.rebecalang.modeltransformer;

import java.util.Hashtable;

public class TransformingContext {
	private static TransformingContext instance = new TransformingContext();
	
	private Hashtable<String, Object> context;
	
	public static TransformingContext getInstance() {
		return instance;
	}
	
	private TransformingContext() {
		context = new Hashtable<String, Object>();
	}
	
	public void registerInContext(String name, Object value) {
		context.put(name, value);
	}
	
	public void unregisterFromContext(String name) {
		context.remove(name);
	}
	
	public Object lookupInContext(String name) {
		return context.get(name);
	}
}
