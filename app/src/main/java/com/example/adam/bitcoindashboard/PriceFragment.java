package com.example.adam.bitcoindashboard;

import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;

public class PriceFragment extends Fragment {

    double USDPrice; //int to store value price from JSONObject
    TextView PriceTxt; //TextView to set value price
    static JSONObject JSONPriceInfo; //JSONObject for parsing value price

    public PriceFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_price, container, false);

        PriceTxt = (TextView)view.findViewById(R.id.price_txt);
        final String PriceURL = "https://blockchain.info/ticker"; //URL to get price value for bitcoins

        //Worker thread for handling parsing price URL and showing price
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONPriceInfo = new JSONObject(IOUtils.toString(new URL(PriceURL), Charset.forName("UTF-8")));
                    ShowPrice.sendEmptyMessage(0);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        return view;
    }

    //Handler for showing price via main UI thread
    Handler ShowPrice = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            try {
                USDPrice = JSONPriceInfo.getJSONObject("USD").getDouble("last");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            PriceTxt.setText(String.valueOf(USDPrice)); //Set display for price
            return false;
        }
    });

}
