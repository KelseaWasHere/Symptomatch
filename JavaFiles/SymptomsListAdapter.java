package com.example.symptomatch;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
public class SymptomsListAdapter extends RecyclerView.Adapter<SymptomsListViewHolder>
{
    Context context;
    List <String> items;

    public SymptomsListAdapter(Context context, List<String> items)
    {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public SymptomsListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return new SymptomsListViewHolder(LayoutInflater.from(context).inflate(R.layout.symptom_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SymptomsListViewHolder holder, int position)
    {
        holder.symptomName.setText(items.get(position));
    }

    @Override
    public int getItemCount()
    {
        return items.size();
    }
}
