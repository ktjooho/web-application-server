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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

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
		LIST,
    }
    
    public RequestHandler(Socket connectionSocket) 
    {
        this.connection = connectionSocket;
		request_header_property_map = new HashMap<String,String>();
		response_header_property_map = new HashMap<String,String>();
		response_body_buffer = "".getBytes();
		query_map = new HashMap<String,String>();
    }
    private void process()
    {
    	//Process CRUD Operations.
    	if(operation_type == OperationType.CREATE)
    	{
    		if(target_resource.equals("/user"))
    		{
    			user = new User(query_map.get("userId"), query_map.get("password"), query_map.get("name"), 	query_map.get("email"));
    			DataBase.addUser(user);
    			status_code = StatusCode.STATUS_302;
				response_header_property_map.put("Location", "http://localhost:8080/index.html");
    		}else{
    			log.debug("Fail to make user");
			}
    		//STATUS_302
    		//
    	}
    	else if(operation_type==OperationType.READ)
    	{
    		response_body_buffer = loadResourceFile(target_resource);
    		status_code = StatusCode.STATUS_200;
			String request_resource_type = request_header_property_map.get("Accept");
			if(request_resource_type.contains("css"))
				response_header_property_map.put("Content-Type", "text/css;charset=utf-8");

			else// (request_resource_type.contains("html"))
				response_header_property_map.put("Content-Type", "text/html;charset=utf-8");

			response_header_property_map.put("Content-Length", String.valueOf(response_body_buffer.length));

    	}
    	else if(operation_type == OperationType.UPDATE)
    	{
    		
    	}else if(operation_type == OperationType.DELETE)
    	{
    		
    	}else if(operation_type==OperationType.LOGIN)
    	{
    		String user_id = query_map.get("userId");
    		User user = DataBase.findUserById(user_id);
			status_code = StatusCode.STATUS_302;
    		String login_cookie = "logined=true";
			response_header_property_map.put("Location", "http://localhost:8080/index.html");

    		if(user==null ||!user.getPassword().equals(query_map.get("password")) )
    		{
    			login_cookie ="logined=false";
				response_header_property_map.put("Location", "http://localhost:8080/user/login_failed.html");
    		}
    		response_header_property_map.put("Set-Cookie",login_cookie);
    		
    	}
    	else if(operation_type == OperationType.LIST)
		{
			//login_check
			String login_check = request_header_property_map.get("Cookie");

			if(login_check==null || !login_check.equals("logined=true")){
				status_code = StatusCode.STATUS_302;
				response_header_property_map.put("Location", "http://localhost:8080/user/login.html");
			}
			else{
				status_code = StatusCode.STATUS_200;
				response_header_property_map.put("Content-Type", "text/html;charset=utf-8");
				StringBuilder stringBuilder = new StringBuilder("");
				stringBuilder.append("<!DOCTYPE html>\n" +
						"<html lang=\"kr\">\n" +
						"<head>\n" +
						"    <meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\">\n" +
						"    <meta charset=\"utf-8\">\n" +
						"    <title>SLiPP Java Web Programming</title>\n" +
						"    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1, maximum-scale=1\">\n" +
						"    <link href=\"../css/bootstrap.min.css\" rel=\"stylesheet\">\n" +
						"    <!--[if lt IE 9]>\n" +
						"    <script src=\"//html5shim.googlecode.com/svn/trunk/html5.js\"></script>\n" +
						"    <![endif]-->\n" +
						"    <link href=\"../css/styles.css\" rel=\"stylesheet\">\n" +
						"</head>\n" +
						"<body>"+
						"<div class=\"container\" id=\"main\">\n" +
								"   <div class=\"col-md-10 col-md-offset-1\">\n" +
								"      <div class=\"panel panel-default\">\n" +
								"          <table class=\"table table-hover\">\n" +
								"              <thead>\n" +
								"                <tr>\n" +
								"                    <th>#</th> <th>사용자 아이디</th> <th>이름</th> <th>이메일</th><th></th>\n" +
								"                </tr>\n" +
								"              </thead>\n" +
								"              <tbody>"

						);

				//effectively final int  idx = 1;
				final AtomicInteger idx = new AtomicInteger(1);
				DataBase.findAll().forEach(
						(user)->{

							stringBuilder.append("<tr>\n"+
									"<th scope=\"row\">"+idx+"</th> " +
									"<td>"+user.getUserId()+"</td> " +
									"<td>"+user.getName()+"</td> " +
									"<td>"+user.getEmail()+"</td>" +
									"<td><a href=\"#\" class=\"btn btn-success\" role=\"button\">수정</a></td>");
							idx.getAndAdd(1);
						}
				);
				stringBuilder.append("</tbody>\n" +
						"          </table>\n" +
						"        </div>\n" +
						"    </div>\n" +
						"</div>\n" +
						"\n" +
						"<!-- script references -->\n" +
						"<script src=\"../js/jquery-2.2.0.min.js\"></script>\n" +
						"<script src=\"../js/bootstrap.min.js\"></script>\n" +
						"<script src=\"../js/scripts.js\"></script>\n" +
						"\t</body>\n" +
						"</html>");
				String data = new String(stringBuilder);
				response_body_buffer = data.getBytes();
				response_header_property_map.put("Content-Length", String.valueOf(response_body_buffer.length));
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
	private boolean isItStaticResource(String line)
	{
		String []post = {
				".html",".ico",".js",".css",".woff",".ttf"
		};
		for(int i=0; i<post.length; ++i)
		{
			if(line.contains(post[i]))
				return true;
		}
		return false;
		/*
		if(line.contains(".html"))
			return true;
		return false;
		*/
	}
    private void parseRequestHttpOperation(String token)
    {
    	if(isItStaticResource(token))
		{
			target_resource = token;
			operation_type = OperationType.READ;
			return ;
		}

    	String uri = token;
    	String request_resource;
    	String params;
    	
    	int index = uri.indexOf("?");
    	int param_start = index + 1;
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
		if(operation_name.equals("list"))
			operation_type = OperationType.LIST;

		if(has_params)
		{
			params = uri.substring(param_start);
			query_map = util.HttpRequestUtils.parseQueryString(params);
		}
    }
    private void parseRequestMethod(String line) throws UnexpectedException
    {
    	log.debug("Request Method:"+line);
    	if( line==null || line.isEmpty() ){
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
			log.debug("Request Header:"+line);
    		Pair pair = HttpRequestUtils.parseHeader(line);
    		request_header_property_map.put(pair.getKey(), pair.getValue());
    	}
    }
    private void parseRequestBody(BufferedReader br) throws UnexpectedException, IOException
    {
    	if(request_type != RequestType.POST)
    		return ;
//    		throw new UnexpectedException("RequestType is supposed to be POST");
    	
    	final String key = "Content-Length";
    	int content_length = 0;
    	if(request_header_property_map.containsKey(key))
    	{
    		content_length = Integer.parseInt(request_header_property_map.get(key));
    	}
    	else 
    		return ;

    	log.debug("Content_legnth : "+content_length);

		/*
		String line = null;
		while((line=br.readLine())!=null && !"".equals(line))
		{
			log.debug("Request_body:"+line);
			Pair pair = HttpRequestUtils.parseHeader(line);
			request_header_property_map.put(pair.getKey(), pair.getValue());
		}
		*/

    	char []cbuf = new char[ content_length+1];
    	cbuf[content_length] = '\0';
		//String s = new String();
		//br.read(s);

		 br.read(cbuf);
		/*
    	if(br.read(cbuf)!= -1 )
    		throw new UnexpectedException("Content-Length Doesn't match");
    	*/
		String str = new String(cbuf);
		log.debug("Content : "+ str);
    	query_map = HttpRequestUtils.parseQueryString(new String(cbuf));
		log.debug("Query_map:"+query_map.toString());
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
		log.debug("Load_Url:"+url);
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
        	response_body_buffer = "".getBytes();
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
    	//response200Header(dos, response_body_buffer.length);
		responseHeader(dos);
    	responseBody(dos, response_body_buffer);
    }
    private void responseHeader(DataOutputStream dos)
	{
		String status="";
		String line_feed = "\r\n";
		String delimeter = ": ";
		String header = "";
		if(status_code==StatusCode.STATUS_200){
			status = "200 OK";
		}else if(status_code==StatusCode.STATUS_302){
			status = "302 Found";
		}

		header = header +"HTTP/1.1 " + status + line_feed;

		//Iterator<HashMap<String,String>> it = response_header_property_map.;
		for(Map.Entry<String,String> entry : response_header_property_map.entrySet()){
			header = header + entry.getKey()+delimeter+entry.getValue()+line_feed;
		}
		header = header + line_feed;
		log.debug("Response Header :"+header);
		try {
			dos.writeBytes(header);
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}
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
    		if(body.length > 0)
            	dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
