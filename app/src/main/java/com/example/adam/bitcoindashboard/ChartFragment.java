package com.example.adam.bitcoindashboard;


import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;

import java.util.Timer;
import java.util.TimerTask;


public class ChartFragment extends Fragment {

    WebView Chart;
    EditText ChartQuery;
    Button QueryBtn;
    String query;
    String ChartURL;

    public ChartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chart, container, false);

        //assign view and widget id values
        Chart = (WebView)view.findViewById(R.id.chart_view);
        ChartQuery = (EditText)view.findViewById(R.id.Chart_Query_View);
        QueryBtn = (Button)view.findViewById(R.id.Query_Button);

        //Thread for setting and refreshing chart info
        new Thread(new Runnable() {
            @Override
            public void run() {
                SetChartInfo.sendEmptyMessage(0);
                ReloadWebView refresh = new ReloadWebView(getActivity(), 10, Chart); //refresh chart
            }
        }).start();

        return view;
    }

    Handler SetChartInfo = new Handler(new Callback(){
        @Override
        public boolean handleMessage(Message msg) {
            QueryBtn.setOnClickListener(new View.OnClickListener() {//when user submits query do the following...
                public void onClick(View v) {
                    query = ChartQuery.getText().toString(); //fetch user input for chart
                    ChartURL = "https://chart.yahoo.com/z?s=BTCUSD=X&t="+query; //set user input to end of URL string
                    Chart.loadUrl(ChartURL);
                }
            });
            return false;
        }
    });

    //generic class for handling reloading a webview
    class ReloadWebView extends TimerTask {
        Activity context;
        Timer timer;
        WebView wv;

        public ReloadWebView(Activity context, int seconds, WebView wv) {
            this.context = context;
            this.wv = wv;

            timer = new Timer();
        /* execute the first task after seconds */
            timer.schedule(this,
                    seconds * 1000,  // initial delay
                    seconds * 1000); // subsequent rate

        /* if you want to execute the first task immediatly */
        /*
        timer.schedule(this,
                0,               // initial delay null
                seconds * 1000); // subsequent rate
        */
        }

        @Override
        public void run() {
            if(context == null || context.isFinishing()) {
                // Activity killed
                this.cancel();
                return;
            }

            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("REFRESHED CHART SUCCESSFULLY");
                    wv.reload();
                }
            });
        }
    }
}
