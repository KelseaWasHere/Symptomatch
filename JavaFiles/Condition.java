package com.example.symptomatch;

public class Condition {

    private String name;
    private String url;
    private String symptoms;
    private String description;
    private String treatments;
    private String actions;
    private int saved;

    public Condition(){}

    public Condition(String name, String url, String symptoms, String description,
                     String treatments, String actions, int saved) {
        this.name = name;
        this.url = url;
        this.symptoms = symptoms;
        this.description = description;
        this.treatments = treatments;
        this.actions = actions;
        this.saved = saved;
    }

    public String getName() {
        String myName = name;
        if(name.length() != 0) {
            myName = myName.replaceFirst("" + myName.charAt(0), ("" + myName.charAt(0)).toUpperCase());
            for (int j = 0; j < myName.length() - 1; j++) {
                if (("" + myName.charAt(j)).equals(" ")) {
                    myName = myName.replace(" " + myName.charAt(j + 1), (" " + myName.charAt(j + 1)).toUpperCase());
                }
            }
        }
        return myName;
    }

    public void setName(String name) { this.name = name; }

    public String getUrl() { return url; }

    public void setUrl(String url) { this.url = url; }

    public String getSymptoms() { return symptoms; }

    public void setSymptoms(String symptoms) { this.symptoms = symptoms; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public String getTreatments() { return treatments; }

    public void setTreatments(String treatments) { this.treatments = treatments; }

    public String getActions() { return actions; }

    public void setActions(String actions) { this.actions = actions; }

    public int getSaved() { return saved; }

    public boolean isSaved() { return saved > 0; }

    public void setSaved(int saved) { this.saved = saved; }

}
