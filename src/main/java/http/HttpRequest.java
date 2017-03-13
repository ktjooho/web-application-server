package http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import util.HttpRequestUtils;
import util.HttpRequestUtils.Pair;
import util.IOUtils;

public class HttpRequest {
	private static final Logger log = (Logger) LoggerFactory.getLogger(HttpRequest.class);

	private String				path;
	private Map<String,String>	header;
	private Map<String,String>	parameter;
	private String				method;

	public String getPath() {
		return path;
	}
	public String getHeader(String field){
		return header.get(field);
	}
	public Map<String, String> getHeader() {
		return header;
	}

	public Map<String, String> getParameter() {
		return parameter;
	}

	public String getMethod() {
		return method;
	}

	private String getUrl(String token) {
	
		String url = token;
		if(url.equals("/")){
			url = "/index.html";
		}
		return url;
	}
	private void parseUrl(String url) {
		path = url;
		//Only GET
		if(method.equals("GET"))
		{
			int index = url.indexOf('?');
			if(index < 0)
				return ;
			path = url.substring(0, index);
			String query = url.substring(index+1);
			parameter = HttpRequestUtils.parseQueryString(query);
		}
	}
	private void parseHeader(BufferedReader br) throws IOException {
		String line;
		while( !(line=br.readLine()).equals("")){
			Pair pair = HttpRequestUtils.parseHeader(line);
			header.put(pair.getKey(), pair.getValue());
		}
	}
	private void parseBody(BufferedReader br)throws IOException {
	
		int contentLength=0;
		
		if(header.containsKey("Content-Length"))
			contentLength = Integer.parseInt(header.get("Content-Length"));
		
		String body = IOUtils.readData(br, contentLength);
		
		if(!body.isEmpty())
			parameter = HttpRequestUtils.parseQueryString(body);
		
	}
	private void parseRequestLine(BufferedReader br) throws IOException {
	
		String line = br.readLine();
		if(line == null)  {
			log.error("Get Error to read Http Request Packet");
			return ;
		}
		String[] tokens = line.split(" ");
		method = tokens[0];
		parseUrl(getUrl(tokens[1]));
	}
	private void parse(BufferedReader br) throws IOException {
	
		parseRequestLine(br);
		parseHeader(br);
		parseBody(br);
	}
	
	public HttpRequest(InputStream in) throws IOException {
		// TODO Auto-generated constructor stub
		header = new HashMap<String,String>();
		parameter = new HashMap<String,String>();
		BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
		parse(br);
	}
	 
}
