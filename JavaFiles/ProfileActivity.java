package com.example.symptomatch;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {
    private SharedPreferences profileSharedPrefs;
    private EditText ageEditText;
    private Spinner sexSpinner;
    private int userAge;
    private String userSex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileSharedPrefs = getSharedPreferences("SharedPrefs", MODE_PRIVATE);
        userAge = profileSharedPrefs.getInt("age", -1);
        userSex = profileSharedPrefs.getString("sex", "Undefined");

        ageEditText = (EditText) findViewById(R.id.profileAgeEditText);
        sexSpinner = (Spinner) findViewById(R.id.profileSexSpinner);

        if(userAge > 0)
            ageEditText.setText(Integer.toString(userAge));
        else
            userAge = -1;

        if(userSex.equals("Male"))
            sexSpinner.setSelection(1);
        else if (userSex.equals("Female"))
            sexSpinner.setSelection(2);
        else if (userSex.equals("Other"))
            sexSpinner.setSelection(3);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        String strUserAge = ageEditText.getEditableText().toString().trim();
        userSex = sexSpinner.getSelectedItem().toString();

        // Verify the user entered a valid age
        try {
            userAge = Integer.parseInt(strUserAge);
        } catch (NumberFormatException e) {
            userAge = -1;
        }

        SharedPreferences.Editor profileEdit = profileSharedPrefs.edit();
        if(userAge != -1 && userAge <= 100) {
            profileEdit.putInt("age", userAge);
        }
        if(!userSex.equals("Select Sex")) {
            profileEdit.putString("sex", userSex);
        }
        profileEdit.apply();
    }
}