package io.oneinfinity.eventmanagement;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by ancha on 12/17/2017.
 */

public class ItemService {

    private final String jwToken;

    ItemService(String token) {

        jwToken = token;

    }

    public ItemModel[] execute(){
        if(ItemModel.getItems() != null) {
            return ItemModel.getItems();
        }
        String url = BuildConfig.ITEM_URL;
        InputStream inputStream = null;
        ItemModel[] items = null;
        String result = "";
        String eventId = "/" + EventModel.eventId;
        JSONArray itemArray;
        try {


            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            url = url + eventId;
            // 2. make POST request to the given URL
            HttpGet httpGet = new HttpGet(url);

            httpGet.setHeader("Accept", "application/json");
            httpGet.setHeader("Authorization", "Bearer " + jwToken);

            HttpResponse httpResponse = httpclient.execute(httpGet);

            inputStream = httpResponse.getEntity().getContent();

            // 10. convert inputstream to string
            if(inputStream != null) {
                result = convertInputStreamToString(inputStream);
                itemArray = new JSONArray(result);
                items = parseItems(itemArray);
                ItemModel.setItems(items);
            }
            else {
                result = "";
            }

        } catch (Exception e) {
            Log.w("InputStream", e.getLocalizedMessage());
        }

        return items;

    }

    private ItemModel[] parseItems(JSONArray array) {
        ItemModel[] arrayItems = new ItemModel[array.length()];

        for (int i = 0; i < array.length(); ++i) {
            JSONObject rec = null;
            try {
                rec = array.getJSONObject(i);
                ItemModel item = new ItemModel();
                item.setItemId(rec.getString("_id"));
                item.setItemName(rec.getString("itemName"));
                item.setMerchantId(rec.getString("merchant"));
                item.setItemPrice(rec.getLong("itemPrice"));
                item.setItemImage(rec.getString("itemImage"));
                item.setItemCount(rec.getInt("itemCount"));
                item.setCurrency(rec.getString("currency"));
                arrayItems[i] = item;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return arrayItems;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }
}
