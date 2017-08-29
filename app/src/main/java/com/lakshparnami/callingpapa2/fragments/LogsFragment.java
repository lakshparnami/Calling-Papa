package com.lakshparnami.callingpapa2.fragments;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lakshparnami.callingpapa2.R;
import com.lakshparnami.callingpapa2.databasehandlers.LogsDatabaseHandler;
import com.lakshparnami.callingpapa2.recyclers.LogRecyclerAdapter;

import java.util.ArrayList;
import java.util.Collections;

public class LogsFragment extends Fragment {

    public static final ArrayList<LogRecyclerAdapter.LogData> itemsData = new ArrayList<>();
    public static final LogRecyclerAdapter mAdapter = new LogRecyclerAdapter();

    public LogsFragment() {
        // Required empty public constructor
    }

    private static void getDetails(Activity activity) {
        itemsData.clear();
        Cursor c;
        final LogsDatabaseHandler dbh = new LogsDatabaseHandler(activity.getApplicationContext());
        dbh.Open();
        c = dbh.returnData();
        if (c.moveToFirst()) {
            do {
                itemsData.add(new LogRecyclerAdapter.LogData(
                        c.getString(0),
                        c.getString(1),
                        c.getString(2),
                        c.getString(3),
                        c.getString(4),
                        c.getString(5),
                        c.getBlob(6),
                        activity.getApplicationContext(), activity));
            } while (c.moveToNext());
        }
        dbh.Close();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_logs, container, false);
        itemsData.clear();
        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(mAdapter);
        getDetails(getActivity());
        Collections.reverse(itemsData);
        mAdapter.notifyDataSetChanged();
        return v;
    }


}
