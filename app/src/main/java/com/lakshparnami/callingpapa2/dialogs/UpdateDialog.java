package com.lakshparnami.callingpapa2.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.lakshparnami.callingpapa2.R;
import com.lakshparnami.callingpapa2.activities.Contact;
import com.lakshparnami.callingpapa2.databasehandlers.DetailsDatabaseHandler;
import com.lakshparnami.callingpapa2.fragments.AddFragment;
import com.lakshparnami.callingpapa2.fragments.SavedDataFragment;
import com.lakshparnami.callingpapa2.fragments.SuperFragment;

import java.util.ArrayList;
import java.util.List;

public class UpdateDialog extends DialogFragment {
    private TextView real;
    private AutoCompleteTextView fake;
    private AutoCompleteTextView name;
    private String cNumber="";
    private String id;
    private static final int REAL_CONTACT_PICKER_RESULT = 1001;
    private static final int FAKE_CONTACT_PICKER_RESULT = 1002;
    private String nameS="";

    private void doLaunchContactPicker() {
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(contactPickerIntent, FAKE_CONTACT_PICKER_RESULT);
    }

    private String removeCountryCode(String cNumber)
    {
        if (cNumber.startsWith("0"))
        {
            cNumber = cNumber.substring(1);
        }
        if(cNumber.startsWith("+"))
        {
            String[] rl = this.getResources().getStringArray(R.array.CountryCodes);
            for (String aRl : rl) {
                String[] g = aRl.split(",");
                cNumber = cNumber.replace("+"+g[0], "");
            }
        }
        return cNumber;
    }

    private void databaseAdd()
    {
        if (real.getText().toString().isEmpty() || fake.getText().toString().isEmpty() || name.getText().toString().isEmpty())
        {
            SuperFragment.customToast(getActivity().getResources().getString(R.string.please_enter_all),getActivity());
        }
        else {

            cNumber=real.getText().toString();
            cNumber= PhoneNumberUtils.stripSeparators(cNumber);
            if(cNumber.length()>15)
                cNumber=cNumber.substring(cNumber.length() - 15, cNumber.length());
            DetailsDatabaseHandler dbh = new DetailsDatabaseHandler(getActivity().getApplicationContext());
            dbh.Open();
            Cursor c = dbh.returnDetails(cNumber);
            String fakeNumber=fake.getText().toString();
            fakeNumber= PhoneNumberUtils.stripSeparators(fakeNumber);
            if(fakeNumber.length()>15)
                fakeNumber=fakeNumber.substring(fakeNumber.length() - 15, fakeNumber.length());
            if (c.moveToFirst()) {
                dbh.updateData(cNumber, fakeNumber, name.getText().toString(), id);
                getArguments().putString("real", cNumber);
                getArguments().putString("fake", fakeNumber);
                getArguments().putString("name",name.getText().toString());
                getArguments().putString("imageID",id);
            }
            dbh.Close();
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

            // Inflate and set the layout for the dialog
        final View v= inflater.inflate(R.layout.dialog_update, null);
        real= (TextView) v.findViewById(R.id.real);
        fake= (AutoCompleteTextView) v.findViewById(R.id.fake);
        name= (AutoCompleteTextView) v.findViewById(R.id.name);
        Spinner fakeCode = (Spinner) v.findViewById(R.id.fakecode);
        List<Integer> x = SuperFragment.getCountryList(getActivity());
        /*
        fake.setAdapter(AddFragment.phoneAdapter);
        name.setAdapter(AddFragment.nameAdapter);
        addMyListener(fake);
        addMyListener(name);*/
        ArrayAdapter<Integer> codeAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, x);
        codeAdapter.setDropDownViewResource(R.layout.spinner_item);
        fakeCode.setAdapter(codeAdapter);

        real.setText(getArguments().getString("real"));
        fake.setText(SuperFragment.removeCountryCode(getArguments().getString("fake"),getActivity()));
        name.setText(getArguments().getString("name"));
        String code=getArguments().getString("fake");
        if (code != null && !((code=code.replace(fake.getText().toString(),""))== null)&&!((code=code.replace("+","")).equals("")))
        {
            int spinnerPosition = codeAdapter.getPosition(Integer.parseInt(code));
            fakeCode.setSelection(spinnerPosition);
        }
        id=getArguments().getString("imageID");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            fake.setHint(Html.fromHtml("<small><small><small>" + fake.getHint().toString() + "</small></small></small>",Html.FROM_HTML_MODE_COMPACT));
            name.setHint(Html.fromHtml("<small><small><small>" + name.getHint().toString() + "</small></small></small>",Html.FROM_HTML_MODE_COMPACT));
        }
        else {
            fake.setHint(Html.fromHtml("<small><small><small>" + fake.getHint().toString() + "</small></small></small>"));
            name.setHint(Html.fromHtml("<small><small><small>" + name.getHint().toString() + "</small></small></small>"));
        }
            final ImageButton pickFake= (ImageButton) v.findViewById(R.id.pick_fake);
        pickFake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLaunchContactPicker();
            }
        });


            builder.setView(v)
                    // Add action buttons
                    .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            databaseAdd();

                            SavedDataFragment.getDetails(getActivity());
                            SavedDataFragment.mAdapter.notifyDataSetChanged();
                            if(getActivity() instanceof Contact)
                            {
                                ((TextView)getActivity().findViewById(R.id.fake)).setText(getArguments().getString("fake"));
                                ((TextView)getActivity().findViewById(R.id.name)).setText(getArguments().getString("name"));
                            }

                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            UpdateDialog.this.getDialog().cancel();
                        }
                    });
            return builder.create();

    }
    public void onActivityResult(final int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        ArrayList<String> numberList=new ArrayList<>();
        if (resultCode == Activity.RESULT_OK)
        {
            Uri contactData = data.getData();
            final Cursor c = getActivity().getContentResolver().query(contactData, null, null, null, null);
            if(c!=null){
            if (c.moveToFirst()) {
                id =c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                String hasPhone =c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                if (hasPhone.equalsIgnoreCase("1")) {
                    Cursor phones = getActivity().getContentResolver().query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ id,
                            null, null);
                    if(phones!=null)
                    {
                        phones.moveToFirst();
                        cNumber = phones.getString(phones.getColumnIndex("data1"));
                        nameS = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        do
                        {
                            numberList.add(phones.getString(phones.getColumnIndex("data1")));
                        } while (phones.moveToNext());


                        final CharSequence[] items = numberList.toArray(new String[numberList.size()]);
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle(R.string.choose);
                        builder.setItems(items, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item)
                        {
                            cNumber = items[item].toString();
                            cNumber = PhoneNumberUtils.stripSeparators(cNumber);
                            cNumber=cNumber.trim();
                            cNumber= removeCountryCode(cNumber);
                            switch (reqCode)
                            {
                                case (FAKE_CONTACT_PICKER_RESULT) :
                                    fake.setText(cNumber);
                                    name.setText(nameS);
                                    break;
                            }
                        }
                    });
                    AlertDialog alert = builder.create();
                        cNumber=cNumber.trim();
                        cNumber= removeCountryCode(cNumber);
                    if(numberList.size() > 1) {
                        alert.show();
                    } else {
                        cNumber = PhoneNumberUtils.stripSeparators(cNumber);
                        switch (reqCode)
                        {
                            case (REAL_CONTACT_PICKER_RESULT) :
                                real.setText(cNumber);
                                break;
                            case (FAKE_CONTACT_PICKER_RESULT) :
                                fake.setText(cNumber);
                                name.setText(nameS);
                                break;
                        }
                    }
                    phones.close();}
                }
            }

                }

            if (c != null) {
                c.close();
            }
        }
        }

/*
    private void addMyListener(final AutoCompleteTextView textView) {
        textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String s=textView.getText().toString();
                String nameT;
                if (!textView.equals(name) && s.indexOf(',') > 0) {
                    s = s.substring(0, s.indexOf(','));
                    s = PhoneNumberUtils.stripSeparators(s);
                    nameT = (AddFragment.nameAdapter.getItem(AddFragment.phoneAdapter.getPosition(s)));
                    s = removeCountryCode(s);
                    String code = s;
                    if (!((code = code.replace(s, "")) == null)) {
                        code = code.replace("+", "");
                        if (!code.equalsIgnoreCase(""))
                            spinnerPosition = CodeAdapter.getPosition(Integer.parseInt(code));
                    }
                    if (textView.equals(fake)) {
                        fakeCode.setSelection(spinnerPosition);
                        fake.setText(s);
                        name.setText(nameT);
                    }
                }else if (textView.equals(name)) {
                    if (s.indexOf(',') > 0)
                    {
                        String phno=s;
                        s = s.substring(0, s.indexOf(','));
                        name.setText(s);
                        phno=phno.replace(s,"");
                        phno=phno.replace(",","");
                        phno=removeCountryCode(phno);
                        fake.setText(phno);
                        String code = s;
                        if (!((code = code.replace(s, "")) == null))
                        {
                            code = code.replace("+", "");
                            if (!code.equalsIgnoreCase(""))
                                spinnerPosition = CodeAdapter.getPosition(Integer.parseInt(code));
                            fakeCode.setSelection(spinnerPosition);

                        }
                    }
                }
            }
        });

    }
*/
}
