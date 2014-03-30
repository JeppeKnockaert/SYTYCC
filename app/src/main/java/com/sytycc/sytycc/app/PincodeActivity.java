package com.sytycc.sytycc.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputFilter;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sytycc.sytycc.app.data.Notifiable;

/**
 * Created by Roel on 30/03/14.
 */
public class PincodeActivity extends Activity {

    private EditText pincodeEditText;
    private Button okButton;
    private Button cancelButton;
    private static int PIN_LENGTH = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.pincode_layout);
        findViewById(R.id.linearLayout).setBackgroundColor(Color.WHITE);

        pincodeEditText = (EditText) findViewById(R.id.pinEditText);
        pincodeEditText.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(PIN_LENGTH),
                DigitsKeyListener.getInstance(),
        });
        okButton = (Button) findViewById(R.id.pinOKButton);
        cancelButton = (Button) findViewById(R.id.pinCancelButton);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pin = pincodeEditText.getText().toString();
                                /* Validate with pin dummy data because we can not access the
                                * pin code of a user in the api */
                if(!pin.equals("") && Integer.parseInt(pin) < 500000){
                                    /* Pin correct, show detailed information about */
                    Intent intent = new Intent(PincodeActivity.this, MainActivity.class);
                    if((getIntent() != null) && (getIntent().getExtras() != null) && (getIntent().getExtras().getInt("TAB") == 1)){
                        intent.putExtra("TAB",1);
                    }
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(PincodeActivity.this, "PIN incorrect", Toast.LENGTH_SHORT).show();
                }
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }
}
