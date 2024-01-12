package com.example.symptomatch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.PopupWindow;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private PopupWindow popupWindow;
    private ConstraintLayout layout;
    private SharedPreferences sharedPrefs;
    private Boolean firstAppOpen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layout = findViewById(R.id.main_constraint_layout);
    }

    @Override
    protected void onStart() {
        super.onStart();
        sharedPrefs = getSharedPreferences("SharedPrefs", MODE_PRIVATE);
        firstAppOpen = sharedPrefs.getBoolean("firstAppOpen", true);
        if (firstAppOpen) {
            createPopupWindow();
            SharedPreferences.Editor edit = sharedPrefs.edit();
            edit.putBoolean("firstAppOpen", false);
            edit.apply();
        }
    }

    public void AddSymptomsButtonClicked(View view)
    {
        Intent intent = new Intent(MainActivity.this, AddSymptomsActivity.class);
        startActivity(intent);
    }

    public void SavedConditionsButtonClicked(View view)
    {
        Intent intent = new Intent(MainActivity.this, ViewConditionsActivity.class);
        startActivity(intent);
    }

    public void ProfileButtonClicked(View view)
    {
        Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
        startActivity(intent);
    }

    public void InfoButtonClicked(View view)
    {
        createPopupWindow();
    }

    private void createPopupWindow()
    {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popUpView = inflater.inflate(R.layout.info_popup, null);
        popupWindow = new PopupWindow(popUpView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);

        layout.post(new Runnable()
        {
            @Override
            public void run()
            {
                popupWindow.showAtLocation(layout, Gravity.TOP, 0, 0);
            }
        });
    }
    public void ExitPopup(View view)
    {
        popupWindow.dismiss();
    }

}
