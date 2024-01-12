package com.example.symptomatch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class ViewConditionsActivity extends AppCompatActivity {

    DatabaseHelper db;
    List<String> condition_list;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_conditions);
        db = new DatabaseHelper(this);
        condition_list = new ArrayList<String>();
        condition_list = db.getConditionNames();

        recyclerView = findViewById(R.id.condition_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new MyRecyclerViewAdapter(this, condition_list));
    }

    @Override
    protected void onResume() {
        super.onResume();
        condition_list = db.getConditionNames();
        recyclerView.setAdapter(new MyRecyclerViewAdapter(this, condition_list));
    }

    public void ConditionButtonClicked(View view)
    {
        Button clickedButton = (Button) view; // since the view is the specific button that was clicked, this dynamically assigns the Button based on which button you clicked
        String clickedConditionName = clickedButton.getText().toString(); // get the name of the symptom in the clicked button
        Intent intent = new Intent(ViewConditionsActivity.this, ConditionInfoActivity.class);
        intent.putExtra("name", clickedConditionName);
        startActivity(intent);
    }

}