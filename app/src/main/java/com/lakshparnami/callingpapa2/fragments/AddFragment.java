package com.lakshparnami.callingpapa2.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.lakshparnami.callingpapa2.OutCallReceiver;
import com.lakshparnami.callingpapa2.databasehandlers.DetailsDatabaseHandler;
import com.lakshparnami.callingpapa2.R;
import com.lakshparnami.callingpapa2.activities.MainActivity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AddFragment extends SuperFragment {
    private static final int REAL_CONTACT_PICKER_RESULT = 1001;
    private static final int FAKE_CONTACT_PICKER_RESULT = 1002;
    private static String id="";
    //private static boolean isChecked=true;

    private View v;
    private String nameS;
    private String cNumber;
    private boolean clear;
    private AutoCompleteTextView real;
    private AutoCompleteTextView fake;
    private AutoCompleteTextView name;
    private Spinner realCode,fakeCode;
    private ArrayAdapter<Integer> CodeAdapter;
    private int spinnerPosition;
//    private List<Integer> x;

 /*   public static ArrayAdapter<String>  phoneAdapter,nameAdapter;
    public static Set<String>  phoneSet,nameSet;*/

/*
    private void setToArrayAdapter(){
        if(phoneAdapter!=null&&nameAdapter!=null)
        {
        phoneAdapter.clear();
        nameAdapter.clear();
        phoneAdapter.addAll(phoneSet);
        nameAdapter.addAll(nameSet);
        }
    }
*/
    public AddFragment() {
        nameS = "";
        cNumber = "";
        clear = true;
    }


    private Integer getCountryZipCode(){
        String CountryID;
        String CountryZipCode="";

        TelephonyManager manager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        //getNetworkCountryIso
        CountryID= manager.getSimCountryIso().toUpperCase();
        String[] rl=this.getResources().getStringArray(R.array.CountryCodes);
        for (String aRl : rl) {
            String[] g = aRl.split(",");
            if (g[1].trim().equals(CountryID.trim())) {
                CountryZipCode = g[0];
                break;
            }
        }
        if(!CountryZipCode.equals(""))
        return Integer.parseInt(CountryZipCode);
        else
            return 0;
    }

    private String getFormattedNumber(String Number, String code) {
        Number=PhoneNumberUtils.stripSeparators(Number);
        if(Number.length()>15)
            Number=Number.substring(Number.length() - 15, Number.length());
        Number=code+Number;
        if(Integer.parseInt(code)!=0)
            Number="+"+Number;
        return Number;
    }

    private void doLaunchContactPicker(int fakeOrReal) {
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED){
            Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(contactPickerIntent, fakeOrReal);
        }
        else
        {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_CONTACTS},101);
        }
    }
    private void databaseAdd()
    {
        if (real.getText().toString().isEmpty() || fake.getText().toString().isEmpty() || name.getText().toString().isEmpty())
        {
            customToast(getActivity().getResources().getString(R.string.please_enter_all),getActivity());
            clear=false;
        }
        else {
            cNumber=real.getText().toString();
            String fakeNumber=fake.getText().toString();
            cNumber=getFormattedNumber(cNumber,realCode.getSelectedItem().toString());
            fakeNumber=getFormattedNumber(fakeNumber,fakeCode.getSelectedItem().toString());

            DetailsDatabaseHandler dbh = new DetailsDatabaseHandler(getActivity().getApplicationContext());
            dbh.Open();
            Cursor c = dbh.returnDetails(cNumber);
            if (c.moveToFirst()) {
                if(fakeNumber.equals(cNumber))
                {
                    customToast(getActivity().getResources().getString(R.string.same_number),getActivity());
                }else
                {

                    dbh.updateData(cNumber, fakeNumber, name.getText().toString(),id);
                    customToast(cNumber + getActivity().getResources().getString(R.string.updated),getActivity());
                    clear=true;
                }
            }
            else
            {
                if(fakeNumber.equals(cNumber))
                {
                    customToast(getActivity().getResources().getString(R.string.same_number),getActivity());
                    clear=false;
                }
                else
                {
                    dbh.insertData(cNumber,fakeNumber, name.getText().toString(),id);
                    customToast(getActivity().getResources().getString(R.string.added),getActivity());
                }
                clear=true;
            }
            id= "";
            dbh.Close();
        }
    }

    @Override
    public void onActivityResult(final int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        Set<String> numberList=new HashSet<>();
        if (resultCode == Activity.RESULT_OK)
        {
            final String idOnResult;
            Uri contactData = data.getData();
            final Cursor c = getActivity().getContentResolver().query(contactData, null, null, null, null);
            if(c!=null) {
                if (c.moveToFirst()) {
                    idOnResult = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                    String hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                    if (hasPhone.equalsIgnoreCase("1"))
                    {
                        Cursor phones = getActivity().getContentResolver().query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + idOnResult,
                                null, null);
                        if (phones != null&& phones.moveToFirst())
                        {

//                            if()
                            cNumber = phones.getString(phones.getColumnIndex("data1"));
                            nameS = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                            do
                            {
                                numberList.add(phones.getString(phones.getColumnIndex("data1")));
                            }
                            while (phones.moveToNext());
                            final CharSequence[] items = numberList.toArray(new String[numberList.size()]);
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setTitle(R.string.choose);
                            builder.setItems(items, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int item) {
                                    cNumber = items[item].toString();
                                    modifyAndInsert(cNumber,reqCode,idOnResult);
                                }
                            });
                            AlertDialog alert = builder.create();
                            if (numberList.size() > 1) {
                                alert.show();
                            } else {
                                modifyAndInsert(cNumber,reqCode,idOnResult);
                            }
                            phones.close();}
                    }
                }
                c.close();
            }
        }
    }
private void modifyAndInsert(String cNumber, int reqCode, String idOnResult)
{
    final EditText nameET= (EditText) v.findViewById(R.id.name);

    cNumber = PhoneNumberUtils.stripSeparators(cNumber);
    String code=cNumber;
    cNumber= removeCountryCode(cNumber,getActivity());
    if (!((code=code.replace(cNumber,""))== null))
    {
        code=code.replace("+","");
        if(!code.equalsIgnoreCase(""))
            spinnerPosition = CodeAdapter.getPosition(Integer.parseInt(code));
    }
    switch (reqCode)
    {
        case (REAL_CONTACT_PICKER_RESULT):
            real.setText(cNumber);
            realCode.setSelection(spinnerPosition);
            break;
        case (FAKE_CONTACT_PICKER_RESULT):
            id=idOnResult;
            fakeCode.setSelection(spinnerPosition);
            fake.setText(cNumber);
            nameET.setText(nameS);
            break;
    }
}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        v = inflater.inflate(R.layout.fragment_add, container, false);
        ImageButton pickReal = (ImageButton) v.findViewById(R.id.pick_real);
        final ImageButton pickFake = (ImageButton) v.findViewById(R.id.pick_fake);
        final ToggleButton tb = (ToggleButton) v.findViewById(R.id.toggle_button);
        real = (AutoCompleteTextView) v.findViewById(R.id.real);
        fake = (AutoCompleteTextView) v.findViewById(R.id.fake);
        name = (AutoCompleteTextView) v.findViewById(R.id.name);
        realCode = (Spinner) v.findViewById(R.id.realcode);
        fakeCode = (Spinner) v.findViewById(R.id.fakecode);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        final TextView sensitivity = (TextView) v.findViewById(R.id.sensitivityTV);
        List<Integer> x = getCountryList(getActivity());
//        new CountryLoader().start();
       /* setToArrayAdapter();
        real.setAdapter(phoneAdapter);
        fake.setAdapter(phoneAdapter);
        name.setAdapter(nameAdapter);*/
        //addMyListener(real);
        //addMyListener(fake);
        //addMyListener(name);
        CodeAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, x);
        CodeAdapter.setDropDownViewResource(R.layout.spinner_item);
        realCode.setAdapter(CodeAdapter);
        fakeCode.setAdapter(CodeAdapter);
        int spinnerPosition = CodeAdapter.getPosition(getCountryZipCode());
        realCode.setSelection(spinnerPosition);
        fakeCode.setSelection(spinnerPosition);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        {
            real.setHint(Html.fromHtml("<small>" + real.getHint().toString() + "</small>",Html.FROM_HTML_MODE_COMPACT));
            fake.setHint(Html.fromHtml("<small>" + fake.getHint().toString() + "</small>",Html.FROM_HTML_MODE_COMPACT));
            name.setHint(Html.fromHtml("<small>" + name.getHint().toString() + "</small>",Html.FROM_HTML_MODE_COMPACT));
        }
        else
        {
            real.setHint(Html.fromHtml("<small>" + real.getHint().toString() + "</small>"));
            fake.setHint(Html.fromHtml("<small>" + fake.getHint().toString() + "</small>"));
            name.setHint(Html.fromHtml("<small>" + name.getHint().toString() + "</small>"));
        }
        MainActivity.addListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                databaseAdd();
                if (clear) {
                    real.setText("");
                    fake.setText("");
                    name.setText("");
                }
                SavedDataFragment.getDetails(getActivity());
                SavedDataFragment.mAdapter.notifyDataSetChanged();
            }
        };

/*
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED){*/
        pickFake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLaunchContactPicker(FAKE_CONTACT_PICKER_RESULT);
            }
        });

        pickReal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLaunchContactPicker(REAL_CONTACT_PICKER_RESULT);
            }
        });
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        tb.setChecked(prefs.getBoolean("Enabled", false));
        tb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               // AddFragment.isChecked = isChecked;
                SharedPreferences.Editor edtr = prefs.edit();
                edtr.putBoolean("Enabled", isChecked);
                edtr.apply();
            }
        });
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        final SeekBar seekBar = (SeekBar) v.findViewById(R.id.seekBar);
        seekBar.setProgress(prefs.getInt("sensitivity", OutCallReceiver.DEFAULT_DELAY));
        seekBar.setMax(OutCallReceiver.MAX_DELAY);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                SharedPreferences.Editor edtr = prefs.edit();
                edtr.putInt("sensitivity", progress);
                edtr.apply();
                sensitivity.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar2) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar2) {
                SharedPreferences.Editor edtr = prefs.edit();
                edtr.putInt("sensitivity", seekBar.getProgress());
                edtr.apply();
                sensitivity.setText(getResources().getString(R.string.sensitivity));
            }
        });
        return v;
    }


/*
    private class CountryLoader extends Thread
    {
        public CountryLoader()
        {

        }
        public void run()
        {
           x = getCountryList(getActivity());
        }
    }*/
    /*
    private void addMyListener(final AutoCompleteTextView textView) {
        if (nameAdapter!=null&&phoneAdapter!=null)
            textView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    String s = textView.getText().toString();
                    String nameT;
                    if (!textView.equals(name) && s.indexOf(',') > 0) {
                        s = s.substring(0, s.indexOf(','));
                        s = PhoneNumberUtils.stripSeparators(s);
                        nameT = (nameAdapter.getItem(phoneAdapter.getPosition(s)));
                        s = removeCountryCode(s, getActivity());

                        if (textView.equals(real))
                        {
                            real.setText(s);
                        } else if (textView.equals(fake))
                        {
                            fake.setText(s);
                            name.setText(nameT);
                        }
                    } else if (textView.equals(name) && s.indexOf(',') > 0) {
                        String phno = s;
                        s = s.substring(0, s.indexOf(','));
                        name.setText(s);
                        phno = phno.replace(s, "");
                        phno = phno.replace(",", "");
                        phno = removeCountryCode(phno, getActivity());
                        fake.setText(phno);
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {
                }
            });
    }*/
}
