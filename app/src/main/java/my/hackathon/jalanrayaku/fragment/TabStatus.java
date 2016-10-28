package my.hackathon.jalanrayaku.fragment;


import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import my.hackathon.jalanrayaku.MainActivity;
import my.hackathon.jalanrayaku.R;
import my.hackathon.jalanrayaku.data.Dengue;
import my.hackathon.jalanrayaku.data.Weather;

/**
 * Created by MuhammadIqbal on 25/10/2016.
 */

public class TabStatus extends Fragment {

    private final static String TAG = "TABSTATUS";

    private EditText mETAddress;
    private TextView mTVProbability;
    private Button mBtnCari;

    private boolean bSearch = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_status, container, false);

        mETAddress = (EditText) view.findViewById(R.id.et_address);
        mTVProbability = (TextView) view.findViewById(R.id.et_probability);

        mETAddress.setImeActionLabel("Cari", KeyEvent.KEYCODE_ENTER);
        mETAddress.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                if(keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    getAddressCoordinate();
                    return true;
                }

                return false;
            }
        });

        mETAddress.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    getAddressCoordinate();
                    return true;
                }

                return false;
            }
        });

        mBtnCari = (Button) view.findViewById(R.id.btn_load);
        mBtnCari.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAddressCoordinate();
            }
        });
        return view;
    }

    /***
     *
     */
    private void getAddressCoordinate() {

        String address = mETAddress.getText().toString();
        if(!address.isEmpty()){
            new GetPointFromAddressTask().execute(address);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser) {

            if(bSearch) {
                getChancesOfOutbreak();
            }
        }
    }

    /***
     * ASYNC TASK
     */
    class GetPointFromAddressTask extends AsyncTask<String, Void, ArrayList<Address>> {

        @Override
        protected ArrayList<Address> doInBackground(String... params) {

            String search = params[0] + " malaysia";

            Geocoder coder = new Geocoder(getContext(), new Locale("mys"));
            List<Address> addresses = null;

            try {
                addresses = coder.getFromLocationName(search, 1);
                if(addresses != null) {
                    return (ArrayList<Address>) addresses;
                }
            } catch (Exception ex) {
                Log.d(TAG, ex.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Address> addresses) {
            super.onPostExecute(addresses);

            if(addresses != null) {
                Address address = addresses.get(0);
                Log.d(TAG, "Latitude: " + address.getLatitude() + ", Longitude: " + address.getLongitude());
                // set back to main activity
                ((MainActivity) getActivity()).setLocation(address.getLatitude(), address.getLongitude());
                bSearch = true;
            }
        }
    }

    /***
     * Check for severity of nearby outbreak.
     * The higher the severity, the higher the chances of outbreak happening.
     */
    private void getChancesOfOutbreak() {

       if(((MainActivity)getActivity()).getDengueNearbyHotspot().isEmpty()) {
           AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
           builder.setMessage("Tiada kes wabak denggi berdekatan dengan anda.");
           builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialogInterface, int i) {
                   dialogInterface.dismiss();
               }
           });

           builder.show();
       } else {
           double percent = (getProbabilityOnOutbreak() + getProbabilityOnWeather()) * 100;

           String s = String.format("%.2f", percent);
           mTVProbability.setText(s+"%");
       }
    }

    private double getProbabilityOnOutbreak() {

        double avgRate = 0;
        int index = 0;

        int totalCase = 0;
        int totalDuration = 0;

        for (Dengue dengue: ((MainActivity)getActivity()).getDengueNearbyHotspot()) {

            totalCase += dengue.getTotalCase();
            totalDuration += dengue.getOutbreakDuration();

            index++;
        }

        avgRate = (double) totalCase/totalDuration;

        Log.d(TAG, String.valueOf(avgRate));

        return avgRate * 0.7;

    }

    private double getProbabilityOnWeather() {

        int rain = 0;
        int index = 0;

        for(Weather weather: ((MainActivity)getActivity()).getWeatherHistory()) {
            if(!weather.getEvents().isEmpty()) {
                rain++;
            }

            index++;
        }

        double rate = (double) rain/index;
        Log.d(TAG, String.valueOf(rate));

        return rate * 0.3;
    }
}
