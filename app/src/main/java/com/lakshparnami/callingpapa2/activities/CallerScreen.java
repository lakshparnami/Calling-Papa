package com.lakshparnami.callingpapa2.activities;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.internal.telephony.ITelephony;
import com.lakshparnami.callingpapa2.OutCallReceiver;
import com.lakshparnami.callingpapa2.R;
import com.lakshparnami.callingpapa2.databasehandlers.LogsDatabaseHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class CallerScreen extends Activity {
    private Boolean turnSpeakerOn = true;
    private Boolean turnMicOff = true;
    private Boolean showDialer = true;
    private Boolean putOnHold = true;
    private byte[] photobyte;
    private void Del(ContentResolver resolver, String strNum) {
        try {
            String strUriCalls = "content://call_log/calls";
            Uri UriCalls = Uri.parse(strUriCalls);
            //Cursor c = res.query(UriCalls, null, null, null, null);
            if (null != resolver) {
                resolver.delete(UriCalls, CallLog.Calls.NUMBER + " ='" + strNum + "'" +CallLog.Calls.CONTENT_TYPE+""+ CallLog.Calls.OUTGOING_TYPE,null);
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    private com.android.internal.telephony.ITelephony telephonyService;

    private void getTeleService() {
        TelephonyManager tm = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        try {
            // Java reflection to gain access to TelephonyManager's
            // ITelephony getter
            Log.v("LakshayVibhor", "Get getTeleService...");
            Class c = Class.forName(tm.getClass().getName());
            Method m = c.getDeclaredMethod("getITelephony");
            m.setAccessible(true);
            this.telephonyService = (ITelephony) m.invoke(tm);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("LakshayVibhor", "FATAL ERROR: could not connect to telephony subsystem");
            Log.e("LakshayVibhor", "Exception object: " + e);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        DeleteLog();
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(oldAudioMode);
        audioManager.setRingerMode(oldRingerMode);
        audioManager.setSpeakerphoneOn(isSpeakerPhoneOn);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return keyCode == KeyEvent.KEYCODE_HOME;
    }

    public static int oldAudioMode, oldRingerMode;
    public static boolean isSpeakerPhoneOn;
private long duration;
private Date callDateTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY | WindowManager.LayoutParams.TYPE_PHONE|WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);
        setContentView(R.layout.activity_caller_screen);
        duration =System.currentTimeMillis();
        callDateTime =new Date();
//        Toast.makeText(getApplicationContext(), callDateTime.toString(),Toast.LENGTH_LONG).show();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                TextView name = (TextView) findViewById(R.id.fakeName);
                TextView number = (TextView) findViewById(R.id.fakeNumber);
                name.setText(OutCallReceiver.Name);
                number.setText(OutCallReceiver.fakeNumber);
                Bitmap photo;

                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CONTACTS)
                        == PackageManager.PERMISSION_GRANTED)
                try {
                    if (OutCallReceiver.id.equals("")) OutCallReceiver.id = "0";
                    InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(getContentResolver(),
                            ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.valueOf(OutCallReceiver.id)), true);
                    if (inputStream != null) {
                        photo = BitmapFactory.decodeStream(inputStream);
                        ImageView contactImage = (ImageView) findViewById(R.id.contactImage);
                        contactImage.setImageBitmap(photo);
                        inputStream = ContactsContract.Contacts.openContactPhotoInputStream(getContentResolver(),
                                ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI,
                                        Long.valueOf(OutCallReceiver.id)), false);
                        photo = BitmapFactory.decodeStream(inputStream);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        photobyte = stream.toByteArray();
                        inputStream.close();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, 10);
//        createFakeLog();
        final FloatingActionButton endCall = (FloatingActionButton) findViewById(R.id.endCallButton);
        final ImageButton speaker = (ImageButton) findViewById(R.id.speaker);
        final ImageButton mic = (ImageButton) findViewById(R.id.mic);
        final ImageButton dialpad = (ImageButton) findViewById(R.id.dialpad);
        final ImageButton hold = (ImageButton) findViewById(R.id.hold);
        endCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        endCall.hide();
                        getTeleService();
                        telephonyService.endCall();
                        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                        audioManager.setMode(oldAudioMode);
                        audioManager.setRingerMode(oldRingerMode);
                        audioManager.setSpeakerphoneOn(isSpeakerPhoneOn);
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("EXIT", true);
                        startActivity(intent);
                        finish();
                    }
                }, 50);
                DeleteLog();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        DeleteLog();
                        createFakeLog();
                    }
                }, 200);
            }
        });

        speaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                audioManager.setMode(AudioManager.MODE_IN_CALL);
                audioManager.setSpeakerphoneOn(turnSpeakerOn);
                if (turnSpeakerOn) speaker.setBackgroundResource(R.drawable.selected_circle);
                else {
                    speaker.setBackgroundColor(Color.parseColor("#00000000"));
                    audioManager.setMode(oldAudioMode);
                    audioManager.setRingerMode(oldRingerMode);
                    audioManager.setSpeakerphoneOn(isSpeakerPhoneOn);
                }
                turnSpeakerOn = !turnSpeakerOn;

            }
        });
        mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                audioManager.setMode(AudioManager.MODE_IN_CALL);
                audioManager.setMicrophoneMute(turnMicOff);
                if (turnMicOff) mic.setBackgroundResource(R.drawable.selected_circle);
                else {
                    mic.setBackgroundColor(Color.parseColor("#00000000"));
                    audioManager.setMode(oldAudioMode);
                    audioManager.setRingerMode(oldRingerMode);
                    audioManager.setSpeakerphoneOn(isSpeakerPhoneOn);
                }
                turnMicOff = !turnMicOff;

            }
        });
        dialpad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (showDialer) dialpad.setBackgroundResource(R.drawable.selected_circle);
                else dialpad.setBackgroundColor(Color.parseColor("#00000000"));
                showDialer = !showDialer;
            }
        });
        hold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (putOnHold) hold.setBackgroundResource(R.drawable.selected_circle);
                else hold.setBackgroundColor(Color.parseColor("#00000000"));
                putOnHold = !putOnHold;
            }
        });
    }

    private void makeLogEntry(Date callDateTime, long millis) {
        SimpleDateFormat dateFormatter= new SimpleDateFormat("dd/MM/yyyy , hh:mm:ss", Locale.US);
        String DateTime=dateFormatter.format(callDateTime);
        long second = (millis / 1000) % 60;
        long minute = (millis / (60000)) % 60;
        long hour = (millis / (3600000)) % 24;
        String durations=hour+" h"+minute+" m"+second+" s";
        LogsDatabaseHandler dbh=new LogsDatabaseHandler(getApplicationContext());
        dbh.Open();
        dbh.insertData(OutCallReceiver.realNumber, OutCallReceiver.fakeNumber, OutCallReceiver.Name, OutCallReceiver.id, DateTime, durations,photobyte);
        dbh.Close();

    }

    private void createFakeLog() {
        ContentValues values = new ContentValues();
        values.put(CallLog.Calls.NUMBER, OutCallReceiver.fakeNumber);
        values.put(CallLog.Calls.DATE, System.currentTimeMillis());
        duration =new Date().getTime()-callDateTime.getTime();
        makeLogEntry(callDateTime, duration);
        duration = duration /1000;
        values.put(CallLog.Calls.DURATION, duration);
        values.put(CallLog.Calls.TYPE, CallLog.Calls.OUTGOING_TYPE);
        values.put(CallLog.Calls.NEW, 1);
        values.put(CallLog.Calls.CACHED_NAME, "");
        values.put(CallLog.Calls.CACHED_NUMBER_TYPE, 0);
        values.put(CallLog.Calls.CACHED_NUMBER_LABEL, "");
        Log.e("LakshCallPaa", "Inserting edit log placeholder for " + OutCallReceiver.fakeNumber);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALL_LOG) == PackageManager.PERMISSION_GRANTED)
            getContentResolver().insert(CallLog.Calls.CONTENT_URI, values);
    }


    private void DeleteLog() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Del(getContentResolver(), OutCallReceiver.RNO2);
                String s = OutCallReceiver.RNO2;
                s = PhoneNumberUtils.stripSeparators(s);
                if (s.length() >= 10)
                    s = s.substring(s.length() - 10, s.length());
                Del(getContentResolver(), s);
            }
        }, 1000);
    }

        @Override
    public void onBackPressed() {
    }
}
