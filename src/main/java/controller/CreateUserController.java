package controller;

import java.io.IOException;
import java.util.Map;

import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;
import model.User;

public class CreateUserController extends AbstractController {
	
	//create/user
	@Override
	public void doPost(HttpRequest request, HttpResponse response) {
		// TODO Auto-generated method stub
		Map<String,String> params = request.getParameter();
		User user = new User(params.get("userId"), params.get("password"), params.get("name"), params.get("password"));
		DataBase.addUser(user);
		
		try {
			response.sendRedirect("/index.html");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage());
		}
	}

}
