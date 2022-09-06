<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div id="reset-password-form">

	<c:if test="${not empty message}">
		<div class="notify">${message}</div>
	</c:if>

	<h2>Reset Password</h2>

    <p style="line-height:1.23em;">Enter phone that is registered with us to reset your password</p>

	<form action="${pageContext.request.contextPath}/users/send" method="post">

        <fieldset>

            <br/>
            <label for="phone">Cell Phone</label>
            <input id="phone" type="text" placeholder="" name="phone" style="width:100%;">

            <br/>

            <div style="text-align:right;margin:30px 0px 170px;">
                <input type="submit" class="button retro" id="reset-password" value="Reset Password"/>
            </div>
        </fieldset>

	</form>

</div>


