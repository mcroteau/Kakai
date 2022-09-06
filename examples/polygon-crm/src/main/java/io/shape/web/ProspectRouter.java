package io.shape.web;

import io.kakai.annotate.*;
import io.kakai.annotate.http.Get;
import io.kakai.annotate.http.Post;
import io.kakai.model.web.HttpRequest;
import io.kakai.model.web.HttpResponse;
import io.shape.service.ProspectService;

@Router
public class ProspectRouter {

    @Bind
    ProspectService prospectService;

    @Text
    @Get("/data")
    public String data(){
        return prospectService.data();
    }

    @Get("/snapshot")
    @Design("/designs/auth.jsp")
    public String snapshot(HttpResponse resp){
        return prospectService.snapshot(resp);
    }

    @Get("/prospects")
    @Design("/designs/auth.jsp")
    public String searchScreen(HttpRequest req,
                                HttpResponse resp){
        return prospectService.searchScreen(req, resp);
    }

    @Get("/prospects/search")
    @Design("/designs/auth.jsp")
    public String getProspects(HttpRequest req,
                              HttpResponse resp){
        return prospectService.getProspects(resp, req);
    }

    @Get("/prospects/{id}")
    @Design("/designs/auth.jsp")
    public String index(HttpResponse resp,
                        @Variable Long id){
        return prospectService.index(id, resp);
    }

    @Get("/prospects/create")
    @Design("/designs/auth.jsp")
    public String create(HttpResponse resp){
        return prospectService.create(resp);
    }

    @Post("/prospects/save")
    public String save(HttpRequest req,
                       HttpResponse resp){
        return prospectService.save(resp, req);
    }

    @Get("/prospects/edit/{id}")
    @Design("/designs/auth.jsp")
    public String getEdit(HttpResponse resp,
                          @Variable Long id){
        return prospectService.getEdit(id, resp);
    }

    @Post("/prospects/update")
    public String update(HttpRequest req,
                         HttpResponse resp){
        return prospectService.update(resp, req);
    }

    @Post("/prospects/delete/{id}")
    public String delete(HttpRequest req,
                         HttpResponse resp,
                         @Variable Long id){
        return prospectService.delete(id, resp);
    }

    @Get("/prospects/history/{id}")
    @Design("/designs/auth.jsp")
    public String history(HttpResponse resp,
                          @Variable Long id){
        return prospectService.history(id, resp);
    }

    @Get("/prospects/activity/add/{id}")
    @Design("/designs/auth.jsp")
    public String addActivity(HttpResponse resp,
                                @Variable Long id){
        return prospectService.addActivity(id, resp);
    }

    @Post("/prospects/activity/save/{id}")
    public String saveActivity(HttpRequest req,
                              HttpResponse resp,
                              @Variable Long id){
        return prospectService.saveActivity(id, resp, req);
    }

    @Get("/prospects/activity/edit/{id}")
    @Design("/designs/auth.jsp")
    public String editActivity(HttpResponse resp,
                              @Variable Long id){
        return prospectService.editActivity(id, resp);
    }

    @Post("/prospects/activity/update/{id}")
    public String updateActivity(HttpRequest req,
                                 HttpResponse resp,
                                 @Variable Long id){
        return prospectService.updateActivity(id, resp, req);
    }

    @Post("/prospects/activity/delete/{id}")
    public String deleteActivity(HttpRequest req,
                                 HttpResponse resp,
                                 @Variable Long id){
        return prospectService.deleteActivity(id, resp, req);
    }

    @Post("/prospects/activity/complete/{id}")
    public String completeActivity(HttpRequest req,
                             HttpResponse resp,
                             @Variable Long id){
        return prospectService.completeActivity(id, resp, req);
    }

    @Post("/prospects/effort/save/{id}")
    public String saveEffort(HttpRequest req,
                               HttpResponse resp,
                               @Variable Long id){
        return prospectService.saveEffort(id, resp, req);
    }

    @Post("/prospects/effort/stop/{id}")
    public String stopEffort(HttpRequest req,
                             HttpResponse resp,
                             @Variable Long id){
        return prospectService.stopEffort(id, resp, req);
    }

    @Get("/prospects/notes/edit/{id}")
    @Design("/designs/auth.jsp")
    public String editNotes(HttpResponse resp,
                            @Variable Long id){
        return prospectService.editNotes(id, resp);
    }

    @Post("/prospects/notes/update/{id}")
    public String updateNotes(HttpRequest req,
                             HttpResponse resp,
                             @Variable Long id){
        return prospectService.updateNotes(id, resp, req);
    }

    @Post("/prospects/sale/{id}")
    public String markSale(HttpRequest req,
                              HttpResponse resp,
                              @Variable Long id){
        return prospectService.markSale(id, resp, req);
    }
}
