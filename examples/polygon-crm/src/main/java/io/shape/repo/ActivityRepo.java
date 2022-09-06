package io.shape.repo;

import io.kakai.Kakai;
import io.kakai.annotate.Bind;
import io.kakai.annotate.Persistence;
import io.shape.model.Activity;

import java.util.ArrayList;
import java.util.List;

@Persistence
public class ActivityRepo {

    @Bind
    Kakai kakai;

    public long getId() {
        String sql = "select max(id) from activities";
        long id = kakai.getLong(sql, new Object[]{});
        return id;
    }

    public Long getCount() {
        String sql = "select count(*) from activities";
        Long count = kakai.getLong(sql, new Object[]{});
        return count;
    }

    public Activity get(long id){
        String sql = "select * from activities where id = [+]";
        Activity activity = (Activity) kakai.get(sql, new Object[]{id}, Activity.class);
        return activity;
    }

    public Activity get(String name){
        String sql = "select * from activities where name = '[+]'";
        Activity activity = (Activity) kakai.get(sql, new Object[]{name}, Activity.class);
        return activity;
    }

    public List<Activity> getList(){
        String sql = "select * from activities";
        List<Activity> activities = (ArrayList) kakai.getList(sql, new Object[]{}, Activity.class);
        return activities;
    }

    public Activity save(Activity activity){
        String sql = "insert into activities (name) values ('[+]')";
        kakai.update(sql, new Object[] {
            activity.getName()
        });

        Long id = getId();
        Activity savedActivity = get(id);
        return savedActivity;
    }

    public boolean update(Activity activity) {
        String sql = "update activities set name = '[+]' where id = [+]";
        kakai.update(sql, new Object[] {
                activity.getName(),
                activity.getId()
        });
        return true;
    }

    public boolean delete(long id){
        String sql = "delete from activities where id = [+]";
        kakai.delete(sql, new Object[] { id });
        return true;
    }

}
