package com.example.android.usgsquakereportclient;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<EarthQuake> earthQuakes;
    EarthQuakeAdapter adapter;
    public static final String URL = "http://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&eventtype=earthquake&orderby=time&minmag=1&maxmag=8&limit=100";
    //public static final String URL = "http://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&eventtype=earthquake&limit=10";

    View loadingScreenView;
    View noInternetScreenView;
    View noDataView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.list);
        earthQuakes = new ArrayList<>();
        adapter = new EarthQuakeAdapter(this, earthQuakes);

        /*
        Initialising default Views
         */
        loadingScreenView = findViewById(R.id.loading_screen);
        noInternetScreenView = findViewById(R.id.no_internet_screen);
        noDataView = findViewById(R.id.no_data);

        /*
        Getting Connectivity service.
         */
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            /*
            This code will execute when there will be an internet connection available.
             */
            EarthQuakeAsyncTask earthQuakeAsyncTask = new EarthQuakeAsyncTask(new EarthQuakeAsyncResponse() {
                @Override
                public void processFinish(ArrayList<EarthQuake> earthQuakeList) {
                    earthQuakes.clear();
                    if (earthQuakeList.size() == 0) {
                        /*
                        This code will execute only when there is no data to display.
                         */
                        listView.setVisibility(View.GONE);
                        loadingScreenView.setVisibility(View.GONE);
                        noInternetScreenView.setVisibility(View.GONE);
                        noDataView.setVisibility(View.VISIBLE);
                    } else {
                        /*
                        This code will execute when there is some data to display.
                         */
                        earthQuakes.addAll(earthQuakeList);
                        loadingScreenView.setVisibility(View.GONE);
                        noDataView.setVisibility(View.GONE);
                        noInternetScreenView.setVisibility(View.GONE);
                        listView.setVisibility(View.VISIBLE);
                    }
                    listView.setAdapter(adapter);
                }
            });
            earthQuakeAsyncTask.execute(URL);
        } else {
            /*
            This code will work when there is no internet connectivity.
             */
            listView.setVisibility(View.GONE);
            loadingScreenView.setVisibility(View.GONE);
            noDataView.setVisibility(View.GONE);
            noInternetScreenView.setVisibility(View.VISIBLE);
        }


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                EarthQuake earthQuakeCurrent = earthQuakes.get(i);
                startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(earthQuakeCurrent.getUrl())));
            }
        });
    }
}
