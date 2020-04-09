package ca.andrewmcneill.chroniclelist;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class CreditsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit);
        getSupportActionBar().setTitle("About ChronicleList");
    }
}
