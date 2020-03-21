package com.one4ll.womansafety_womansafety;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class RescueScreen extends AppCompatActivity {

    private static final String TAG = "RescueScreen";

    private Button rescue;
    private Button edit_button;
    private Button contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rescue_screen);

        rescue = findViewById(R.id.rescue);
        edit_button = findViewById(R.id.edit_button);
        contact = findViewById(R.id.contact);
        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openContact();
            }
        });
        rescue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getSharedPreferences("storage",MODE_PRIVATE);
                String number = sharedPreferences.getString("number","0");
                Log.d(TAG, "onClick: "+number);
                String message = sharedPreferences.getString("message","0");
                //Sends message to Emergency contact
//                MessageUtil.sendEmergencyMessage(message, number);

            }
        });
        edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RescueScreen.this,MainActivity.class);
                intent.putExtra("number",getSharedPreferences("storage",MODE_PRIVATE).getString("number","0"));
                intent.putExtra("message",getSharedPreferences("storage",MODE_PRIVATE).getString("message","0"));
                startActivity(intent);
            }
        });
    }
    public void openContact(){
        Intent openContact = new Intent(RescueScreen.this, Contact.class);
        startActivity(openContact);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_screen_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.set_up_bluetooth_menu){
            Intent intent = new Intent(RescueScreen.this,BluetoothDevicesListActivity.class);
            startActivity(intent);
            return true;
        }
        return false;
    }
}
