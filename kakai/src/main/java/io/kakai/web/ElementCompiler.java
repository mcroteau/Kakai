package io.kakai.web;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import io.kakai.Kakai;
import io.kakai.model.web.*;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Thank you Mr. Walter
 * https://gist.github.com/JensWalter/0f19780d131d903879a2
 */
public class ElementCompiler {

    Kakai kakai;
    byte[] bytes;
    Map<String, HttpSession> sessions;
    HttpExchange httpExchange;

    public ElementCompiler(Kakai kakai, byte[] bytes, Map<String, HttpSession> sessions, HttpExchange httpExchange){
        this.kakai = kakai;
        this.bytes = bytes;
        this.sessions = sessions;
        this.httpExchange = httpExchange;
    }

    public HttpRequest compile(){
        Headers headers = httpExchange.getRequestHeaders();

        HttpRequest httpRequest = new HttpRequest(sessions, httpExchange);

        String contentType = headers.getFirst("Content-Type");

        String delimeter = "";
        if(contentType != null) {
            String[] bits = contentType.split("boundary=");
            if (bits.length > 1) {
                delimeter = bits[1];
                StringBuilder sb = new StringBuilder();
                for (byte b : bytes) {
                    sb.append((char) b);
                }

                String payload = sb.toString();
                List<RequestComponent> requestComponentns = getRequestComponents(delimeter, payload);
                for (RequestComponent requestComponent : requestComponentns) {
                    String requestComponentKey = requestComponent.getName();
                    httpRequest.set(requestComponentKey, requestComponent);
                }
            }else if(bytes.length > 0){

                String query = "";
                try {

                    query = new String(bytes, "utf-8");
                    query = java.net.URLDecoder.decode(query, StandardCharsets.UTF_8.name());

                } catch (Exception ex){
                    ex.printStackTrace();
                }

                for (String entry : query.split("&")) {
                    RequestComponent element = new RequestComponent();
                    String[] keyValue = entry.split("=", 2);
                    String key = keyValue[0];
                    if(keyValue.length > 1){
                        String value = keyValue[1];
                        element.setName(key);
                        element.setValue(value);
                    }else{
                        element.setName(key);
                        element.setValue("");
                    }
                    httpRequest.put(key, element);
                }

            }
        }

        return httpRequest;
    }


    protected List<RequestComponent> getRequestComponents(String delimiter, String payload){
        List<String> componentElements = Arrays.asList(payload.split("Content-Disposition: form-data;"));

        Map<String, RequestComponent> requestComponentMap = new HashMap();
        Integer activeNameIndex = 0;
        for(String componentElement : componentElements) {

            if (componentElement.contains("name=\"")) {

                Integer beginNameIndex = payload.indexOf("name=\"", activeNameIndex);
                Integer endNameIndex = payload.indexOf("\"", beginNameIndex + "name=\"".length());
                String elementName = payload.substring(beginNameIndex + "name=\"".length(), endNameIndex);

                if(activeNameIndex == 0)activeNameIndex = endNameIndex;

                if (!requestComponentMap.containsKey(elementName)) {

                    RequestComponent requestComponent = new RequestComponent();
                    requestComponent.setName(elementName);

                    Integer fileIndex = payload.indexOf("filename=", endNameIndex);

                    if (fileIndex - endNameIndex > 3) {
                        Integer startValueIndex = payload.indexOf("\r\n", endNameIndex + 1);
                        Integer endValueIndex = payload.indexOf(delimiter, startValueIndex + 1);
                        String elementValue = payload.substring(startValueIndex + 1, endValueIndex);
                        if (elementValue.endsWith("\r\n--")) {
                            int lastbit = elementValue.indexOf("\r\n--");
                            elementValue = elementValue.substring(0, lastbit).trim();
                        }
                        requestComponent.setValue(elementValue);
                        requestComponent.setHasFiles(false);
                    } else {
                        FileComponent fileComponent = getFileComponent(activeNameIndex, delimiter, payload);
                        if(fileComponent != null) {
                            requestComponent.getFiles().add(fileComponent);
                            activeNameIndex = fileComponent.getActiveIndex();
                        }
                    }

                    requestComponentMap.put(elementName, requestComponent);

                } else {
                    RequestComponent requestComponent = requestComponentMap.get(elementName);
                    FileComponent fileComponent = getFileComponent(activeNameIndex, delimiter, payload);
                    if(fileComponent != null) {
                        requestComponent.getFiles().add(fileComponent);
                        activeNameIndex = fileComponent.getActiveIndex();
                    }
                }
            }
        }

        List<RequestComponent> requestComponents = new ArrayList<>();
        for(Map.Entry<String, RequestComponent> requestComponentEntry : requestComponentMap.entrySet()){
            RequestComponent requestComponent = requestComponentEntry.getValue();
            requestComponents.add(requestComponent);
        }

        return requestComponents;

    }


    protected FileComponent getFileComponent(Integer activeNameIndex, String delimeter, String payload) {
        FileComponent fileComponent = new FileComponent();

        Integer fileIdx = payload.indexOf("filename=", activeNameIndex);
        Integer startFile = payload.indexOf("\"", fileIdx + 1);
        Integer endFile = payload.indexOf("\"", startFile + 1);
        String fileName = payload.substring(startFile + 1, endFile);
        fileComponent.setFileName(fileName);

        Integer startContent = payload.indexOf("Content-Type", endFile + 1);
        Integer startType = payload.indexOf(":", startContent + 1);
        Integer endType = payload.indexOf("\r\n", startType + 1);
        String type = payload.substring(startType + 1, endType).trim();
        fileComponent.setContentType(type);

        //finicky, need to remove the final 2 bytes

        Integer activeBeginIndexEdit = payload.indexOf("\r\n", endType);
        Integer activeBeginIndex = activeBeginIndexEdit + "\r\n\r\n".length();
        Integer activeCloseIndex = payload.indexOf(delimeter, activeBeginIndexEdit);

        if (activeCloseIndex - activeBeginIndex > "\r\n\r\n".length()) {

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            for (int activeIndex = activeBeginIndex; activeIndex < activeCloseIndex; activeIndex++) {
                byte activeByte = bytes[activeIndex];
                byteArrayOutputStream.write(activeByte);
            }

            byte[] bytes = byteArrayOutputStream.toByteArray();
            fileComponent.setFileBytes(bytes);
            fileComponent.setActiveIndex(activeCloseIndex);

            return fileComponent;
        }

        return null;
    }
}