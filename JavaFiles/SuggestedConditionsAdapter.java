package com.example.symptomatch;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
public class SuggestedConditionsAdapter extends RecyclerView.Adapter<SuggestedConditionsViewholder>
{
    Context context;
    List <String> items;

    public SuggestedConditionsAdapter(Context context, List<String> items)
    {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public SuggestedConditionsViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return new SuggestedConditionsViewholder(LayoutInflater.from(context).inflate(R.layout.conditions_relative_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SuggestedConditionsViewholder holder, int position)
    {
        holder.conditionButton.setText(items.get(position));
    }

    @Override
    public int getItemCount()
    {
        return items.size();
    }
}
