package my.hackathon.jalanrayaku;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.util.ArrayList;

import my.hackathon.jalanrayaku.adapter.MainPageAdapter;
import my.hackathon.jalanrayaku.data.Dengue;
import my.hackathon.jalanrayaku.data.Weather;
import my.hackathon.jalanrayaku.util.volley.VolleyRequestQueue;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "MAINACTIVITY";

    private final static int TAB_COUNT = 3;

    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private ArrayList<Dengue> mDengueList;
    private ArrayList<Weather> mWeathersHistory;

    private LatLng mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mTabLayout = (TabLayout) findViewById(R.id.tl_tab);
        mViewPager = (ViewPager) findViewById(R.id.vp_page);

        setupTab();

        getWeatherData();
    }

    private void setupTab() {
        mTabLayout.addTab(mTabLayout.newTab().setText("Status"));
        mTabLayout.addTab(mTabLayout.newTab().setText("Map"));
        mTabLayout.addTab(mTabLayout.newTab().setText("Activity"));
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        MainPageAdapter adapter = new MainPageAdapter(getSupportFragmentManager(), TAB_COUNT);
        mViewPager.setAdapter(adapter);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    /***
     * Get 2-week weather data
     */
    private void getWeatherData() {

        // get data
        String uri = "http://x3.xfero.xyz:8082/api/weather";

        Log.d(TAG, uri);

        // VERIFY USER
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                uri,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (!response.isEmpty()) {

                            Gson gson = new Gson();
                            Weather[] weathers = gson.fromJson(response, Weather[].class);

                            // reset
                            mWeathersHistory = new ArrayList<>();

                            for(int index = 0; index < weathers.length; index++) {
                                Weather weather = weathers[index];
                                // add to array
                                mWeathersHistory.add(weather);
                            }

                            /*// map data
                            if(mWeathersHistory.size() > 0) {
                                setOutbreakToMap();
//                                getAverage();
                            } else {

                            }*/
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG,error.toString());
                    }
                }
        );

        VolleyRequestQueue.getInstance(getApplicationContext()).addToRequestQueue(stringRequest, TAG);
    }

    /***
     * Get direction from Google server
     */
    private void getList() {

        // get data
        String uri = "http://x3.xfero.xyz:8082/api/dengue?";
        uri += "lat=" + mLocation.latitude;
        uri += "&lng=" + mLocation.longitude;
        uri += "&dist=" + 3000;

        Log.d(TAG, uri);

        // VERIFY USER
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                uri,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (!response.isEmpty()) {

                            Gson gson = new Gson();
                            Dengue[] dengues = gson.fromJson(response, Dengue[].class);

                            // reset
                            mDengueList = new ArrayList<>();

                            for(int index = 0; index < dengues.length; index++) {
                                Dengue dengue = dengues[index];
                                // add to array
                                mDengueList.add(dengue);
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG,error.toString());
                    }
                }
        );

        VolleyRequestQueue.getInstance(getApplicationContext()).addToRequestQueue(stringRequest, TAG);
    }

    /***
     * Get Weather 2-week history
     * @return
     */
    public ArrayList<Weather> getWeatherHistory() {
        return mWeathersHistory;
    }

    /***
     * Get nearby hotspot history
     * @return
     */
    public ArrayList<Dengue> getDengueNearbyHotspot() {
        return mDengueList;
    }

    public void setLocation(double latitude, double longitude) {
        mLocation = new LatLng(latitude, longitude);
        // update list
        getList();
    }

    public LatLng getLocation() {
        return mLocation;
    }
}
