package controller;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import http.HttpRequest;
import http.HttpResponse;

public abstract class AbstractController implements Controller {
	protected static final Logger log = (Logger) LoggerFactory.getLogger(AbstractController.class);
	@Override
	public void service(HttpRequest request, HttpResponse response) {
		// TODO Auto-generated method stub
		if(request.getMethod().equals("GET")) {
			doGet(request, response);
		}
		else {
		
			doPost(request, response);
		}
	}
	protected void doGet(HttpRequest request, HttpResponse response){
	
		
	}
	protected void doPost(HttpRequest request, HttpResponse response){
	
		
	}
	
	
}
