package io.shape.repo;

import io.kakai.Kakai;
import io.kakai.annotate.Bind;
import io.kakai.annotate.Persistence;
import io.shape.model.Status;

import java.util.ArrayList;
import java.util.List;

@Persistence
public class StatusRepo {

    @Bind
    Kakai kakai;

    public long getId() {
        String sql = "select max(id) from statuses";
        long id = kakai.getLong(sql, new Object[]{});
        return id;
    }

    public long getCount() {
        String sql = "select count(*) from statuses";
        Long count = kakai.getLong(sql, new Object[] { });
        return count;
    }

    public Status get(long id){
        String sql = "select * from statuses where id = [+]";
        Status status = (Status) kakai.get(sql, new Object[]{ id }, Status.class);
        return status;
    }

    public List<Status> getList(){
        String sql = "select * from statuses";
        List<Status> statuses = (ArrayList) kakai.getList(sql, new Object[]{}, Status.class);
        return statuses;
    }

    public Status save(Status status){
        String sql = "insert into statuses (name) values ('[+]')";
        kakai.update(sql, new Object[] {
                status.getName()
        });

        Long id = getId();
        Status savedStatus = get(id);
        return savedStatus;
    }

    public boolean update(Status status) {
        String sql = "update statuses set name = '[+]' where id = [+]";
        kakai.update(sql, new Object[] {
                status.getName(),
                status.getId()
        });
        return true;
    }

    public boolean delete(long id){
        String sql = "delete from statuses where id = [+]";
        kakai.delete(sql, new Object[] { id });
        return true;
    }

}
