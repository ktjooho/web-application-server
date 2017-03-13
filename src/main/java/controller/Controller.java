package controller;

import http.HttpRequest;
import http.HttpResponse;

public interface Controller {
	public void service(HttpRequest request, HttpResponse response);
}

