package com.grootan.slackoncrash.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button crashIt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        crashIt = (Button) findViewById(R.id.crashIt);
        crashIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String test = null;
                test.toLowerCase();
            }
        });
    }
}
