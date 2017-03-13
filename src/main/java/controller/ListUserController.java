package controller;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;
import model.User;
import util.HttpRequestUtils;

public class ListUserController extends AbstractController {

	@Override
	public void doGet(HttpRequest request, HttpResponse response) {
		// TODO Auto-generated method stub
		String line = request.getHeader("Cookie");
		log.debug("Cookie Line : {}",line);
		if(!isLogin(line)) {
			try {
				response.sendRedirect("/user/login.html");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				log.error(e.getMessage());
			}
			return ;
		} 
		
		Collection<User> users = DataBase.findAll();
        StringBuilder sb = new StringBuilder();
        sb.append("<table border='1'>");
        for (User user : users) {
            sb.append("<tr>");
            sb.append("<td>" + user.getUserId() + "</td>");
            sb.append("<td>" + user.getName() + "</td>");
            sb.append("<td>" + user.getEmail() + "</td>");
            sb.append("</tr>");
        }
        sb.append("</table>");
        
        try {
			response.forwardData(sb.toString().getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage());
		}
	}
	private boolean isLogin(String line){
		Map<String,String> cookies = HttpRequestUtils.parseCookies(line);
		String value = cookies.get("logined");
		if(value==null){
			return false;
		}
		return Boolean.parseBoolean(value);
	}
}
