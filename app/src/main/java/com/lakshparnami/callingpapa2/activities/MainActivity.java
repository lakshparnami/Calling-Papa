package com.lakshparnami.callingpapa2.activities;

import android.Manifest;
import android.app.DialogFragment;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;


import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.lakshparnami.callingpapa2.dialogs.DeleteLogsDialog;
import com.lakshparnami.callingpapa2.dialogs.DeleteContactsDialog;
import com.lakshparnami.callingpapa2.R;
import com.lakshparnami.callingpapa2.fragments.AddFragment;
import com.lakshparnami.callingpapa2.fragments.LogsFragment;
import com.lakshparnami.callingpapa2.fragments.SavedDataFragment;
import com.lakshparnami.callingpapa2.fragments.SuperFragment;


public class MainActivity extends AppCompatActivity {
    public static View.OnClickListener addListener;
    private static View.OnClickListener deleteContactsListener;
    private static View.OnClickListener deleteLogsListener;
    private FloatingActionButton fab;
    private InterstitialAd mInterstitialAd;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1003;


    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mInterstitialAd.loadAd(adRequest);
    }


    @Override
    protected void onResume() {
        super.onResume();
        requestNewInterstitial();
        SavedDataFragment.mAdapter.notifyDataSetChanged();
        LogsFragment.mAdapter.notifyDataSetChanged();
    }
    @Override
    public void onBackPressed() {
        overridePendingTransition(R.anim.nothing,R.anim.exit_contact);
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            finish();
        }
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));
        requestNewInterstitial();
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
                finish();
                //beginPlayingGame();
            }
        });
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED||ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_CONTACTS,Manifest.permission.CALL_PHONE},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);


      /*  AddFragment.PNAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.list_dropdowwn_item, new ArrayList<String>());
        AddFragment.NPAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.list_dropdowwn_item, new ArrayList<String>());
        AddFragment.phoneAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.list_dropdowwn_item, new ArrayList<String>());
        AddFragment.nameAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.list_dropdowwn_item, new ArrayList<String>());
//     */ //  readContactData();
        //startActivity(new Intent(MainActivity.this,SplashScreen.class));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#323232"));
        }
        initViewPagerAndTabs();
//        fab.setColorFilter(Color.parseColor("#006699"));
        fab = (FloatingActionButton) findViewById(R.id.fab);
        deleteContactsListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SavedDataFragment.itemsData.isEmpty())
                    SuperFragment.customToast(getResources().getString(R.string.empty),MainActivity.this);
                else
                {
                    DialogFragment dialogFragment = new DeleteContactsDialog();
                    dialogFragment.show(getFragmentManager(), "Name");
                }
            }
        };
        deleteLogsListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LogsFragment.itemsData.isEmpty())
                    SuperFragment.customToast(getResources().getString(R.string.empty),MainActivity.this);
                else
                {
                    DialogFragment dialogFragment = new DeleteLogsDialog();
                    dialogFragment.show(getFragmentManager(), "Name");
                }
            }
        };
    }



    private void initViewPagerAndTabs() {
        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        String[] a={getString(R.string.tab1),getString(R.string.tab2),getString(R.string.tab3)};
        ViewPagerAdapter pagerAdapter=new ViewPagerAdapter(getSupportFragmentManager(),a);
        if (viewPager != null) {
            viewPager.setAdapter(pagerAdapter);
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    setListener(position);
                }

                @Override
                public void onPageSelected(int position) {
                    setListener(position);
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(
                            INPUT_METHOD_SERVICE);
                    if (getCurrentFocus()!=null)
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }
            });
            viewPager.setPageTransformer(true,new ZoomOutPageTransformer());
        }
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        if (tabLayout != null) {
            tabLayout.setupWithViewPager(viewPager);
        }
/*
        ContactLoader loader=new ContactLoader();
        loader.setPriority(Thread.MAX_PRIORITY);
        loader.start();*/
/**/
    }

    private void setListener(final int position) {

        if(position == 1)
        {
            fab.setOnClickListener(deleteContactsListener);
            fab.setImageResource(R.drawable.ic_delete_white_36dp);
        }
        else
        if(position == 0)
        {
            fab.setOnClickListener(addListener);
            fab.setImageResource(R.drawable.ic_add_white_36dp);
        }
        else
        if(position == 2)
        {
            fab.setOnClickListener(deleteLogsListener);
            fab.setImageResource(R.drawable.ic_delete_white_36dp);
        }

    }

    public static class ViewPagerAdapter extends FragmentStatePagerAdapter {

            private final CharSequence[] Titles;
            private final int NumbOfTabs;
            public ViewPagerAdapter(FragmentManager fm, CharSequence mTitles[]) {
                super(fm);
                this.Titles = mTitles;
                this.NumbOfTabs = 3;
            }
            @Override
            public Fragment getItem(int position) {
                if(position == 0)
                    return new AddFragment();
                if(position==1)
                    return new SavedDataFragment();
                if(position==2)
                    return new LogsFragment();
                return new AddFragment();
            }
            @Override
            public CharSequence getPageTitle(int position) {
                return Titles[position];
            }
            @Override
            public int getCount() {
                return NumbOfTabs;
            }
    }

    public static class ZoomOutPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.5f;
        private static final float MIN_ALPHA = 0.4f;
        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();
            if (position < -1)
                view.setAlpha(0);
            else if (position <= 1) {
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                if (position < 0)
                    view.setTranslationX(horzMargin - vertMargin / 2);
                else
                    view.setTranslationX(-horzMargin + vertMargin / 2);
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);
                view.setAlpha(MIN_ALPHA + (scaleFactor - MIN_SCALE) / (1 - MIN_SCALE) * (1 - MIN_ALPHA));
            } else view.setAlpha(0);

        }
    }
}

  /**  private synchronized void readContactData() {

        try {
            *//*********** Reading Contacts Name And Number **********//*

            String phoneNumber;
            ContentResolver cr = getBaseContext()
                    .getContentResolver();

            //Query to get contact name

            Cursor cur = cr
                    .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            null,
                            null,
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");

            // If data data found in contacts
            if (cur != null && cur.getCount() > 0) {

                Log.i("AutocompleteContacts", "Reading   contacts........");

                //int k = 0;
                String name;

                while (cur.moveToNext()) {

                    String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                    name = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

                    //Check contact have phone number
                    if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0)
                    {

                        //Create query to get phone number by contact id
                        Cursor pCur = cr
                                .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                        null,
                                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                                + " = "+id,
                                        null,
                                        null);

                        if (pCur != null)
                        {
                            pCur.moveToFirst();
                            do
                            {
                                try
                                {
                                    phoneNumber = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                    phoneNumber= PhoneNumberUtils.stripSeparators(phoneNumber);
                                    phoneNumber=SuperFragment.removeCountryCode(phoneNumber,MainActivity.this);
                                    AddFragment.nameAdapter.add(name);
                                    AddFragment.phoneAdapter.add(phoneNumber);
                                    AddFragment.PNAdapter.add(phoneNumber+","+name);
                                    AddFragment.NPAdapter.add(name+" ,"+phoneNumber);

                                }
                                catch (Exception e)
                                {
                                    Log.e("Contact not saved",e+"");
                                }
                            }while (pCur.moveToNext());
                            pCur.close();
                        }
                    } // End if

                }  // End while loop

            } // End Cursor value check
            if (cur != null) {
                cur.close();
            }


        } catch (Exception e) {
            Log.i("AutocompleteContacts","Exception : "+ e);
        }


    }
    private class ContactLoader extends Thread
    {
        public ContactLoader()
        {

        }
        public void run()
        {
            readContactData();
        }
    }*/


