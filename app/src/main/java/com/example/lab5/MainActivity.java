package com.example.lab5;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RequestQueue mQueue;

    ListView listView;


    ArrayAdapter<String> arrayAdapter;
    private List<String> currencyList;

    private Handler handler;
    private Runnable updateRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listview);
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        listView.setAdapter(arrayAdapter);

        currencyList = new ArrayList<>();

        loadAndParseCurrencies();

        handler = new Handler();
        updateRunnable = new Runnable() {
            @Override
            public void run() {
                loadAndParseCurrencies();
                handler.postDelayed(this, 5000);
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(updateRunnable, 5000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(updateRunnable);
    }

    private void loadAndParseCurrencies() {
        DataLoader.loadCurrencies(this, new DataLoader.CurrencyLoadListener() {
            @Override
            public void onCurrencyLoadSuccess(JSONObject data) {
                try {
                    currencyList.clear();
                    arrayAdapter.clear();

                    Iterator<String> keys = data.keys();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        JSONObject currency = data.getJSONObject(key);
                        String currencyName = currency.getString("code");
                        double currencyValue = currency.getDouble("value");
                        String currencyInfo = "Name: " + currencyName + " | Price (EUR/"+ currencyName + "): " + currencyValue;

                        currencyList.add(currencyInfo);
                    }

                    arrayAdapter.addAll(currencyList);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCurrencyLoadError(Exception e) {
                Log.e("CurrencyLoader", "Error loading currencies", e);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        MenuItem menuItem = menu.findItem(R.id.action_search);

        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Type any currency you want");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                arrayAdapter.getFilter().filter(newText);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }
}