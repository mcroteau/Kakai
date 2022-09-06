package io.shape.repo;

import io.kakai.Kakai;
import io.kakai.annotate.Bind;
import io.kakai.annotate.Persistence;
import io.shape.model.Role;

import java.util.ArrayList;
import java.util.List;

@Persistence
public class RoleRepo {

	@Bind
	Kakai kakai;

	public int count() {
		String sql = "select count(*) from roles";
		int count = kakai.getInt(sql, new Object[] { });
		return count;
	}

	public Role get(int id) {
		String sql = "select * from roles where id = [+]";
		Role role = (Role) kakai.get(sql, new Object[] { id },Role.class);
		return role;
	}

	public Role get(String name) {
		String sql = "select * from roles where name = '[+]'";
		Role role = (Role) kakai.get(sql, new Object[] { name }, Role.class);
		return role;
	}

	public List<Role> findAll() {
		String sql = "select * from roles";
		List<Role> roles = (ArrayList) kakai.getList(sql, new Object[]{}, Role.class);
		return roles;
	}

	public void save(Role role) {
		String sql = "insert into roles (name) values('[+]')";
		kakai.save(sql, new Object[]{
				role.getName()
		});
	}

}