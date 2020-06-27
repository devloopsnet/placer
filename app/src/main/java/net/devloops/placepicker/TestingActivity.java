package net.devloops.placepicker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import net.devloops.placepicker.ui.main.TestingFragment;

public class TestingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.testing_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, TestingFragment.newInstance())
                    .commitNow();
        }
    }
}