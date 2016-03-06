package com.tixon.phonemask;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText editText = (EditText) findViewById(R.id.mEditText);
        try {
            editText.addTextChangedListener(new PhoneTextWatcher.MaskBuilder(editText)
                    .setIndexLength(5)
                    .setPrefix("+7")
                    .setPattern("322")
                    .build());
        } catch (Exception e) {
            Log.e("myLogs", "error: " + e.toString());
            e.printStackTrace();
            editText.addTextChangedListener(new PhoneTextWatcher.MaskBuilder(editText).build());
        }

        Button button = (Button) findViewById(R.id.mButtonDelete);
        /*button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textWatcher.initializeEditText();
            }
        });*/
    }
}
