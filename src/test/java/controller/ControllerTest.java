package controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.Before;
import org.junit.Test;

import http.HttpRequest;
import http.HttpResponse;

public class ControllerTest {
	private String testDir = "./src/test/resource/";
	private CreateUserController controller;
	
	@Before
	public void init() {
		controller = new CreateUserController();
	}
	
	@Test
	public void createUser() throws Exception {
		InputStream in = new FileInputStream(new File(testDir+"Http_Post.txt")); 
		HttpRequest request = new HttpRequest(in);
		HttpResponse response = new HttpResponse(createOutputStream("CreateController.txt"));
		controller.service(request, response);
	}
	
	
	private OutputStream createOutputStream(String filename) 
			throws FileNotFoundException{
		return new FileOutputStream(new File(testDir+ filename));
	}
	
	
}
