package com.example.adam.bitcoindashboard;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.commons.io.IOUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.Charset;

public class BlockDetailsFragment extends Fragment {

    int BlockHeight; //Start at arbitrary starting height picked by user
    String StringToInt; //Used to fetch block height query initially as a string
    String blockURL;

    static JSONObject JSONBlockInfo; //JSONObject for containing general block info

    String block_hash = ""; //current block hash
    String block_height = ""; //current block height
    String block_prev_block = ""; //prev block hash
    String block_bits = ""; //current block amount of bits
    String block_number = ""; //current block index

    TextView BlockHashView;
    TextView BlockHeightView;
    TextView BlockPrevBlockView;
    TextView BlockBitsView;
    TextView BlockNumberView;

    View view;

    Boolean URLcheck; //Check to see if next block exists

    public BlockDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onSaveInstanceState(Bundle outstate){
        outstate.putString("GetHash", block_hash);
        outstate.putString("GetHeight", block_height);
        outstate.putString("GetPrev", block_prev_block);
        outstate.putString("GetBits", block_bits);
        outstate.putString("GetNumber", block_number);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState != null){ //load block info on swapped in fragment
            block_hash = savedInstanceState.getString("GetHash");
            block_height = savedInstanceState.getString("GetHeight");
            block_prev_block = savedInstanceState.getString("GetPrev");
            block_bits = savedInstanceState.getString("GetBits");
            block_number = savedInstanceState.getString("GetNumber");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        if(view == null) {
            view = inflater.inflate(R.layout.fragment_block_details, container, false);

            BlockHashView = (TextView) view.findViewById(R.id.block_hash_view);
            BlockHashView.setFreezesText(true); //used to save view text upon fragment swap
            BlockHeightView = (TextView) view.findViewById(R.id.block_height_view);
            BlockHeightView.setFreezesText(true);
            BlockPrevBlockView = (TextView) view.findViewById(R.id.block_prev_block_view);
            BlockPrevBlockView.setFreezesText(true);
            BlockBitsView = (TextView) view.findViewById(R.id.block_bits_view);
            BlockBitsView.setFreezesText(true);
            BlockNumberView = (TextView) view.findViewById(R.id.block_number_view);
            BlockNumberView.setFreezesText(true);

            final EditText QueryBlock = (EditText) view.findViewById(R.id.BlockQueryView);
            QueryBlock.setFreezesText(true);
            Button Go = (Button) view.findViewById(R.id.go_block);
            Button Previous = (Button) view.findViewById(R.id.prev_btn);
            Button Next = (Button) view.findViewById(R.id.next_btn);

            Go.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    StringToInt = QueryBlock.getText().toString();
                    BlockHeight = Integer.parseInt(StringToInt);
                    blockURL = "https://blockchain.info/block-height/" + BlockHeight + "?format=json"; //Get the URL encoded JSON for the height
                    BlockDataAggregator(); //Parse and display data
                }
            });

            Previous.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!block_prev_block.equals(null)) { //Check for previous block if it exists
                        BlockHeight--; //decrement the height to go to the previous block in the chain
                        blockURL = "https://blockchain.info/block-height/" + BlockHeight + "?format=json"; //Get the URL encoded JSON for the height
                        BlockDataAggregator(); //Parse and display data
                    }
                }
            });

            Next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    URLcheck = URLUtil.isValidUrl("https://blockchain.info/block-height/" + BlockHeight+1 + "?format=json");
                    if(URLcheck != false) { //check next block for info
                        BlockHeight++; //decrement the height to go to the previous block in the chain
                        blockURL = "https://blockchain.info/block-height/" + BlockHeight + "?format=json"; //Get the URL encoded JSON for the height
                        BlockDataAggregator(); //Parse and display data
                    }
                }
            });
        }
        // Inflate the layout for this fragment
        return view;
    }

    void BlockDataAggregator(){
        //Parse the JSON and print info using a worker thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONBlockInfo = new JSONObject(IOUtils.toString(new URL(blockURL), Charset.forName("UTF-8")));
                    AssignJSONData.sendEmptyMessage(0);
                    WidgetBlockInfo.sendEmptyMessage(0);
                }
                catch (JSONException e){
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //Attach necessary block info to the view using a handler
    Handler AssignJSONData = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            try {
                JSONBlockInfo = JSONBlockInfo.getJSONArray("blocks").getJSONObject(0);
                block_hash = JSONBlockInfo.get("hash").toString();
                block_height = JSONBlockInfo.get("height").toString();
                block_prev_block = JSONBlockInfo.get("prev_block").toString();
                block_bits = JSONBlockInfo.get("bits").toString();
                block_number = JSONBlockInfo.get("block_index").toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return false;
        }
    });

    //Show block info in widgets
    Handler WidgetBlockInfo = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            BlockHashView.setText(block_hash);
            BlockHeightView.setText(block_height);
            BlockPrevBlockView.setText(block_prev_block);
            BlockBitsView.setText(block_bits);
            BlockNumberView.setText(block_number);

            System.out.println("HASH: "+block_hash);
            System.out.println("HEIGHT: "+ block_height);
            System.out.println("PREV HASH: "+block_prev_block);
            System.out.println("BITS: "+block_bits);
            System.out.println("NUMBER: "+block_number);
            Log.e("BlockDetailsFragment", "HASH: "+block_hash);
            Log.e("BlockDetailsFragment", "HEIGHT: "+ block_height);
            Log.e("BlockDetailsFragment", "PREV HASH: "+block_prev_block);
            Log.e("BlockDetailsFragment", "BITS: "+block_bits);
            Log.e("BlockDetailsFragment", "NUMBER: "+block_number);
            return false;
        }
    });
}