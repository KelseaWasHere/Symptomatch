package com.example.symptomatch;

import android.content.Intent;
import android.media.Image;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SuggestedConditionsViewholder extends RecyclerView.ViewHolder
{
    Button conditionButton;

    public SuggestedConditionsViewholder(@NonNull View itemView)
    {
        super(itemView);
        conditionButton = itemView.findViewById(R.id.sugConditionButtonTemplate);
    }

}
