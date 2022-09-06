package io.kakai.extras;

import io.kakai.Kakai;
import io.kakai.annotate.Bind;
import io.kakai.annotate.Repo;

import java.util.ArrayList;
import java.util.List;


@Repo
public class TodoRepo {

    @Bind
    Kakai kakai;

    public Long getCount() {
        String sql = "select count(*) from todos";
        Long count = kakai.getLong(sql, new Object[]{});
        return count;
    }

    public Todo getById(int id){
        String sql = "select * from todos where id = [+]";
        Todo todo = (Todo) kakai.get(sql, new Object[]{ id }, Todo.class);
        return todo;
    }

    public List<Todo> getList(){
        String sql = "select * from todos";
        List<Todo> todos = (ArrayList) kakai.getList(sql, new Object[]{}, Todo.class);
        return todos;
    }

    public void save(Todo todo){
        String sql = "insert into todos (title) values ('[+]')";
        kakai.save(sql, new Object[] {
                todo.getTitle()
        });
    }

    public void update(Todo todo) {
        String sql = "update todos set title = '[+]', complete = [+] where id = [+]";
        kakai.update(sql, new Object[] {
                todo.getTitle(),
                todo.isComplete(),
                todo.getId()
        });
    }

    public void delete(int id){
        String sql = "delete from todos where id = [+]";
        kakai.delete(sql, new Object[] { id });
    }

    public List<Person> getPeople(int id){
        String sql = "select * from todo_people where todo_id = [+]";
        List<Person> todoPeople = (ArrayList) kakai.getList(sql, new Object[]{ id }, Person.class);
        return todoPeople;
    }

    public void savePerson(Person person) {
        String sql = "insert into todo_people (todo_id, person) values ([+],'[+]')";
        kakai.save(sql, new Object[] {
                person.getTodoId(),
                person.getName()
        });
    }

    public void deletePerson(Integer id) {
        String sql = "delete from todo_people where id = [+]";
        kakai.delete(sql, new Object[] { id });
    }
}
