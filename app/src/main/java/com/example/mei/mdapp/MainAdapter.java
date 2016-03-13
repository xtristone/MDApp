package com.example.mei.mdapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * Created by mei on 2016/2/1.
 */
public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainHolder> {

    Context context;

    public MainAdapter(Context context)
    {
        this.context=context;
    }

    @Override
    public MainHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView= LayoutInflater.from(context).inflate(R.layout.item_main, parent, false);
        return new MainHolder(convertView);
    }

    @Override
    public void onBindViewHolder(MainHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 10;
    }

    class MainHolder extends RecyclerView.ViewHolder
    {

        public MainHolder(View itemView) {
            super(itemView);


        }
    }

}
