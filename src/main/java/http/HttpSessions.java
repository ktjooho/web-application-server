package http;

import java.util.Map;

public class HttpSessions {

	public static final String SESSION_ID_NAME = "JSESSIONID";
	
	private static Map<String,HttpSession> sessions;
	
	public static HttpSession getSession(String id) {
		
		HttpSession session = sessions.get(id);
		
		if(session == null) {
			session = new HttpSession(id);
			sessions.put(id, session);
		}
		return session;
	}
	
	static void removeSession(String id) {
		
		sessions.remove(id);
	
	}
	
	
	
}
