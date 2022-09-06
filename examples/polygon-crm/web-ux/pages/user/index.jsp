<div class="inside-container">
	<kakai:if spec="${message != ''}">
		<div class="notify">${message}</div>
	</kakai:if>

	<h1 class="left-float">Users</h1>

	<a href="/users/create" class="button orange right-float">New User!</a>
	<br class="clear"/>

	<kakai:if spec="${users.size() > 0}">
		<div class="span12">
			<table class="table table-condensed">
				<thead>
					<tr>
						<th>Id</th>
						<th>Name</th>
						<th>Phone</th>
						<th></th>
					</tr>
				</thead>
				<tbody>
					<kakai:iterate items="${users}" var="user">
						<tr>
							<td>${user.id}</td>
							<td>${user.name}</td>
							<td>${user.phone}</td>
							<td><a href="/users/edit/${user.id}" title="Edit" class="button retro">Edit</a>
						</tr>
					</kakai:iterate>
				</tbody>
			</table>
		</div>
	</kakai:if>

	<kakai:if spec="${users.size() == 0}">
		<p>No users created yet.</p>
	</kakai:if>
</div>
</body>
</html>