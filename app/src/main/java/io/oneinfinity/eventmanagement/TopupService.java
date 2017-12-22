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
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import android.util.Log;

public class TopupService {

    private final String secret;
    private final float topAmount;
    private HttpPost httpPost;

    TopupService(String secret, float amount) {
        this.secret = secret;
        this.topAmount = amount;
    }

    public String execute() {
        String url = BuildConfig.TOPUP_URL;
        InputStream inputStream = null;
        String result = "";
        JSONObject response = null;
        try {
            Log.w("Topup service", "executing");
            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            httpPost = new HttpPost(url);

            String json = "";


            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("secret", this.secret);
            jsonObject.accumulate("topUpAmount", this.topAmount);
            jsonObject.accumulate("eventId", EventModel.eventId);

            json = jsonObject.toString();

            StringEntity se = new StringEntity(json);

            httpPost.setEntity(se);

            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            httpPost.setHeader("Authorization", "Bearer " + JwtModel.jwtToken);

            HttpResponse httpResponse = httpclient.execute(httpPost);

            inputStream = httpResponse.getEntity().getContent();
            Log.d("Res Status", httpResponse.getStatusLine().toString());

            Log.d("GOT RESPONSE", inputStream.toString());
            // 10. convert inputstream to string
            if(inputStream != null) {
                result = convertInputStreamToString(inputStream);
                response = new JSONObject(result);
                Log.w("Topup Response", response.toString());
                // result = jwtToken.getString("token");
                if(response.has("msg")) {
                    result = "success";
                }
                else {
                    result = response.getString("message");
                }
            }
            else {
                result = "";
            }

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }


        return result;

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
