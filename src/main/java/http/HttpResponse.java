package http;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import webserver.RequestHandler;

public class HttpResponse {
	
	private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
	
	enum HttpStatusCode{
		CODE_200,
		CODE_302
	}

	Map<String,String>	header;

	DataOutputStream dos;
	
	public String getHeader(String field){
		return header.get(field);
	}
	public void addHeader(String field, String value){
		header.put(field, value);
	}
	public HttpResponse(OutputStream out) {
		// TODO Auto-generated constructor stub
		header = new HashMap<String,String>();
		dos = new DataOutputStream(out);
	}
	public void forwardData(byte[] data) throws IOException {
		addHeader("Content-type", "text/html;charset=utf-8");
		addHeader("Content-Length", String.valueOf(data.length));
		response(HttpStatusCode.CODE_200);
		responseBody(data);
	}
	public void forward(String path) throws IOException{
		if(path.contains(".css"))
			addHeader("Content-type", "text/css;charset=utf-8");
		else
			addHeader("Content-type", "text/html;charset=utf-8");
		
		byte[] data = Files.readAllBytes(new File("./webapp"+path).toPath());
		addHeader("Content-Length", String.valueOf(data.length));
		response(HttpStatusCode.CODE_200);
		responseBody(data);
	}
	public void sendRedirect(String path) throws IOException{
		//Response-302
		addHeader("Location", path);
		response(HttpStatusCode.CODE_302);
	}
	private void response(HttpStatusCode statusCode) throws IOException{
		
		String stateLine="";
		String head = "";
		if(statusCode == HttpStatusCode.CODE_200){
			stateLine = "HTTP/1.1 200 OK \r\n";
			
		}else if(statusCode==HttpStatusCode.CODE_302){
			stateLine = "HTTP/1.1 302 Redirect \r\n";
		}
		for(Map.Entry<String, String>entry : header.entrySet()){
			head = head + entry.getKey()+": "+entry.getValue()+"\r\n";
		}
		head += "\r\n";
		
		dos.writeBytes(stateLine);
		dos.writeBytes(head);
	}
	private void responseBody(byte[] bodyBuffer) throws IOException{
		dos.write(bodyBuffer, 0, bodyBuffer.length);
		dos.writeBytes("\r\n");
		dos.flush();
	}
}
