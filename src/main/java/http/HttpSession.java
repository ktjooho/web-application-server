package http;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HttpSession {
	
	//private UUID uuid;
	private String id;
	private Map<String, Object> attributes;
	
	public HttpSession(String id) {
		// TODO Auto-generated constructor stub
		attributes = new HashMap<String, Object>();
		this.id = id;
	}
	
	String getId() {
		return id;
	}
	
	public void setAttribute(String name, Object value) {
		attributes.put(name, value);
	}
	
	public Object getAttribute(String name) {
		return attributes.get(name);
	}
	
	public void removeAttribute(String name) {
		attributes.remove(name);
	}
	
	public void invalidate() {
		attributes.clear();
	}
}
