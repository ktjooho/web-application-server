package http;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;

import http.HttpRequest;

import static junit.framework.Assert.*;

import junit.framework.Assert;

public class HttpRequestTest {
	private String testDir = "./src/test/resource/";
	
	@Before
	public void init()
	{
		
		
	}
	@Test
	public void request_GET() throws Exception{
		InputStream in = new FileInputStream(new File(testDir+"Http_Get.txt")); 
		HttpRequest request = new HttpRequest(in);
		assertEquals("GET", request.getMethod());
		assertEquals("/user/list", request.getPath());
		assertEquals("keep-alive", request.getHeader().get("Connection"));
		//assertEquals("juho", request.getParameter().get("userId"));
	}
	
	@Test
	public void request_POST() throws Exception{
		//File::ish
		//File::
		//File [] HiddenFiles = new File(".").listFiles(File::isHidden);
		InputStream in = new FileInputStream(new File(testDir+"Http_Post.txt")); 
		HttpRequest request = new HttpRequest(in);
		assertEquals("POST", request.getMethod());
		assertEquals("/user/create", request.getPath());
		assertEquals("keep-alive", request.getHeader().get("Connection"));
		assertEquals("javajigi", request.getParameter().get("userId"));
		assertEquals("46", request.getHeader().get("Content-Length"));
	}
}
