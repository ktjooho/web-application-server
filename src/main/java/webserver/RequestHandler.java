package webserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import model.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import controller.Controller;
import controller.CreateUserController;
import controller.ListUserController;
import controller.LoginController;
import util.HttpRequestUtils;
import util.IOUtils;
import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
    private Socket connection;

    private static final Map<String, Controller> requestMapper = new HashMap<String,Controller>();
    {
    	requestMapper.put("/user/create", new CreateUserController());
    	requestMapper.put("/user/list", new ListUserController());
    	requestMapper.put("/user/login", new LoginController());
    }


    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }
    private String getDefaultPath(String path) {
    	if(path.equals("/")) {
    		return "/index.html";
    	}
    	return path;
    }
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
           
        	HttpRequest request = new HttpRequest(in);
            HttpResponse response = new HttpResponse(out);
            
            Controller controller = requestMapper.get(request.getPath());
            
            if(controller==null)
            	response.forward(getDefaultPath(request.getPath()));
            else 
            	controller.service(request, response);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
