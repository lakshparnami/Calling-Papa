package com.lakshparnami.callingpapa2.fragments;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lakshparnami.callingpapa2.databasehandlers.DetailsDatabaseHandler;
import com.lakshparnami.callingpapa2.R;
import com.lakshparnami.callingpapa2.recyclers.SavedRecyclerAdapter;

import java.util.ArrayList;

public class SavedDataFragment extends Fragment {

    public static final ArrayList<SavedRecyclerAdapter.CallingData> itemsData = new ArrayList<>();
    public static final SavedRecyclerAdapter mAdapter = new SavedRecyclerAdapter();

    public SavedDataFragment() {
        // Required empty public constructor
    }



    public static void getDetails(Activity activity) {
        itemsData.clear();
        Cursor c;
        final DetailsDatabaseHandler dbh = new DetailsDatabaseHandler(activity.getApplicationContext());
        dbh.Open();
        c = dbh.returnData();




        if (c.moveToFirst()) {
                do {
                    itemsData.add(new SavedRecyclerAdapter.CallingData(
                            c.getString(0),
                            c.getString(1),
                            c.getString(2),
                            c.getString(3),
                            activity.getApplicationContext(), activity));
                } while (c.moveToNext());
        }
        dbh.Close();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_saved_data, container, false);
        itemsData.clear();
        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        GridLayoutManager glm = new GridLayoutManager(getActivity().getApplicationContext(), 2);
        recyclerView.setLayoutManager(glm);
        DefaultItemAnimator animator=new DefaultItemAnimator();
        animator.setAddDuration(500);
        animator.setRemoveDuration(500);
        recyclerView.setItemAnimator(animator);
        recyclerView.setAdapter(mAdapter);
        getDetails(getActivity());
       /*if(itemsData.size()==0){
            itemsData.add(new CallingData("", "", "", "", getActivity().getApplicationContext(), getActivity(), true));
        }*/
        mAdapter.notifyDataSetChanged();
        return v;
    }


}
