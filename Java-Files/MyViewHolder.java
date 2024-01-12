package com.example.symptomatch;

import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyViewHolder extends RecyclerView.ViewHolder{

    Button condition_button;
    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        condition_button = itemView.findViewById(R.id.conditionButton);

    }
}