package com.lakshparnami.callingpapa2.recyclers;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lakshparnami.callingpapa2.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class ContactLogRecyclerAdapter extends RecyclerView.Adapter<ContactLogRecyclerAdapter.ViewHolder> {
    private final ArrayList<ContactLogData> itemsData;

    public ContactLogRecyclerAdapter() {
        this.itemsData = com.lakshparnami.callingpapa2.activities.Contact.itemsData;
    }

    // Create new views (invoked by the layout_recycler manager)
    @Override
    public ContactLogRecyclerAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        // create a new view
        final View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_log_contact, parent, false);
        return new ViewHolder(itemLayoutView);
    }

    // Replace the contents of a view (invoked by the layout_recycler manager)
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {




        // - get data from your itemsData at this position
        // - replace the contents of the view with that itemsData
        viewHolder.duration.setText(itemsData.get(position).getDuration());
        viewHolder.name.setText(itemsData.get(position).getName());
        SimpleDateFormat f= new SimpleDateFormat("dd/MM/yyyy , hh:mm:ss", Locale.US);
        try {
            Date d =  f.parse(itemsData.get(position).getTime());
            DateFormat date = new SimpleDateFormat("MM/dd/yyyy",Locale.US);
            DateFormat time = new SimpleDateFormat("hh:mm:ss",Locale.US);
            viewHolder.time.setText(time.format(d));
            viewHolder.date.setText(date.format(d));
        } catch (ParseException e) {
        e.printStackTrace();
    }
        /*viewHolder.del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    final String s = viewHolder.real.getText().toString();
                    customToast("Deleted", ContactLogData.getActivity());
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            DetailsDatabaseHandler dbh = new DetailsDatabaseHandler(context);
                            dbh.Open();
                            dbh.deleteData(s);
                            dbh.Close();
                            itemsData.remove(position);
                            SavedDataFragment.mAdapter.notifyDataSetChanged();
                        }
                    }, 30);
                } catch (Exception e) {
                    Log.e("error", "here");
                }

            }
        });*/

     }

    // inner class to hold a reference to each item of RecyclerView
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView date;
        public final TextView time;
        public final TextView duration;
        public final TextView name;
//        public final RelativeLayout listItemLayout;

        public ViewHolder(final View itemLayoutView) {
            super(itemLayoutView);
            time = (TextView) itemLayoutView.findViewById(R.id.time);
            date = (TextView) itemLayoutView.findViewById(R.id.date);
            duration = (TextView) itemLayoutView.findViewById(R.id.duration);
            name = (TextView) itemLayoutView.findViewById(R.id.nametxt);
            
            /*
            del = (ImageButton) itemLayoutView.findViewById(R.id.delC);
            edit = (ImageButton) itemLayoutView.findViewById(R.id.callC);
            */

//            listItemLayout = (RelativeLayout) itemLayoutView.findViewById(R.id.call_log_item);
        }

    }


    // Return the size of your itemsData (invoked by the layout_recycler manager)
    @Override
    public int getItemCount() {
        return itemsData.size();
    }

    public static class ContactLogData {




        private final String Time;


        private final String Name;
        private final String Duration;

        public ContactLogData(String time, String duration, String name) {
            Time=time;
            Name=name;
            Duration=duration;
        }

        public String getTime() {
            return Time;
        }

        public String getDuration() {
            return Duration;
        }



        public String getName() {
            return Name;
        }


    }
}