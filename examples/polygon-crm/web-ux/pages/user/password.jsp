<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div id="edit-user-form">

	<c:if test="${not empty message}">
		<div class="notify">
			${message}
		</div>
	</c:if>
		
	<h1>Update Password</h1>
	
	<form action="${pageContext.request.contextPath}/user/update_password/${user.id}" class="form" modelAttribute="user" method="post">
		
		<input type="hidden" name="id" value="${user.id}"/>

		<div class="form-group">
		  	<label for="password">Password</label>
		  	<input type="text" name="password" id="password" placeholder="******" value="">
		</div>
		
		<div class="form-group">
			<input type="submit" class="button" id="update" value="Update"/>
		</div>
		
	</form>
	
</div>
	
		
