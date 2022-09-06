package io.kakai.jdbc;

import io.kakai.resources.Resources;
import org.h2.tools.RunScript;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;

public class DatabaseMediator {

    Logger Log = Logger.getLogger("DatabaseMediator");

    Resources resources;

    final Integer CONNECTIONS  = 301;
    final String DB_DRIVER     = "org.h2.Driver";
    final String DB_NAME       = ".kakai.database.tmp";
    final String DB_URL_PREFIX = "jdbc:h2:";
    final String DB_USER       = "sa";
    final String DB_PASS       = "";

    final String SCHEMA_FILE = "schema.sql";
    final String SCHEMA_URI = "src/main/resources/schema.sql";

    DataSource datasource;

    public DatabaseMediator(Resources resources){
        this.resources = resources;
    }

    public void createDatabase() throws Exception {

        String artifactPath = resources.getResourceUri();
        if(artifactPath.equals("")){
            Log.info("non-persistence mode");
        }

        InputStream in;
        if (resources.isJar()) {
            JarFile jarFile = resources.getJarFile();
            JarEntry jarEntry = jarFile.getJarEntry(SCHEMA_URI);
            in = jarFile.getInputStream(jarEntry);
        } else {
            File createFile = new File(artifactPath + File.separator + SCHEMA_FILE);
            if(!createFile.exists()){
                Log.info("schema.sql missing in src/main/resources/. project will be treated as a non persistent application.");
                return;
            }
            in = new FileInputStream(createFile);
        }

        StringBuilder schemaSql = new StringBuilder();
        if(in == null || in.available() == 0){
            Log.info("src/main/resources/schema.sql contains no tables. project will be treated as a non persistent application.");
            return;
        }
        if (in.available() > 0) schemaSql = resources.convert(in);

        Path path = Paths.get("src", "main", "resources");
        String completePath = path.toAbsolutePath().toString();
        String dbUrl = DB_URL_PREFIX + completePath + File.separator + DB_NAME;

        this.datasource = new ExecutableDatasource.Builder()
                .connections(CONNECTIONS)
                .url(dbUrl)
                .driver(DB_DRIVER)
                .user(DB_USER)
                .password(DB_PASS)
                .create();

        Connection conn = datasource.getConnection();

        RunScript.execute(conn, new StringReader("drop all objects;"));

        if (!schemaSql.toString().equals("")) {
            RunScript.execute(conn, new StringReader(schemaSql.toString()));
        }
        conn.commit();
        conn.close();

        Log.info("development environment setup");
    }

    public DataSource getDevelopmentDatasource(){
        return this.datasource;
    }
}

