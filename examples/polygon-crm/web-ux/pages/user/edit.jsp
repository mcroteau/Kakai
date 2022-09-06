
<div class="inside-container">

	<a href="/users" id="prospect-back" class="href-dotted">&larr;&nbsp;Back</a>

	<kakai:if spec="${message != ''}">
		<div class="notify">${message}</div>
	</kakai:if>

	<h1>Edit User</h1>

	<kakai:if test="${activityCounts.size() > 0}">
		<h3>Great Job!</h3>
		<p>You're always doing a great job.
			Let's see what you've been up to.</p>
		<kakai:iterate items="${activityCounts}" var="activityCount">
			<p>${activityCount.count} ${activityCount.name}s</p>
		</kakai:iterate>
		<p>Not bad...</p>
	</kakai:if>
	<kakai:if spec="${activityCounts.size() == 0}">
		Nothing to show yet.
	</kakai:if>

	<form action="/users/update/${user.id}" method="post">

		<label>Name</label>
		<p class="information"></p>
		<input type="text" name="name" placeholder="" value="${user.name}"/>

		<label>Cell Phone</label>
		<span class="tiny">The application uses your cell phone to send notification updates.</span>
		<input type="text" name="phone" placeholder="9079878652" value="${user.phone}"/>

		<div class="button-wrapper">
			<input type="submit" value="Update User" class="button green"/>
		</div>
	</form>

	<a href="/signout" class="href-dotted">Signout</a>

</div>

