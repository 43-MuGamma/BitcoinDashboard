package com.example.adam.bitcoindashboard;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends FragmentActivity {

    //Only ListBlockFragment is retained on orientation due to persisting state use of SharedPreferences
    ListBlockFragment BlockList= new ListBlockFragment(); //initialized wallet of addresses
    ChartFragment PriceChart = new ChartFragment(); //Price chart
    BlockDetailsFragment Block = new BlockDetailsFragment(); //for single block querying and navigation purposes
    PriceFragment Price = new PriceFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        final Button BlockBtn = (Button)findViewById(R.id.block_btn);
        Button PriceBtn = (Button)findViewById(R.id.price_btn);
        Button List = (Button)findViewById(R.id.list_blocks);
        Button Chart = (Button)findViewById(R.id.chart_btn);

        BlockBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                    if (findViewById(R.id.Land_Fragment_Container) == null) { //if in portrait mode
                        getFragmentManager()
                                .beginTransaction()
                                .replace(R.id.Fragment_Container, Block) //Call BlockDetailsFragment procedures for current block
                                .commit();
                    } else { //Landscape mode
                        getFragmentManager()
                                .beginTransaction()
                                .replace(R.id.Land_Fragment_Container, Block) //Call BlockDetailsFragment procedures for current block
                                .commit();
                    }
            }
        });

        PriceBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(findViewById(R.id.Land_Fragment_Container) == null) { //if in portrait mode
                    getFragmentManager()
                            .beginTransaction()
                            .replace(R.id.Fragment_Container, Price) //Call PriceFragment
                            .commit();
                } else{ //Landscape mode
                    getFragmentManager()
                            .beginTransaction()
                            .replace(R.id.Land_Fragment_Container, Price) //Call PriceFragment
                            .commit();
                }
            }
        });

        List.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(findViewById(R.id.Land_Fragment_Container) == null){ //if in portrait mode
                    getFragmentManager()
                            .beginTransaction()
                            .replace(R.id.Fragment_Container, BlockList) //Call ListBlockFragment
                            .commit();
                } else{ //Landscape mode
                    getFragmentManager()
                            .beginTransaction()
                            .replace(R.id.Land_Fragment_Container, BlockList) //Call ListBlockFragment
                            .commit();
                }
            }
        });

        Chart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(findViewById(R.id.Land_Fragment_Container) == null){ //if in portrait mode
                    getFragmentManager()
                            .beginTransaction()
                            .replace(R.id.Fragment_Container, PriceChart) //Call ListBlockFragment
                            .commit();
                } else{ //Landscape mode
                    getFragmentManager()
                            .beginTransaction()
                            .replace(R.id.Land_Fragment_Container, PriceChart) //Call ChartFragment
                            .commit();
                }
            }
        });
    }
}
