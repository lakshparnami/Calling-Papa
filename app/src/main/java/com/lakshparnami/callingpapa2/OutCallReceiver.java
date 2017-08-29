package com.lakshparnami.callingpapa2;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.CallLog;
import android.telephony.PhoneNumberUtils;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.lakshparnami.callingpapa2.activities.CallerScreen;
import com.lakshparnami.callingpapa2.databasehandlers.DetailsDatabaseHandler;

public class OutCallReceiver extends BroadcastReceiver {
    public static String fakeNumber="",realNumber="",Name="",RNO2="",id="0";
    public static final int DEFAULT_DELAY=1050;
    public static final int MAX_DELAY=2500;
    private static int lastState = TelephonyManager.CALL_STATE_IDLE;
    public OutCallReceiver()
    {

    }
    private void Del(ContentResolver resolver, String strNum)
    {
        try
        {
            Uri UriCalls = Uri.parse("content://call_log/calls");
            if(resolver != null)
            {
                resolver.delete(UriCalls, CallLog.Calls.NUMBER +" ='"+strNum+"'",null);
            }
        }
        catch(Exception e)
        {
            e.getMessage();
        }
    }
    private void DeleteLog(final Context context) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Del(context.getContentResolver(), OutCallReceiver.RNO2);
                String s= OutCallReceiver.RNO2;
                s= PhoneNumberUtils.stripSeparators(s);
                if(s.length()>=10)
                    s=s.substring(s.length()-10,s.length());
                Del(context.getContentResolver(), s);

            }
        },1000);
    }
    @Override
    public void onReceive(final Context context, final Intent intent) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        final int delay = MAX_DELAY - prefs.getInt("sensitivity", DEFAULT_DELAY);
        if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL))
            if (prefs.getBoolean("Enabled", true)) try {
                if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
                    realNumber = (intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER));
                    final String RNO1 = realNumber;
                    if (realNumber != null) {
                            realNumber = PhoneNumberUtils.stripSeparators(realNumber);
                        if (realNumber.length() > 15)
                            realNumber = realNumber.substring(realNumber.length() - 15, realNumber.length());
                        final TelephonyManager tm = (TelephonyManager) context.getSystemService(
                                Context.TELEPHONY_SERVICE);

                        tm.listen(new PhoneStateListener() {
                            @Override
                            public void onCallStateChanged(int state, String incomingNumber) {
                                if (state== TelephonyManager.CALL_STATE_OFFHOOK && lastState != TelephonyManager.CALL_STATE_RINGING)
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                DetailsDatabaseHandler dbh = new DetailsDatabaseHandler(context);
                                                dbh.Open();
                                                Cursor c = dbh.returnData();
                                                if (c.moveToFirst()) {
                                                    do {
                                                        if (prefs.getBoolean("Enabled", false)) {
                                                            if (realNumber.equals(c.getString(0))) {
                                                                RNO2 = RNO1;
                                                                fakeNumber = c.getString(1);
                                                                Name = c.getString(2);
                                                                id = c.getString(3);
                                                                AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                                                                CallerScreen.oldAudioMode = audioManager.getMode();
                                                                CallerScreen.oldRingerMode = audioManager.getRingerMode();
                                                                CallerScreen.isSpeakerPhoneOn = audioManager.isSpeakerphoneOn();
                                                                Intent i = new Intent(context, CallerScreen.class);
                                                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                                                i.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                                                                i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                                                                DeleteLog(context);
                                                                context.startActivity(i);
                                                            }
                                                        }
                                                    } while (c.moveToNext());
                                                }
                                                dbh.Close();
                                            }
                                        }, delay);
                                lastState = state;
                                DeleteLog(context);
                            }
                        }, PhoneStateListener.LISTEN_CALL_STATE);

                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
    }
}