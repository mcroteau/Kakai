<div id="signup-form-container">

    <c:if test="${not empty message}">
        <div class="notify">${message}</div>
    </c:if>

    <p style="text-align: left; margin-top:30px;">Are you already a member?
        <a href="${pageContext.request.contextPath}/signin" class="href-dotted">Signin!</a>
    </p>


    <form action="${pageContext.request.contextPath}/register" method="post" id="registration-form">
        <fieldset style="text-align: left">

            <h2 style="margin-bottom:20px;">Signup</h2>

            <label>Email</label>
            <input id="username" type="email" placeholder="Email Address" name="username" style="width:100%;">

            <label>Password</label>
            <input id="password" type="password" placeholder="Password &#9679;&#9679;&#9679;" name="password" style="width:100%;">

        </fieldset>
    </form>


    <div style="width:100%;margin-top:41px;text-align:center;margin-bottom:30px; ">
        <input type="submit" class="button retro" id="signup-button" value="Signup!" style="width:100%;"/>
    </div>

    <br class="clear"/>

</div>


<script>

    var processing = false
    var form = document.getElementById("registration-form");
    var button = document.getElementById("signup-button");

    button.addEventListener("click", function(event){
        event.preventDefault();
        if(!processing){
            processing = true;
            form.submit();
        }
    })

</script>
