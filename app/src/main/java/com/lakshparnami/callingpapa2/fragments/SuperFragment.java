package com.lakshparnami.callingpapa2.fragments;

import android.app.Activity;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.lakshparnami.callingpapa2.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SuperFragment extends Fragment {
    public static List<Integer> getCountryList(Activity activity) {
        String[] rl=activity.getResources().getStringArray(R.array.CountryCodes);
        Integer[] codes=new Integer[rl.length];
        for(int i=0;i<rl.length;i++)
        {
            String[] g = rl[i].split(",");
            codes[i]=Integer.parseInt(g[0]);
        }
        Set<Integer> xx=new HashSet<>();
        xx.addAll(Arrays.asList(codes));
        xx.add(0);
        List<Integer> x=new ArrayList<>(xx);
        Collections.sort(x);
        return x;
    }

    public static String removeCountryCode(String cNumber,Activity activity)
    {
        if (cNumber.startsWith("0"))
        {
            cNumber = cNumber.substring(1);
        }
        if(cNumber.startsWith("+"))
        {
            String[] rl = activity.getResources().getStringArray(R.array.CountryCodes);
            for (String aRl : rl) {
                String[] g = aRl.split(",");
                cNumber = cNumber.replace("+"+g[0], "");
            }
        }
        return cNumber;
    }
     public  static void customToast(String Message,Activity activity)
    {
        LayoutInflater inflater=activity.getLayoutInflater();
        View layout=inflater.inflate(R.layout.toast_red,(ViewGroup)activity.findViewById(R.id.toast_layout_red));
        TextView text=(TextView)layout.findViewById(R.id.toast_textView);
        text.setText(Message);
        final Toast toast=new Toast(activity.getApplicationContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.setView(layout);
        toast.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.cancel();
            }
        }, 700);
    }
}
