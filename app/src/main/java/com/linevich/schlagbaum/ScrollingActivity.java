package com.linevich.schlagbaum;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.Locale;

import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.content.Intent.ACTION_CALL;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class ScrollingActivity extends AppCompatActivity {
    TextView logTextView;
    static final String BAUM_NUMBER = "+380671733978";

    static boolean needToCallback = true;
    protected TextToSpeech ttobj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        canListenCalls();
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        logTextView = findViewById(R.id.textlog);

        final MyBroadcastReceiver br = new MyBroadcastReceiver(this);
        IntentFilter filter = new IntentFilter(READ_PHONE_STATE);
        filter.addAction("android.intent.action.PHONE_STATE");
        this.registerReceiver(br, filter);

        ttobj = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    ttobj.setLanguage(Locale.GERMAN);
                    ttobj.speak("Guten tag.", TextToSpeech.QUEUE_FLUSH, null);
//                    ttobj.setLanguage(Locale.UK);
//                    ttobj.speak("Good day.", TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                needToCallback = true;
                callBaum();
            }
        });
    }

    public void callBaum() {
        if (!needToCallback) {
            Log.d("main", "Already called but requested");
            return;
        }

        try {
            ttobj.speak("Nach schlagbaum telefonieren.", TextToSpeech.QUEUE_FLUSH, null);
//            ttobj.speak("Calling the gate.", TextToSpeech.QUEUE_FLUSH, null);
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        boolean canCall = ContextCompat.checkSelfPermission(getApplicationContext(), CALL_PHONE) == PERMISSION_GRANTED;
        if (!canCall) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 42);
        }
        Intent callIntent = new Intent(ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + BAUM_NUMBER.trim()));
        this.startActivity(callIntent);
        needToCallback = false;
    }

    private void canListenCalls() {
        boolean canListenCalls = ContextCompat.checkSelfPermission(getApplicationContext(), READ_PHONE_STATE) == PERMISSION_GRANTED;
        if (!canListenCalls) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 43);
        }
    }

    void log(String msg) {
        Log.d("", msg);
        logTextView.append(msg + "\n");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
