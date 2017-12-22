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
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import android.util.Log;

public class LoginService {

    private final String email;
    private final String password;

    LoginService(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String execute() {
        String url = BuildConfig.LOGIN_URL;
        InputStream inputStream = null;
        String result = "";
        String response;
        JSONObject jwtToken = null;
        try {

            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(url);

            String json = "";


            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("email", this.email);
            jsonObject.accumulate("password", this.password);

            json = jsonObject.toString();

            StringEntity se = new StringEntity(json);

            httpPost.setEntity(se);

            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            HttpResponse httpResponse = httpclient.execute(httpPost);

            inputStream = httpResponse.getEntity().getContent();

            // 10. convert inputstream to string
            if(inputStream != null) {
                response = convertInputStreamToString(inputStream);
                jwtToken = new JSONObject(response);
                Log.w("Ouput", jwtToken.toString());
                if(jwtToken.has("token")) {
                    String token = jwtToken.getString("token");
                    new JwtModel(token, this.password);
                    new MerchantModel(jwtToken.getString("id"));
                    result = "success";
                }
                else {
                    result = jwtToken.getString("message");
                }

            }
            else {
                result = "Auth failed";
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
