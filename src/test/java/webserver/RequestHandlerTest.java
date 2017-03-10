package webserver;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import util.HttpRequestUtils;
import util.HttpRequestUtils.Pair;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import model.User;

public class RequestHandlerTest {
	private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
	
	@Test
	public void test_parseQueryString()
	{
		String req = "/user/create?userId=Juho+Sung&password=1234&name=%EC%84%B1%EC%A3%BC%ED%98%B8&email=kxjooho%40gmail.com";
		Map<String,String> m = util.HttpRequestUtils.parseQueryString(req);
		//Assert
		// assertThat(parameters.get("userId"), is("javajigi"));
		//assertEquals("Juho Sung", m.get("userId"));
		//assertEquals("Juho Sung", m.get("userId"));
		assertThat(m.get("userId"), is("Juho Sung"));
		//Assert.
		
		//how..?
		//request_handler = new RequestHandler(null);
	}
	@Test
	public void testHH()
	{
		String str = "Host: Juho";
		Pair pair = util.HttpRequestUtils.parseHeader(str);
		assertThat(pair.getValue(),is("Juho"));
	}
	
	@Test(expected=ClassNotFoundException.class )
	public void testQueryString() throws ClassNotFoundException, InstantiationException, IllegalAccessException
	{
		String class_name = "User";
		Class cls = Class.forName(class_name);
		Object obj = cls.newInstance();
		//assertThat(obj.getClass(),User.class);
		log.debug("hello:"+obj.toString());
		assertEquals(obj.getClass() , User.class);
		
	}
	@Test
	public void test_getOperation()
	{
		String str = "/user/juho/create";
		int index = str.lastIndexOf("/");
		String operation_name = str.substring(index+1);
		String target_resource = str.substring(0, index);
		
		assertThat(operation_name,is("create"));
		assertThat(target_resource,is("/user/juho"));
	}
	@Test
	public void test_parseHeader() throws IOException
	{
		String req = "Accept: */*\n\nuserId=javajig&password=password";
		byte [] data = "abcdefg\n\nggg".getBytes();
		
		ByteArrayInputStream bi = new ByteArrayInputStream(data);
		BufferedReader br = new BufferedReader(new InputStreamReader(bi,"UTF-8"));
		String line = null;
		
		while((line=br.readLine())!=null && !"".equals(line)){
			log.debug("test:"+line);
			
		}
		log.debug(br.readLine());
		
		
		Pair pair = HttpRequestUtils.parseHeader(req);
		
		
		
	}
	
	
	
}
