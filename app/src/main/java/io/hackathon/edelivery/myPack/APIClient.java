package io.hackathon.edelivery.myPack;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.squareup.okhttp.*;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import io.hackathon.edelivery.DeliveryService.Order;
import io.hackathon.edelivery.ViewOrders;

/**
 * Created by study on 0004 7/4/2015.
 */
public class APIClient {
    public static String url = "http://edelivery.cf/v1";

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient();


    String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }


    Call get(String url, Callback callback) throws  IOException {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    int put(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .put(body)
                .build();
        Response response = client.newCall(request).execute();
        return response.code();
    }

    public Order getOrder(final String ordCode,final ViewOrders.OrderListener listener) throws  IOException, JSONException {
        String serUrl = url + "/orders/"+ordCode;
        this.get(serUrl, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.isSuccessful()) {
                    String get = response.body().string();
                    try {
                        JSONObject getJson = new JSONObject(get);
                        JSONObject getLoc = getJson.getJSONObject("loc");
                        LatLng myLoc = new LatLng(getLoc.getDouble("lat"), getLoc.getDouble("long"));
                        Log.d("a", "a" + myLoc.latitude + " " + myLoc.longitude);
                        listener.onSuccess(new Order(ordCode, myLoc, getJson.getInt("status")));
                    }
                    catch (JSONException e) {

                    }

                    //Log.d("clgt", responseStr);
                    // Do what you want to do with the response.
                } else {
                    // Request not successful
                }
            }
        });
//        JSONObject get = new JSONObject(resp);
//        JSONObject loc = get.getJSONObject("loc");
//        double latitude, longitude;
//        latitude = loc.getDouble("lat");
//        longitude = loc.getDouble("long");
//        LatLng location = new LatLng(latitude, longitude);
//        result = new Order(ordCode, location, get.getInt("status"), get.getString("deliver_phone_number"));
//
//        return result;
        return null;
    }



    public void updateOrder(Order data)  throws  IOException, JSONException  {
        String json = this.createOrderJSON(data);
        this.put(url+"/orders/"+data.getCode(), json);
    }

    protected String createOrderJSON(Order data) {
        return "{'loc': { 'long': "+data.getLocation().longitude+", " +
                "'lat': "+data.getLocation().latitude+"} , 'status': "+data.getStatus()+"," +
                " }";
    }

    /* *


    */

}
