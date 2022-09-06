package io.shape.service;

import com.google.gson.Gson;
import io.kakai.annotate.Property;
import io.kakai.annotate.Service;
import io.shape.model.SmsMessage;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

@Service
public class SmsService {

    static final String URI = "https://textbelt.com/text";

    Gson gson = new Gson();

    @Property("key")
    String key;

    public boolean send(String phone, String message) {

        try {
            SmsMessage sms = new SmsMessage();
            sms.setKey(key);
            sms.setPhone(phone);
            sms.setMessage(message);

            String json = gson.toJson(sms);

            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(SmsService.URI);

            StringEntity entity = new StringEntity(json);
            httpPost.setEntity(entity);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            client.execute(httpPost);
            client.close();

        } catch (UnsupportedEncodingException uex) {
            uex.printStackTrace();
        } catch (ClientProtocolException ex) {
            ex.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

}