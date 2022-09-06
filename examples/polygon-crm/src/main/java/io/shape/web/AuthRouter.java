package io.shape.web;

import io.kakai.annotate.Bind;
import io.kakai.annotate.Design;
import io.kakai.annotate.Router;
import io.kakai.annotate.http.Get;
import io.kakai.annotate.http.Post;
import io.kakai.model.web.HttpRequest;
import io.kakai.model.web.HttpResponse;
import io.shape.service.AuthService;

@Router
public class AuthRouter {

	@Bind
	AuthService authService;

	@Post("/authenticate")
	public String authenticate(HttpRequest req,
							   HttpResponse resp){
		return authService.authenticate(req, resp);
	}

	@Get("/signin")
	@Design("/designs/guest.jsp")
	public String signin(HttpResponse resp){
		return "/pages/signin.jsp";
	}

	@Get("/signup")
	@Design("/designs/guest.jsp")
	public String signup(HttpResponse resp){
		return "/pages/signup.jsp";
	}

	@Get("/signout")
	public String signout(HttpRequest req,
						  HttpResponse resp){
		return authService.deAuthenticate(req, resp);
	}

	@Get("/unauthorized")
	@Design("/designs/guest.jsp")
	public String unauthorized(HttpResponse resp){
		return "/pages/401.jsp";
	}

}