package com.example.symptomatch;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class AddSymptomsActivity extends AppCompatActivity {
    private Context thisActivityContext;
    private ArrayList<String> symptomsList = new ArrayList<>(0); // list of user's added symptoms
    private ArrayList<String> conditionsList = new ArrayList<>(0); // list of suggested conditions
    private DatabaseHelper db;
    private EditText addSymptomEditText;
    private List<String> items = new ArrayList<String>();
    private RecyclerView recyclerView;
    private ProgressBar spinner;
    private ConstraintLayout layout;
    private PopupWindow popupWindow;
    private SharedPreferences sharedPrefs;
    private int userAge;
    private String userSex;
    private HttpURLConnection connection;
    private Handler handler;
    private Runnable conditionsRestartRunnable;
    private Runnable infoRestartRunnable;
    private String clickedConditionName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_symptoms);
        db = new DatabaseHelper(this);
		
        thisActivityContext = this;

        sharedPrefs = getSharedPreferences("SharedPrefs", MODE_PRIVATE);
        userAge = sharedPrefs.getInt("age", -1);
        userSex = sharedPrefs.getString("sex", "Undefined");

        layout = findViewById(R.id.add_symptoms_constraint_layout);

        recyclerView = findViewById(R.id.recyclerviewAddCondition);
        recyclerView.setVisibility(View.VISIBLE);

        spinner = findViewById(R.id.spinner);
        spinner.setVisibility(View.GONE);

        addSymptomEditText = findViewById(R.id.addSymptomEditText);

        handler = new Handler();
        conditionsRestartRunnable = new Runnable() {
            @Override
            public void run() {
                Log.i("WebService", "ChatGPT timeout, disconnecting");
                connection.disconnect();
                new ChatGPTConditions().execute(symptomsList);
                handler.postDelayed(conditionsRestartRunnable, 20000);
                Toast.makeText(thisActivityContext, "ChatGPT Timeout. Restarting.", Toast.LENGTH_LONG).show();
                Log.i("WebService", "ChatGPT restarting");
            }
        };
        infoRestartRunnable = new Runnable() {
            @Override
            public void run() {
                Log.i("WebService", "ChatGPT timeout, disconnecting");
                connection.disconnect();
                new ChatGPTConditionInfo().execute(clickedConditionName);
                handler.postDelayed(infoRestartRunnable, 60000);
                Toast.makeText(thisActivityContext, "ChatGPT Timeout. Restarting.", Toast.LENGTH_LONG).show();
                Log.i("WebService", "ChatGPT restarting");
            }
        };
		
        recyclerView.setLayoutManager(new LinearLayoutManager((this)));
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        recyclerView.setVisibility(View.VISIBLE);
        spinner.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
        if (connection != null)
            connection.disconnect();
    }

    public void ViewSymptomsButtonClicked(View view) {
        createPopupWindow();
    }
    private void createPopupWindow()
    {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popUpView = inflater.inflate(R.layout.symptom_list, null);
        popupWindow = new PopupWindow(popUpView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        layout.post(new Runnable()
        {
            @Override
            public void run()
            {
                popupWindow.showAtLocation(layout, Gravity.TOP, 0, 0);
            }
        });

        RecyclerView symptomListRecyclerView = popUpView.findViewById(R.id.symptomList);
        symptomListRecyclerView.setLayoutManager(new LinearLayoutManager((this)));
        symptomListRecyclerView.setAdapter(new SymptomsListAdapter(this, symptomsList));
    }
    public void ExitPopup(View view)
    {
        popupWindow.dismiss();
    }

    public void clearSymptomsButtonClicked(View view) {
        symptomsList.clear();
        items.clear();
        recyclerView.setAdapter(new SuggestedConditionsAdapter(this, items));
        if (connection != null)
            connection.disconnect();
        spinner.setVisibility(View.GONE);
    }

    public void AddSymptomButtonClicked(View view) {
        // Don't allow button to work if LLM is running
        if(spinner.getVisibility() == View.VISIBLE) {
            return;
        }

        // Get the trimmed symptom input from the EditText
        String symptom = addSymptomEditText.getText().toString().trim();

        // Don't add the symptom if there is none
        if(symptom.length() == 0) {
            Toast.makeText(this, "Enter a symptom first", Toast.LENGTH_SHORT).show();
            return;
        }

        // Format the string so that symptom is trimmed and capitalized
        //String firstLetter = "" + symptom.charAt(0);

        if(symptom.length() != 0) {
            symptom = symptom.replaceFirst("" + symptom.charAt(0), ("" + symptom.charAt(0)).toUpperCase());
            for (int j = 0; j < symptom.length() - 1; j++) {
                if(("" + symptom.charAt(j)).equals(" ")) {
                    symptom = symptom.replaceAll(" [a-z]", (" " + symptom.charAt(j + 1)).toUpperCase());
                }
            }
        }

        //symptom = symptom.replaceFirst(firstLetter, firstLetter.toUpperCase());

        // Add symptom to symptomsList
        if (symptomsList.contains(symptom))
            Toast.makeText(this, "Symptom already added", Toast.LENGTH_SHORT).show();
        else if (symptomsList.size() >= 10)
            Toast.makeText(this, "Maximum symptoms added", Toast.LENGTH_SHORT).show();
        else {
            // If we are safe to continue, flush previous info
            items.clear();
            conditionsList.clear();
            // Then add the new symptom
            symptomsList.add(symptom);
            Log.i("WebService", "Symptom added: " + symptom);
            Log.i("WebService", "Current symptomsList: " + symptomsList.toString());
            // And perform a call to the LLM
            new ChatGPTConditions().execute(symptomsList);
            recyclerView.setVisibility(View.GONE);
            spinner.setVisibility(View.VISIBLE);
        }
    }

    public void SugConditionButtonClicked(View view) {

        spinner.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        Log.i("Debug", "Got Here");

        Button clickedButton = (Button) view; // since the view is the specific button that was clicked, this dynamically assigns the Button based on which button you clicked
        String clickedConditionName = clickedButton.getText().toString(); // get the name of the symptom in the clicked button


        if (db.getCondition(clickedConditionName) != null) { //checks if the clicked condition is already in the database
            // pass the name of the condition to the condition info screen and go there
            Intent intent = new Intent(AddSymptomsActivity.this, ConditionInfoActivity.class);
            intent.putExtra("name", clickedConditionName);
            startActivity(intent);
        }
        else
            new ChatGPTConditionInfo().execute(clickedConditionName); // execute the ChatGPT code for finding condition info, adding condition to database, and switching activity
    }

    public void setRVAdapter() {
        recyclerView.setVisibility(View.VISIBLE);
        spinner.setVisibility(View.GONE);
        recyclerView.setAdapter(new SuggestedConditionsAdapter(this, items));
    }


    // LLM functions

    class ChatGPTConditions extends AsyncTask {
        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            setRVAdapter();
        }

        @Override
        protected Object doInBackground(Object[] objects) { // this is called after the execute method
            return getChatGPTConditions((ArrayList) objects[0]); // this is the function that actually does everything
        }
    }

    protected String getChatGPTConditions(ArrayList symptoms) {
        // this is the function that finds suggested conditions and updates UI
        Log.i("Enter LLM", "Got to LLM function");
        final String urlStr = "https://api.openai.com/v1/chat/completions";
        final String apiKey = "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";
        final String model = "gpt-3.5-turbo";

        String profileDataAddIn = "";
        if (userAge > 0)
            profileDataAddIn += "When presenting your answer, take into consideration that the answer must be relevant to somebody with " + userAge + " years of age. ";
        if (userSex.equals("Male"))
            profileDataAddIn += "When presenting your answer, take into consideration that the answer must be relevant to somebody who is a male. Do not provide answers pertaining to pregnancy or female reproductive organs. ";
        else if (userSex.equals("Female"))
            profileDataAddIn += "When presenting your answer, take into consideration that the answer must be relevant to somebody who is a female. Do not provide answers pertaining to male reproductive organs. ";

        final String PROMPTSENTENCE = "Present your answer in a json format where each key has the first letter of each word capitalized and " +
                "is in an array named names. " + profileDataAddIn +
                "Given the following list of symptoms, what could be the possible conditions: ";

        try {
            // this block of code connects to ChatGPT and writes the prompt in
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);
            connection.setRequestProperty("Content-Type", "application/json");
            String finalPrompt = PROMPTSENTENCE + arrayListToPromptString(symptoms);
            String requestBody = "{\"model\": \"" + model + "\", \"messages\": [{\"role\": \"user\", \"content\": \"" + finalPrompt + "\"}]}";
            connection.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(requestBody);
            writer.flush();
            writer.close();

            handler.postDelayed(conditionsRestartRunnable, 20000);
            int responseCode = connection.getResponseCode(); // this gets the response code once ChatGPT finishes its response (warning: you might die of boredom before this happens)
            handler.removeCallbacks(conditionsRestartRunnable);

            Log.d("WebService", "Response code: " + responseCode);
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                // this block of code reads ChatGPT's response
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String output;
                StringBuffer response = new StringBuffer();
                while ((output = br.readLine()) != null) { response.append(output); }
                br.close();
                Log.i("WebService", "Response from ChatGPT: " + response);
                connection.disconnect();

                // get the part of ChatGPT's response that we asked for
                JSONObject jsonContent = extractMessageFromJSONResponse(response.toString());
                if (jsonContent == null) {
                    jsonContent = new JSONObject();
                }
                Log.i("WebService", "Requested JSON extracted from ChatGPT response: " + jsonContent);

                // use the array we asked for to populate conditionsList
                JSONArray conditionNames = jsonContent.getJSONArray("names");
                for (int i = 0; i < conditionNames.length(); i++) {
                    conditionsList.add(conditionNames.getString(i).toLowerCase());
                }

                for (int i = 0; i < conditionsList.size(); i++)
                {
                    String myItem = conditionsList.get(i);
                    if(myItem.length() != 0) {
                        myItem = myItem.replaceFirst("" + myItem.charAt(0), ("" + myItem.charAt(0)).toUpperCase());
                        for (int j = 0; j < myItem.length() - 1; j++) {
                            if(("" + myItem.charAt(j)).equals(" ")) {
                                myItem = myItem.replaceAll(" [a-z]", (" " + myItem.charAt(j + 1)).toUpperCase());
                            }
                        }
                        items.add(myItem);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("WebService", "getChatGPTConditions Error");
        }
        return null;
    }

    class ChatGPTConditionInfo extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] objects) { // this is called after the execute method
            return getChatGPTConditionInfo((String) objects[0]); // this is the function that actually does everything
        }
    }

    protected String getChatGPTConditionInfo(String condition) { // this is the function that finds condition info, adds condition to database, and switches activity
        final String urlStr = "https://api.openai.com/v1/chat/completions";
        final String apiKey = "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";
        final String model = "gpt-3.5-turbo";
        String profileDataAddIn = "";
        if (userAge > 0)
            profileDataAddIn += "When presenting your answer, take into consideration that the answer must be relevant to somebody with " + userAge + " years of age. ";
        if (userSex.equals("Male"))
            profileDataAddIn += "When presenting your answer, take into consideration that the answer must be relevant to somebody who is a male. Do not provide answers pertaining to pregnancy or female reproductive organs. ";
        else if (userSex.equals("Female"))
            profileDataAddIn += "When presenting your answer, take into consideration that the answer must be relevant to somebody who is a female. Do not provide answers pertaining to male reproductive organs. ";

        final String PROMPT = "Present your answer as a json object where each key is lowercase. " +
                profileDataAddIn +
                "Find a webpage on https://www.webmd.com/ that best describes the provided medical condition. " +
                "Provide arrays description, symptoms, common treatments, recommended actions, and webpage url " +
                "by using the webpage found on https://www.webmd.com/ for the medical condition: " + condition;

        try {
            // this block of code connects to ChatGPT and writes the prompt in
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);
            connection.setRequestProperty("Content-Type", "application/json");
            String requestBody = "{\"model\": \"" + model + "\", \"messages\": [{\"role\": \"user\", \"content\": \"" + PROMPT + "\"}]}";
            connection.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(requestBody);
            writer.flush();
            writer.close();

            Log.i("WebService", "Condition sent to ChatGPT: " + condition); // this gets the response code once ChatGPT finishes its response (warning: you might die of boredom before this happens)

            handler.postDelayed(infoRestartRunnable, 60000);
            int responseCode = connection.getResponseCode();
            handler.removeCallbacks(infoRestartRunnable);
			
            Log.d("WebService", "Response code: " + responseCode);
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                // this block of code reads ChatGPT's response
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String output;
                StringBuffer response = new StringBuffer();
                while ((output = br.readLine()) != null) { response.append(output); }
                br.close();
                Log.i("WebService", "Response from ChatGPT: " + response.toString());
                connection.disconnect();

                // get the part of ChatGPT's response that we asked for
                JSONObject jsonContent = extractMessageFromJSONResponse(response.toString());
                if (jsonContent == null) {
                    jsonContent = new JSONObject();
                }
                Log.i("WebService", "Requested JSON extracted from ChatGPT response: " + jsonContent.toString());

                // create the condition strings and arrays
                String conditionDescription = "";
                JSONArray conditionDescriptionArray = new JSONArray();
                if (jsonContent.get("description") instanceof String)
                    conditionDescription = jsonContent.getString("description");
                else if (jsonContent.get("description") instanceof JSONArray)
                    conditionDescriptionArray = jsonContent.getJSONArray("description");
                JSONArray conditionSymptomsArray = jsonContent.getJSONArray("symptoms");
                JSONArray conditionTreatmentsArray = jsonContent.getJSONArray("common treatments");
                JSONArray conditionActionsArray = jsonContent.getJSONArray("recommended actions");
                String conditionUrl = "";
                JSONArray conditionUrlArray = new JSONArray();
                if (jsonContent.get("webpage url") instanceof String)
                    conditionUrl = jsonContent.getString("webpage url");
                else if (jsonContent.get("webpage url") instanceof JSONArray)
                    conditionUrlArray = jsonContent.getJSONArray("webpage url");

                // create conditionDescription String if it was an array
                if (jsonContent.get("description") instanceof JSONArray) {
                    for (int i = 0; i < conditionDescriptionArray.length(); i++) {
                        conditionDescription += conditionDescriptionArray.getString(i) + " ";
                    }
                }

                // create conditionSymptoms String
                String conditionSymptoms = "";
                for (int i = 0; i < conditionSymptomsArray.length(); i++) {
                    conditionSymptoms += "- " + conditionSymptomsArray.getString(i) + "\n\n";
                }

                // create conditionTreatments String
                String conditionTreatments = "";
                for (int i = 0; i < conditionTreatmentsArray.length(); i++) {
                    conditionTreatments += "- " + conditionTreatmentsArray.getString(i) + "\n\n";
                }

                // create conditionActions String
                String conditionActions = "";
                for (int i = 0; i < conditionActionsArray.length(); i++) {
                    conditionActions += "- " + conditionActionsArray.getString(i) + "\n\n";
                }

                // create conditionUrl String if it was an array
                if (jsonContent.get("webpage url") instanceof JSONArray) {
                    for (int i = 0; i < conditionUrlArray.length(); i++) {
                        conditionUrl += conditionUrlArray.getString(i) + "\n";
                    }
                }

                // insert condition into database by using condition Strings
                db.insertCondition(condition, conditionUrl, conditionSymptoms, conditionDescription, conditionTreatments, conditionActions);
                Log.i("Database", "Condition just added to database: " + condition);

                // pass the name of the condition to the condition info screen and go there
                Intent intent = new Intent(AddSymptomsActivity.this, ConditionInfoActivity.class);
                intent.putExtra("name", condition);
                startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("WebService", "getChatGPTConditions Error");
        }
        return null;
    }

    public static String arrayListToPromptString(ArrayList list) { // this function just gets the prompt by using the symptomsList
        String convertedString = "";
        String finalString;

        for (int i = 0; i < list.size(); i++) {
            convertedString = convertedString + list.get(i);
        }

        finalString = convertedString;
        return finalString;
    }

    public static JSONObject extractMessageFromJSONResponse(String response) { // this function gets the useful part of the JSON that ChatGPT gives us
        try {
            JSONObject jsonResponse = new JSONObject(response);
            JSONArray jsonArrayList = jsonResponse.getJSONArray("choices");
            JSONObject firstItem = jsonArrayList.getJSONObject(0);
            JSONObject message = firstItem.getJSONObject("message");
            String contentStr = message.getString("content");

            return new JSONObject(contentStr);

        } catch (JSONException e) {
            e.printStackTrace();
            Log.i("WebService", "extractMessageFromJSONResponse Error");
            return null;
        }
    }
}