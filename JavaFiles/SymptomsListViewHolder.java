package com.example.symptomatch;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SymptomsListViewHolder extends RecyclerView.ViewHolder
{
    TextView symptomName;

    public SymptomsListViewHolder(@NonNull View itemView)
    {
        super(itemView);
        symptomName = itemView.findViewById(R.id.symptom_list_TextView);
    }
}
