package com.lakshparnami.callingpapa2.activities;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.ContentUris;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Build;
import android.support.v7.graphics.Palette;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import com.lakshparnami.callingpapa2.R;
import com.lakshparnami.callingpapa2.databasehandlers.LogsDatabaseHandler;
import com.lakshparnami.callingpapa2.dialogs.UpdateDialog;
import com.lakshparnami.callingpapa2.recyclers.ContactLogRecyclerAdapter;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;

public class Contact extends AppCompatActivity {
    public static final ArrayList<ContactLogRecyclerAdapter.ContactLogData> itemsData = new ArrayList<>();
    private static final ContactLogRecyclerAdapter mAdapter = new ContactLogRecyclerAdapter();

    private static void getDetails(Activity activity, String real) {
        itemsData.clear();
        Cursor c;
        final LogsDatabaseHandler dbh = new LogsDatabaseHandler(activity.getApplicationContext());
        dbh.Open();
        c = dbh.returnDetails(real);
        if (c.moveToFirst()) {
            do {
                itemsData.add(new ContactLogRecyclerAdapter.ContactLogData(

                        c.getString(4),
                        c.getString(5),
                        c.getString(2)));
            } while (c.moveToNext());
        }
        dbh.Close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        MobileAds.initialize(getApplicationContext(), getString(R.string.app_add_id));
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("160295886508A2E5B827FD64FBF69A09")
                .build();
        mAdView.loadAd(adRequest);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        final Bundle args=getIntent().getExtras();
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(args.getString("real"));
        collapsingToolbarLayout.setTitleEnabled(true);

        if (toolbar != null) {
            toolbar.setTitle(args.getString("real"));
        }
        Bitmap photo;
        ImageButton callC= (ImageButton) findViewById(R.id.callC);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        ImageView contactImage1 = (ImageView) findViewById(R.id.contactImage);
        String id=args.getString("imageID");
        try {
            if(id!=null){
            if (id.equals("")) 
                id = "0";
            InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(getContentResolver(),
                    ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, 
                            Long.valueOf(id)), true);
            if (inputStream != null) {
                photo = BitmapFactory.decodeStream(inputStream);
               // ImageView contactImage = (ImageView) findViewById(R.id.contactImage);
                if (contactImage1 != null) {
                    contactImage1.setImageBitmap(photo);
                }
                Palette.Builder builder=Palette.from(photo);
                Palette palette=builder.generate();
                int back=palette.getDarkVibrantColor(0);
                if(back==0)
                    back=palette.getVibrantColor(0);
                if(back==0)
                    back=palette.getDarkVibrantColor(0);
                if(back==0)
                    back=palette.getMutedColor(0);
                if(back==0)
                    back=palette.getDarkMutedColor(0);
                if(back==0)
                    back=palette.getLightMutedColor(Color.parseColor("#d50000"));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Window window = getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(back);
                }
                collapsingToolbarLayout.setContentScrimColor(back);
                fab.setBackgroundTintList(ColorStateList.valueOf(back));
                inputStream.close();
            }
            else
            {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Window window = getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(Color.parseColor("#a40000"));
                }
                contactImage1.setImageResource(R.drawable.pic);

            }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DialogFragment dialogFragment = new UpdateDialog();
                dialogFragment.setArguments(args);
                dialogFragment.show(getFragmentManager(), "Name");


            }
        });
        if (callC != null) {
            callC.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String s = args.getString("real");
                    final Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:" + s));
                    callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(callIntent);

                }
            });
        }

        itemsData.clear();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(mAdapter);
        getDetails(this, args.getString("real"));
        Collections.reverse(itemsData);
        mAdapter.notifyDataSetChanged();
        TextView name= (TextView) findViewById(R.id.name);
        TextView fake= (TextView) findViewById(R.id.fake);
        if (name != null) {
            name.setText(args.getString("name"));
        }
        if (fake != null) {
            fake.setText(args.getString("fake"));
        }
        String status1=args.getString("status");
        if(status1!=null)
        if (status1.equals("calllog")){
            fab.hide();
            if (callC != null) {
                callC.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            supportFinishAfterTransition();
        }else{
            overridePendingTransition(R.anim.nothing, R.anim.exit_contact);
        }
        super.onBackPressed();
    }
}
