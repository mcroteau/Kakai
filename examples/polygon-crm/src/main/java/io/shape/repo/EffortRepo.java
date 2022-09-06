package io.shape.repo;

import io.kakai.Kakai;
import io.kakai.annotate.Bind;
import io.kakai.annotate.Persistence;
import io.shape.model.Effort;
import io.shape.model.ProspectActivity;

import java.util.ArrayList;
import java.util.List;

@Persistence
public class EffortRepo {

    @Bind
    Kakai kakai;

    public long getId() {
        String sql = "select max(id) from efforts";
        long id = kakai.getLong(sql, new Object[]{});
        return id;
    }

    public Long getCount() {
        String sql = "select count(*) from efforts";
        Long count = kakai.getLong(sql, new Object[]{});
        return count;
    }

    public Long getCount(Long id) {
        String sql = "select count(*) from efforts where status_id = [+]";
        Long count = (Long) kakai.get(sql, new Object[]{ id }, Long.class);
        return count;
    }

    public Effort get(long id){
        String sql = "select * from efforts where id = [+]";
        Effort effort = (Effort) kakai.get(sql, new Object[]{ id }, Effort.class);
        return effort;
    }

    public List<Effort> getList(){
        String sql = "select * from efforts";
        List<Effort> efforts = (ArrayList) kakai.getList(sql, new Object[]{}, Effort.class);
        return efforts;
    }

    public List<Effort> getList(Long id){
        String sql = "select * from efforts where prospect_id = [+]";
        List<Effort> efforts = (ArrayList) kakai.getList(sql, new Object[]{ id }, Effort.class);
        return efforts;
    }

    public Effort save(Effort effort){
        String sql = "insert into efforts (start_date, prospect_id, starting_status_id) values ([+], [+], [+])";
        kakai.save(sql, new Object[] {
                effort.getStartDate(),
                effort.getProspectId(),
                effort.getStartingStatusId()
        });

        Long id = getId();
        Effort savedEffort = get(id);
        return savedEffort;
    }

    public boolean update(Effort effort) {
        String sql = "update efforts set end_date = [+], finished = [+], ending_status_id = [+], success = [+] where id = [+]";
        kakai.update(sql, new Object[] {
                effort.getEndDate(),
                effort.getFinished(),
                effort.getEndingStatusId(),
                effort.getSuccess(),
                effort.getId()
        });
        return true;
    }

    public boolean delete(long id){
        String sql = "delete from efforts where id = [+]";
        kakai.delete(sql, new Object[] { id });
        return true;
    }

    public List<ProspectActivity> getActivities(Long id){
        String sql = "select pa.id, pa.activity_id, pa.effort_id, pa.complete_date, a.name " +
                "from prospect_activities pa inner join activities a on pa.activity_id = a.id " +
                "where pa.effort_id = [+] order by pa.complete_date asc";

        List<ProspectActivity> prospectActivities = (ArrayList) kakai.getList(sql, new Object[]{ id }, ProspectActivity.class);
        return prospectActivities;
    }

    public boolean deleteActivity(Long id) {
        String sql = "delete from prospect_activities where effort_id = [+]";
        kakai.delete(sql, new Object[] { id });
        return true;
    }

    public boolean deleteActivities(Long id) {
        String sql = "delete from prospect_activities where effort_id = [+]";
        kakai.delete(sql, new Object[] { id });
        return true;
    }

    public Long getActivityCount() {
        String sql = "select count(*) from prospect_activities where effort_id >= 0";
        Long count = (Long) kakai.get(sql, new Long[]{}, Long.class);
        return count;
    }

    public List<Effort> getSuccesses() {
        String sql = "select * from efforts where success = true";
        List<Effort> efforts = (ArrayList) kakai.get(sql, new Object[]{ }, Effort.class);
        return efforts;
    }

    public Effort getProspectEffort(Long id, Boolean finished) {
        String sql = "select * from efforts where prospect_id = [+] and finished = [+]";
        Effort effort = (Effort) kakai.get(sql, new Object[]{id, finished}, Effort.class);
        return effort;
    }
}
