package io.shape.web;

import io.kakai.annotate.Bind;
import io.kakai.annotate.Design;
import io.kakai.annotate.Router;
import io.kakai.annotate.Variable;
import io.kakai.annotate.http.Get;
import io.kakai.annotate.http.Post;
import io.kakai.model.web.HttpRequest;
import io.kakai.model.web.HttpResponse;
import io.shape.service.UserService;

@Router
public class UserRouter {

	@Bind
	UserService userService;

	@Get("/users")
	@Design("/designs/auth.jsp")
	public String getUsers(HttpResponse resp){
		return userService.getUsers(resp);
	}

	@Get("/users/create")
	@Design("/designs/auth.jsp")
	public String create(HttpResponse resp){
		return userService.create(resp);
	}

	@Post("/users/save")
	public String save(HttpRequest req,
					   HttpResponse resp) {
		return userService.save(resp, req);
	}

	@Get("/users/edit/{id}")
	@Design("/designs/auth.jsp")
	public String getEditUser(HttpResponse resp,
							  @Variable Long id){
		return userService.getEditUser(id, resp);
	}

	@Post("/users/delete/{id}")
	public String deleteUser(HttpResponse resp,
							 @Variable Long id) {
		return userService.deleteUser(id, resp);
	}

	@Post("/users/update/{id}")
	public String updateUser(HttpRequest req,
							 HttpResponse resp,
							 @Variable Long id){
		return userService.updateUser(id, resp, req);
	}

	@Get("/users/reset")
	@Design("/designs/guest.jsp")
	public String reset(){
		return "/pages/user/reset.jsp";
	}

	@Post("/users/send")
	public String sendReset(HttpRequest request,
							HttpResponse resp){
		return userService.sendReset(resp, request);
	}

	@Post("/users/reset/{id}")
	@Design("/designs/guest.jsp")
	public String resetPassword(HttpRequest req,
								HttpResponse resp,
								@Variable Long id){
		return userService.resetPassword(id, resp, req);
	}

}