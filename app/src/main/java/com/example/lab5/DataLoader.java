package com.example.lab5;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class DataLoader {

    private static final String BASE_URL = "https://api.currencyapi.com/v3/latest?apikey=cur_live_XImWZGdBC72IuL6U5BbMn9fQ1gNeBXr7PCox5MMu";

    public static void loadCurrencies(Context context, final CurrencyLoadListener listener) {
        RequestQueue queue = Volley.newRequestQueue(context);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, BASE_URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject data = response.getJSONObject("data");
                            listener.onCurrencyLoadSuccess(data);
                        } catch (JSONException e) {
                            listener.onCurrencyLoadError(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.onCurrencyLoadError(error);
                    }
                });

        queue.add(request);
    }

    public interface CurrencyLoadListener {
        void onCurrencyLoadSuccess(JSONObject data);
        void onCurrencyLoadError(Exception e);
    }
}