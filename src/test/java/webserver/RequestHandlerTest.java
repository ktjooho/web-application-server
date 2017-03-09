package webserver;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class RequestHandlerTest {
	
	@Test
	public void init()
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
	
	
	
}
