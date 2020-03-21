package com.one4ll.womansafety_womansafety;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity {
    private EditText number;
    private EditText message;
    private Button submit;
    private RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        number = findViewById(R.id.number);
        message = findViewById(R.id.message);
        submit = findViewById(R.id.submit);
        relativeLayout = findViewById(R.id.relative_layout);
        relativeLayout.setVisibility(View.INVISIBLE);

        SharedPreferences firstTimeActivity = getSharedPreferences("isFirstTime",MODE_PRIVATE);
        if (getIntent().getStringExtra("number") != null){
            relativeLayout.setVisibility(View.VISIBLE);
            String intentNumber = getIntent().getStringExtra("number");
            String intentMessag = getIntent().getStringExtra("message");
            number.setText(intentNumber);
            message.setText(intentMessag);
            firstTimeActivity.edit().putBoolean("isFirst",true).apply();

        }

        if (firstTimeActivity.getBoolean("isFirst",true)){
            firstTimeActivity.edit().putBoolean("isFirst",false).apply();
        }else {

            Intent intent = new Intent(MainActivity.this,RescueScreen.class);

            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        relativeLayout.setVisibility(View.VISIBLE);

        final SharedPreferences sharedPreferences = getSharedPreferences("storage",MODE_PRIVATE);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkForValid()){
                    sharedPreferences.edit().putString("number",number.getText().toString()).apply();
                    sharedPreferences.edit().putString("message",message.getText().toString()).apply();
                    Intent intent = new Intent(MainActivity.this,RescueScreen.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }

            }
        });
    }
    private boolean checkForValid(){
        boolean flag  = true;
        if (number.getText().toString().isEmpty() || number.getText().toString().trim().equals("")){
            number.setError("Please Enter a number");
            number.requestFocus();
            flag = false;
        }
        if (number.getText().toString().length() != 10){
            number.setError("This is not looks like phone number");
            number.requestFocus();
            flag = false;
        }
        if (message.getText().toString().isEmpty() || message.getText().toString().trim().equals("")){
            message.setError("Please Enter a message");
            flag = false;
        }
        return flag;
    }

}

