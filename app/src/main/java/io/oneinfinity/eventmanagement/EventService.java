package io.oneinfinity.eventmanagement;

/**
 * Created by ujjwal on 12/16/2017.
 */

import io.oneinfinity.eventmanagement.BuildConfig;
import io.oneinfinity.eventmanagement.JwtModel;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import android.util.Log;

public class EventService {


    EventService() {
    }

    public JSONArray execute() {
        String url = BuildConfig.EVENT_URL;
        InputStream inputStream = null;
        String result = "";
        JSONObject events = null;
        JSONArray eventArray = new JSONArray();
        try {

            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(url);
            String json = "";

            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("merchant", MerchantModel.merchantId);

            json = jsonObject.toString();

            StringEntity se = new StringEntity(json);

            httpPost.setEntity(se);


            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            httpPost.setHeader("Authorization", "Bearer " + JwtModel.jwtToken);

            HttpResponse httpResponse = httpclient.execute(httpPost);

            inputStream = httpResponse.getEntity().getContent();

            // 10. convert inputstream to string
            if(inputStream != null) {
                result = convertInputStreamToString(inputStream);
                eventArray = new JSONArray(result);
               // jwtToken = new JSONObject(result);
               // result = jwtToken.getString("token");
            }
            else {
                eventArray = new JSONArray();
            }

        } catch (Exception e) {
            Log.d("InputStream", e.getMessage());
        }


        return eventArray;

    };

    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

}
