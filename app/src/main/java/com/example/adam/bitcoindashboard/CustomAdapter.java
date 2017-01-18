package com.example.adam.bitcoindashboard;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter<String> {
    ArrayList<String> Items;

    public CustomAdapter(ListBlockFragment context, int resource, ArrayList<String> Items) {
        super(context.getContext(),resource);
        this.Items = Items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        return view;
    }

    @Override
    public int getCount() {
        if(Items != null)
            return Items.size();
        else
            return 0; //no addresses in the list
    }
    public void setItems (ArrayList<String> Items){ //Set to existing contained items for data persistence
        this.Items = Items;
    }

    public String getItem(int position) {
        return Items.get(position);
    }
}
