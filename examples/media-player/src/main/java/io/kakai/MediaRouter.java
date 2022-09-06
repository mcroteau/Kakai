package io.kakai;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import io.kakai.annotate.Bind;
import io.kakai.annotate.Design;
import io.kakai.annotate.Router;
import io.kakai.annotate.Variable;
import io.kakai.annotate.http.Get;
import io.kakai.annotate.http.Post;
import io.kakai.model.web.FileComponent;
import io.kakai.model.web.RequestComponent;
import io.kakai.model.web.HttpRequest;
import io.kakai.model.web.HttpResponse;
import io.kakai.resources.MimeGetter;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Router
public class MediaRouter {

    @Bind
    Kakai kakai;

    @Get("/")
    @Design("/pages/default.jsp")
    public String index(HttpResponse resp){
        String sql = "select * from media";
        List<Media> music = (ArrayList) kakai.getList(sql, new Object[]{}, Media.class);
        resp.set("music", music);
        return "/pages/index.jsp";
    }

    @Get("/view/upload")
    @Design("/pages/default.jsp")
    public String upload(){
        return "/pages/upload.jsp";
    }

    @Post("/upload")
    public String upload(HttpRequest req, HttpResponse resp) throws IOException {
        RequestComponent requestComponent = req.getRequestComponent("media");
        FileComponent fileComponent = requestComponent.getFiles().get(0);
        String title = req.value("title");
        String artist = req.value("artist");
        String uri = fileComponent.getFileName();

        Path mediaPath = Paths.get("web-ux", "resources", "media");
        String completeMediaDirPath = mediaPath.toAbsolutePath().toString();
        File mediaDir = new File(completeMediaDirPath);
        if(!mediaDir.exists()){
            mediaDir.mkdirs();
        }

        Path path = Paths.get("web-ux", "resources", "media", uri);
        File file = new File(path.toAbsolutePath().toString());
        if(file.exists()){
            resp.set("message", "Song exists.");
            return "[redirect]/upload";
        }

        OutputStream os = new FileOutputStream(file);
        os.write(fileComponent.getFileBytes());
        os.flush();
        os.close();

        String locateSql = "select * from media where uri = '[+]'";
        Media storedMedia = (Media) kakai.get(locateSql, new Object[]{ uri }, Media.class);

        if(storedMedia != null){
            resp.set("message", "Song exists.");
            return "[redirect]/upload";
        }

        Media media = new Media();
        media.setUri(uri);
        media.setTitle(title);
        media.setArtist(artist);

        String saveSql = "insert into media (uri, title, artist) values ('[+]','[+]','[+]')";
        kakai.save(saveSql, new Object[]{
                media.getUri(),
                media.getTitle(),
                media.getArtist()
        });

        resp.set("message", "Successfully uploaded media!");
        return "[redirect]/";
    }

    @io.kakai.annotate.Media
    @Get("/listen/{id}")
    public String listen(@Variable Integer id, HttpExchange httpExchange) throws IOException {

        String sql = "select * from media where id = [+]";
        Media storedMedia = (Media) kakai.get(sql, new Object[]{ id }, Media.class);

        Path path = Paths.get("web-ux", "resources", "media", storedMedia.uri);
        String completePath = path.toAbsolutePath().toString();

        File file = new File(completePath);
        FileInputStream inputStream = new FileInputStream(file);
        byte[] bytes = new byte[(int)file.length()];

        MimeGetter mimeGetter = new MimeGetter(completePath);
        Headers headers = httpExchange.getResponseHeaders();
        headers.add("content-type", mimeGetter.resolve());
        httpExchange.sendResponseHeaders(200, bytes.length);

        OutputStream outputStream = httpExchange.getResponseBody();

        int bytesRead;
        try {
            while ((bytesRead = inputStream.read(bytes, 0, bytes.length)) != -1) {
                outputStream.write(bytes, 0, bytesRead);
            }

            inputStream.close();
            outputStream.flush();
            outputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
