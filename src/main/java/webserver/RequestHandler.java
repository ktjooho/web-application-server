package webserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.nio.file.Files;
import java.rmi.UnexpectedException;
import java.util.Map;

import javax.xml.crypto.Data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.DataBase;
import model.User;
import util.HttpRequestUtils;
import util.HttpRequestUtils.Pair;

public class RequestHandler extends Thread {
	
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
    
    private Socket connection;
    
    private byte[] request_header_buffer;
    private byte[] request_body_buffer;
    
    private byte[] response_header_buffer;
    private byte[] response_body_buffer;

    
    private User user;
    
    private RequestType request_type;
    private OperationType operation_type;
    private String target_resource;
    private Map<String,String> query_map;
    private Map<String,String> request_header_property_map;
    private String request_body_content;
    
    
    private Map<String,String> response_header_property_map;

    private StatusCode status_code;

    private enum StatusCode{
    	STATUS_200, // 
    	STATUS_302, //
    	STATUS_404,
    }
    private enum RequestType{
    	GET, 
    	POST,
    	PUT,
    	DELETE,
    }
    private enum OperationType{
    	CREATE,
    	READ,
    	UPDATE,
    	DELETE,
    	LOGIN,
    }
    
    public RequestHandler(Socket connectionSocket) 
    {
        this.connection = connectionSocket;
    }
    private void process()
    {
    	//Process CRUD Operations.
    	if(operation_type == OperationType.CREATE)
    	{
    		if(target_resource.equals("user"))
    		{
    			user = new User(query_map.get("userId"), query_map.get("password"), query_map.get("name"), 	query_map.get("email"));
    			DataBase.addUser(user);
    			status_code = StatusCode.STATUS_302;
    			request_header_property_map.put("Location", "http://localhost:8080/index.html");
    		}
    		//STATUS_302
    		//
    	}
    	else if(operation_type==OperationType.READ)
    	{
    		response_body_buffer = loadResourceFile(target_resource);
    		
    		status_code = StatusCode.STATUS_200;
    		request_header_property_map.put("Content-Type", "text/html;charset=utf-8");
    		request_header_property_map.put("Content-Length", String.valueOf(response_body_buffer.length));
    	}
    	else if(operation_type == OperationType.UPDATE)
    	{
    		
    	}else if(operation_type == OperationType.DELETE)
    	{
    		
    	}else if(operation_type==OperationType.LOGIN)
    	{
    		String user_id = query_map.get("userId");
    		User user = DataBase.findUserById(user_id);
    		String login_cookie = "logined=true";
    		
    		if(user==null ||!user.getPassword().equals(query_map.get("password")) )
    		{
    			login_cookie ="logined=false"; 
    		}
    		
    	}
    }
    private void parseRequestHttpVerb(String token)
    {
    	if(token.equals("GET")){
    		request_type = RequestType.GET;
    	}else if(token.equals("POST")){
    		request_type = RequestType.POST;
    	}else if(token.equals("PUT")){
    		request_type = RequestType.PUT;
    	}else if(token.equals("DELETE")){
    		request_type = RequestType.DELETE;
    	}
    }
   
    private void parseRequestHttpOperation(String token)
    {
    	String uri = token;
    	String request_resource;
    	String params;
    	
    	int index = uri.indexOf("?");
    	boolean has_params = false; 

    	if(index < 0){
    		request_resource = uri;
    	}
    	else{
    		request_resource = uri.substring(0,index);
    		has_params = true;
    	}
    	
		index = request_resource.lastIndexOf("/");
		String operation_name = request_resource.substring(index+1);
		
		target_resource = request_resource.substring(0, index);
		operation_type = OperationType.READ;
		
		if(operation_name.equals("create"))
			operation_type = OperationType.CREATE;
		if(operation_name.equals("read"))
			operation_type = OperationType.READ;
		if(operation_name.equals("update"))
			operation_type = OperationType.UPDATE;
		if(operation_name.equals("delete"))
			operation_type = OperationType.DELETE;
		if(operation_name.equals("login"))
			operation_type = OperationType.LOGIN;

		if(has_params)
		{
			params = uri.substring(index+1);
			query_map = util.HttpRequestUtils.parseQueryString(params);
		}
    }
    private void parseRequestMethod(String line) throws UnexpectedException
    {
    	if(line.isEmpty() || line==null){
    		throw new UnexpectedException("Not Http Request");
    	}
    	String []tokens = line.split(" ");
    	parseRequestHttpVerb(tokens[0]);
    	parseRequestHttpOperation(tokens[1]);
    }
    private void parseRequestHeader(BufferedReader br) throws UnexpectedException, IOException
    {
    	//Get Request-type
    	parseRequestMethod(br.readLine());
    	String line = null;
    	while((line=br.readLine())!=null && !"".equals(line))
    	{
    		Pair pair = HttpRequestUtils.parseHeader(line);
    		request_header_property_map.put(pair.getKey(), pair.getValue());
    	}
    }
    private void parseRequestBody(BufferedReader br) throws UnexpectedException, IOException
    {
    	if(request_type != RequestType.POST)
    		throw new UnexpectedException("RequestType is supposed to be POST");
    	
    	final String key = "Content-Length";
    	int content_length = 0;
    	if(request_header_property_map.containsKey(key))
    	{
    		content_length = Integer.parseInt(request_header_property_map.get(key));
    	}
    	else 
    		return ;
    	
    	char []cbuf = new char[ content_length+1];
    	
    	if(br.read(cbuf)!= -1 )
    		throw new UnexpectedException("Content-Length Doesn't match");
    	query_map = HttpRequestUtils.parseQueryString(new String(cbuf));
    }
    private void parseRequest(InputStream in)
    {
    	BufferedReader br=null;
		try {
			br = new BufferedReader(new InputStreamReader(in,"UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage());
		}
		
		try {
			parseRequestHeader(br);
		} catch (UnexpectedException  e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			parseRequestBody(br);
		} catch (UnexpectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e){
			e.printStackTrace();
		}

    }
    private byte[] loadResourceFile(String url)
    {
    	byte[] data = "Fail to load resource file".getBytes();
    	try {
			data = Files.readAllBytes(new File("./webapp"+url).toPath());
			log.debug(data.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return data;
    }
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());
        
        //request_type = RequestType.ERROR;
        
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 
        	response_body_buffer = "There is nothing to show for you.".getBytes();
        	parseRequest(in);
        	process();
            response(out);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    private void response(OutputStream out)
    {
    	/*
    	 * 
    	 */
    	//0.Status_Code
    	//1.ResponseProperties(Refer to ResponseProperty Map) 
    	//2.\r\n
    	//3.ResponseBody
    	
    	
    	DataOutputStream dos = new DataOutputStream(out);
    	response200Header(dos, response_body_buffer.length);
    	responseBody(dos, response_body_buffer);
    }
    //private void responseHeader()
    //private void responseHeaderStatusCode
    //private void responseHeaderProperties
    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
