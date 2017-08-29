package com.lakshparnami.callingpapa2.recyclers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lakshparnami.callingpapa2.R;
import com.lakshparnami.callingpapa2.activities.Contact;

import java.util.ArrayList;


public class LogRecyclerAdapter extends RecyclerView.Adapter<LogRecyclerAdapter.ViewHolder> {
    private final ArrayList<LogData> itemsData;

    public LogRecyclerAdapter() {
        this.itemsData = com.lakshparnami.callingpapa2.fragments.LogsFragment.itemsData;
    }

    // Create new views (invoked by the layout_recycler manager)
    @Override
    public LogRecyclerAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        // create a new view
        final View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_log, parent, false);

        return new ViewHolder(itemLayoutView);
    }

    // Replace the contents of a view (invoked by the layout_recycler manager)
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        final Context context=LogData.getContext();
//        new LoadImage().execute();
        if(itemsData.get(position).getPhoto()!=null) {
            Bitmap photo = BitmapFactory.decodeByteArray(itemsData.get(position).getPhoto(), 0, itemsData.get(position).getPhoto().length);
            viewHolder.contactImage.setImageBitmap(photo);
        }
/*
        try {
            if (!itemsData.get(position).getImageID().equals("")) {
                InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(
                        context.getContentResolver(),
                        ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI,
                                Long.valueOf(itemsData.get(position).getImageID())),false);

                Bitmap photo = BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.pic);

                if (inputStream != null) {
                    photo = BitmapFactory.decodeStream(inputStream);
                }
                if (inputStream != null) {
                    viewHolder.contactImage.setImageBitmap(photo);
                    inputStream.close();
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        viewHolder.contactImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i=new Intent(context, Contact.class);
                Bundle args = new Bundle();
                args.putString("real", "" + viewHolder.real.getText().toString());
                args.putString("fake", "" + viewHolder.fake.getText().toString());
                args.putString("name", "" + viewHolder.name.getText().toString());
                args.putString("status", "calllog");
                args.putString("imageID", "" + itemsData.get(position).getImageID());
               // ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(LogData.getActivity(), viewHolder.contactImage, "pic");
                Pair<View, String> p1 = Pair.create((View)viewHolder.contactImage, "pic");
                /*Pair<View, String> p2 = Pair.create((View)viewHolder.fake, "fake");
                Pair<View, String> p3 = Pair.create((View)viewHolder.real, "real");*/
                Pair<View, String> p4 = Pair.create((View)viewHolder.name, "name");

                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(LogData.getActivity(), p1,p4);

                i.putExtras(args);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    LogData.getActivity().startActivity(i, options.toBundle());
                }else{
                    LogData.getActivity().startActivity(i);
                    LogData.getActivity().overridePendingTransition(R.anim.enter_contact, R.anim.nothing);
                }
            }
        });


        // - get data from your itemsData at this position
        // - replace the contents of the view with that itemsData
        viewHolder.name.setText(itemsData.get(position).getName());
        viewHolder.real.setText(itemsData.get(position).getRealNum());
        viewHolder.fake.setText(itemsData.get(position).getFakeNum());
        viewHolder.time.setText(itemsData.get(position).getTime());
        viewHolder.duration.setText(itemsData.get(position).getDuration());
        /*viewHolder.del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    final String s = viewHolder.real.getText().toString();
                    customToast("Deleted", LogData.getActivity());
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

        public final TextView fake;
        public final TextView real;
        public final TextView name;
        public final TextView time;
        public final TextView duration;
        public final ImageView contactImage;

        public ViewHolder(final View itemLayoutView) {
            super(itemLayoutView);
            name = (TextView) itemLayoutView.findViewById(R.id.nametxt);
            real = (TextView) itemLayoutView.findViewById(R.id.realtxt);
            fake = (TextView) itemLayoutView.findViewById(R.id.faketxt);
            time = (TextView) itemLayoutView.findViewById(R.id.time);
            duration = (TextView) itemLayoutView.findViewById(R.id.duration);
            
            /*
            del = (ImageButton) itemLayoutView.findViewById(R.id.delC);
            edit = (ImageButton) itemLayoutView.findViewById(R.id.callC);
            */
            
            contactImage = (ImageView) itemLayoutView.findViewById(R.id.contactImage);

        }

    }


    // Return the size of your itemsData (invoked by the layout_recycler manager)
    @Override
    public int getItemCount() {
        return itemsData.size();
    }

    public static class LogData {



        public static Context context;
        private final String RealNum;
        private final String FakeNum;
        private final String Name;
        private final String Time;
        private final String Duration;
        public static Activity activity;
        private final String imageID;

        private final byte[] photo;

        public LogData(String RealNum, String FakeNum, String Name, String imageID,String time, String duration,byte[] photo, Context context, Activity activity) {
            this.RealNum=RealNum;
            this.FakeNum=FakeNum;
            this.Name=Name;
            this.photo=photo;
            this.imageID=imageID;
            Time=time;
            Duration=duration;
            LogData.context=context;
            LogData.activity=activity;
        }

        public String getTime() {
            return Time;
        }

        public String getDuration() {
            return Duration;
        }

        public static Activity getActivity() {
            return activity;
        }

        public static Context getContext() {
            return context;
        }


        public String getRealNum() {
            return RealNum;
        }


        public String getFakeNum() {
            return FakeNum;
        }


        public String getName() {
            return Name;
        }


        public byte[] getPhoto() {
            return photo;
        }

        public String getImageID() {
            return imageID;
        }
    }
/*
    public class LoadImage extends AsyncTask<Void ,Void ,Bitmap>{

        Map<String,Bitmap> photos=new HashMap<>();
        Set<String> numbers=new HashSet<>();
        @Override
        protected void onPostExecute(Bitmap photo) {
            if (photo!=null)
            viewHolder.contactImage.setImageBitmap(photo);
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            Bitmap photo;
            if(numbers.contains(itemsData.get(position).getRealNum()))
            {
                return photos.get(itemsData.get(position).getRealNum());
            }
            else
            {
                if(itemsData.get(position).getPhoto().length!=0&&itemsData.get(position).getPhoto()!=null)
                {
                    photo =BitmapFactory.decodeByteArray(itemsData.get(position).getPhoto(),0,itemsData.get(position).getPhoto().length);
                    photos.put(itemsData.get(position).getRealNum(),photo);
                    numbers.add(itemsData.get(position).getRealNum());
                    return  photo;
                }

            }
            return null;
        }
    }
*/

}