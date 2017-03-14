package http;

import java.util.HashMap;
import java.util.Map;

import util.HttpRequestUtils;

public class HttpCookie {

	private Map<String,String> cookies;
	
	public HttpCookie(String cookieValue) {
		// TODO Auto-generated constructor stub
		cookies = HttpRequestUtils.parseCookies(cookieValue);
	}
	
	public String getCookie(String name) {
		return cookies.get(name);
	}
}
