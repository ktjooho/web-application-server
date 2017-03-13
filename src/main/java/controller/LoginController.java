package controller;

import java.io.IOException;
import java.util.Map;

import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;
import model.User;

public class LoginController extends AbstractController {

	@Override
	public void doGet(HttpRequest request, HttpResponse response) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doPost(HttpRequest request, HttpResponse response) {
		// TODO Auto-generated method stub
		Map<String, String>params = request.getParameter();
		User user = DataBase.findUserById(params.get("userId"));
		
		if(user!=null && user.login(params.get("password"))) {
			response.addHeader("Set-Cookie", "logined=true");
			try {
				response.sendRedirect("/index.html");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				log.error(e.getMessage());
			}
		} else {
			try {
				response.sendRedirect("/user/login_failed.html");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				log.error(e.getMessage());
			}
		}
	}
}
