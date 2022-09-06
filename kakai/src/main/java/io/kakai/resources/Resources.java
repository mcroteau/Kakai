package io.kakai.resources;

import io.kakai.Kakai;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.sun.net.httpserver.Headers;
import io.kakai.exception.KakaiException;

public class Resources {

    boolean isJar;

    public Resources(){
        this.isJar = this.isFat();
    }

    public String removeLast(String s) {
        return (s == null || s.length() == 0) ? ""
                : (s.substring(0, s.length() - 1));
    }

    public boolean isJar(){ return this.isJar; }

    public Boolean isFat(){
        String uri = null;
        try {
            uri = getClassesUri();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return uri.contains("jar:file:") ? true : false;
    }

    public String getPayload(byte[] bytes){
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append((char) b);
        }
        return sb.toString();
    }

    public static String SESSION_GUID(int z) {
        String CHARS = ".01234567890";
        StringBuilder guid = new StringBuilder();
        guid.append("kakai.");
        Random rnd = new Random();
        while (guid.length() < z) {
            int index = (int) (rnd.nextFloat() * CHARS.length());
            guid.append(CHARS.charAt(index));
        }
        return guid.toString();
    }


    public byte[] getPayloadBytes(InputStream requestStream) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            byte[] buf = new byte[1024 * 19];
            int bytesRead=0;
            while ((bytesRead = requestStream.read(buf)) != -1){
                bos.write(buf, 0, bytesRead);
            }
            requestStream.close();
            bos.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return bos.toByteArray();
    }

    public Enumeration getJarEntries(){
        JarFile jarFile = getJarFile();
        return jarFile.entries();
    }

    public String getName(String nameWithExt){
        int index = nameWithExt.lastIndexOf(".");
        String qualifiedName = nameWithExt;
        if(index > 0){
            qualifiedName = qualifiedName.substring(index + 1);
        }
        return qualifiedName.toLowerCase();
    }

    public String getMain(){
        try {
            JarFile jarFile = getJarFile();
            JarEntry jarEntry = jarFile.getJarEntry("META-INF/MANIFEST.MF");
            InputStream in = jarFile.getInputStream(jarEntry);
            Scanner scanner = new Scanner(in);

            String line = "";
            do{
                line = scanner.nextLine();
                if(line.contains("Main-Class")){
                    line = line.replace("Main-Class", "");
                    break;
                }
            }
            while(scanner.hasNext());

            line = line.replace(":", "").trim();
            return line;

        } catch (IOException ioex) {
            ioex.printStackTrace();
        }


        throw new IllegalStateException("Apologies, it seems you are trying to run this as a jar but have not main defined.");
    }

    public JarFile getJarFile(){
        try {
            URL jarUri = Kakai.class.getClassLoader().getResource("io/kakai/");
            String jarPath = jarUri.getPath().substring(5, jarUri.getPath().indexOf("!"));

            return new JarFile(jarPath);
        }catch(IOException ex){
            ex.printStackTrace();
        }
        return null;
    }

    public String getClassesUri() throws Exception {
        Path path = Paths.get("build", "classes");
        Path absolutePath = path.toAbsolutePath();
        String classesUri = absolutePath.toString();
        File classesDir = new File(classesUri);
        if(classesDir.exists()){
            return classesUri;
        }

        classesUri = this.getClass().getResource("").toURI().toString();
        if(classesUri == null){
            throw new KakaiException("unable to locate class uri");
        }
        return classesUri;
    }


    public String getProject() {
        if(isJar) {
            JarFile jarFile = getJarFile();
            String path = jarFile.getName();
            String[] bits = path.split("/");
            if(bits.length == 0){
                bits = path.split("\\");
            }
            String namePre = bits[bits.length - 1];
            return namePre.replace(".jar", "");
        }else{
            return "";
        }
    }

    public StringBuilder convert(InputStream in){
        StringBuilder builder = new StringBuilder();
        Scanner scanner = new Scanner(in);
        do {
            builder.append(scanner.nextLine() + "\n");
        } while(scanner.hasNext());
        try {
            in.close();
        }catch (IOException ioex) {
            ioex.printStackTrace();
        }
        return builder;
    }


    public String getCookie(String cookieName, Headers headers){
        String value = "";
        if(headers != null) {
            List<String> cookies = headers.get("Cookie");
            if(cookies != null) {
                for (String cookie : cookies) {
                    String[] bits = cookie.split(";");
                    for (String completes : bits) {
                        String[] parts = completes.split("=");
                        String key = parts[0].trim();
                        if (parts.length > 1) {

                            if (key.equals(cookieName)) {
                                value = parts[1].trim();
                            }
                        }
                    }
                }
            }
        }
        //returning the last one.
        return value;
    }

    public String getResourceUri() throws Exception{
        //todo:boolean jar check
//        final String RESOURCES_URI = "/src/main/resources/";
//        URL indexUri = Kakai.class.getResource(RESOURCES_URI);
//        return indexUri.toURI().toString();

        String resourceUri = Paths.get("src", "main", "resources")
                .toAbsolutePath()
                .toString();
        File resourceDir = new File(resourceUri);
        if(!resourceDir.exists()){
            resourceDir.mkdirs();
        }
        return resourceUri;

    }

}
