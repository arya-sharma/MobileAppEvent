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

public class CheckoutService {

    private final String secret;
    private final float purchaseAmount;
    private final ArrayList<LineItems> lineItems;
    private HttpPost httpPost;

    CheckoutService(String secret, float amount, ArrayList<LineItems> items) {
        this.secret = secret;
        this.purchaseAmount = amount;
        this.lineItems = items;
    }

    public String execute() {
        String url = BuildConfig.CHECKOUT_URL;
        InputStream inputStream = null;
        String result = "";
        JSONObject response = null;
        try {

            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            httpPost = new HttpPost(url);

            String json = "";


            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("secret", this.secret);
            jsonObject.accumulate("purchaseAmount", this.purchaseAmount);
            jsonObject.accumulate("eventId", EventModel.eventId);
            JSONArray itemArray = new JSONArray();
            for(LineItems item: this.lineItems) {
                JSONObject line = new JSONObject();
                line.accumulate("itemId", item.getItemId());
                line.accumulate("itemCount", item.getItemCount());
                line.accumulate("itemPrice", item.getItemPrice());
                line.accumulate("itemName", item.getItemName());
                itemArray.put(line);
            }
            Log.d("Items length", String.valueOf(this.lineItems.size()));
            Log.d("Items", itemArray.toString());
            jsonObject.accumulate("items", itemArray);

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
                Log.w("Order Response", response.toString());
               // result = jwtToken.getString("token");
                if(response.has("data")) {
                    result = "success";
                }
                else{
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
