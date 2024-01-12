package com.example.symptomatch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyViewHolder>{

    Context context;
    List<String> savedConditions;



    public MyRecyclerViewAdapter(Context context, List<String> savedConditions) {
        this.context = context;
        this.savedConditions = savedConditions;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.recyclerview_row,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.condition_button.setText(savedConditions.get(position));
    }

    @Override
    public int getItemCount() {
        return savedConditions.size();
    }
}
