package com.example.adam.bitcoindashboard;


import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class ListBlockFragment extends Fragment {

    ListView AddressList;

    ArrayList<String> Addresses; //list of addresses

    String Balance;

    TextView BalanceText;

    View view;

    public ListBlockFragment() {
        // Required empty public constructor
    }

    @Override
    public void onSaveInstanceState(Bundle outState) { //Save instance when fragment is swapped for another
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("SavedAddresses", Addresses);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState!=null) //load info on swapped in list fragment
            Addresses = savedInstanceState.getStringArrayList("SavedAddresses");
    }

    @Override
    public void onPause(){
        super.onPause();
        save(); //Save data persistently in default preference file location for addresses when swapped or partially obscured
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        load(); //Load data persistently in preference file location for addresses
        if(view == null) { //Check to see if a fragment view exists
            view = inflater.inflate(R.layout.fragment_list_block, container, false);

            AddressList = (ListView) view.findViewById(R.id.block_address_list);

            BalanceText = (TextView) view.findViewById(R.id.TotalAmountTxt);
            BalanceText.setFreezesText(true);
            Button Add = (Button) view.findViewById(R.id.AddBtn);
            final EditText EnterAddress = (EditText) view.findViewById(R.id.CreateAddressTxt);

            Add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Addresses.add(EnterAddress.getText().toString());
                }
            });

            AddressList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String SelectedAddress = AddressList.getItemAtPosition(position).toString();
                    BalanceChecker GetBalance = new BalanceChecker(SelectedAddress);
                    GetBalance.execute();
                }
            });

            AddressList.setSaveEnabled(true);

            AddressList.setAdapter(new CustomAdapter(this, android.R.layout.simple_spinner_dropdown_item, Addresses));
        }
        return view;
    }

    class BalanceChecker extends AsyncTask<Void, Void, Void>{ //AsynchTask used to get balance utilizing ListView
        String BlockAddress;

        public BalanceChecker(String BlockAddress){
             this.BlockAddress = BlockAddress;
        }

        @Override
        protected Void doInBackground(Void... params) { //Runnable functionality in background
            String balanceURL = "https://blockchain.info/q/addressbalance/" + BlockAddress; //Generate balance URL
            try {
                //HTML parsing line
                Document GetBalanceInfo = Jsoup.parse(IOUtils.toString(new URL(balanceURL), Charset.forName("UTF-8")));

                //Assigning balance from body of HTML text
                Balance = GetBalanceInfo.body().text();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) { //Update UI elements (Handler functionality)
            super.onPostExecute(aVoid);
            BalanceText.setText(Balance);
        }
    }

    //Save data persistently (generally for after closing app)
    void save(){
        SharedPreferences PersistentInfo = getActivity().getSharedPreferences("ListData", 0);
        SharedPreferences.Editor DataStorageHandler = PersistentInfo.edit();
        for(int ctr = 0; ctr < Addresses.size(); ctr++) //add each individual address to the preferences object
            DataStorageHandler.putString("Address"+ctr, Addresses.get(ctr));
        DataStorageHandler.commit();
    }

    //Load data persistently
    void load(){
        SharedPreferences PersistentInfo = getActivity().getSharedPreferences("ListData", 0);
        if(Addresses == null){ //Just created fragment on app launch, so no state data immediately available
            Addresses = new ArrayList<String>(); //initialize addresses
            if(PersistentInfo.contains("Address0")) {//If at least one address exists, we add it along with the rest
                for (int ctr = 0; ctr < 30;  ctr++) { //check for 30 possible entries
                    if(PersistentInfo.contains("Address"+ctr))
                        Addresses.add(PersistentInfo.getString("Address" + ctr, null));
                    else
                        break;
                }
                System.out.println("MANAGED TO RESTORE STATE "+Addresses.get(0).toString());
            }
        }
    }
}
