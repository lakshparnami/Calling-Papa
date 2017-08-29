package com.lakshparnami.callingpapa2.activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

//import com.lakshparnami.callingpapa2.ContactLoader;
import com.lakshparnami.callingpapa2.R;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        //getLoaderManager().restartLoader(0,new Bundle(),loaderCallbacks);
       // com.google.firebase.crash.FirebaseCrash.report(new Exception("My first Android non-fatal error"));
            SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            if(prefs.getBoolean("first",true))
            {
                SharedPreferences.Editor et=prefs.edit();
                et.putBoolean("first",false);
                et.apply();
                startActivity(new Intent(SplashScreen.this,MainActivity.class));
                finish();
            }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
        Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom);
        View bg=findViewById(R.id.icon_bg);
        bg.startAnimation(anim);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation)
            {
               /* if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CONTACTS)
                        == PackageManager.PERMISSION_GRANTED)
                {
                    ContactLoader loaderCallbacks = new ContactLoader(SplashScreen.this);
                    getLoaderManager().initLoader(0,new Bundle(),loaderCallbacks);
                    Toast toast=Toast.makeText(getApplicationContext(),"Reading Contacts",Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM,0,20);
                    toast.show();

                }*/
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(SplashScreen.this,MainActivity.class));
//                        overridePendingTransition(R.anim.slide_up_entry,R.anim.slide_up_exit);
                        finish();
                    }
                },500);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

       /* new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run() {
             finish();
            }
        },1000)
        ;*/
    }

}
