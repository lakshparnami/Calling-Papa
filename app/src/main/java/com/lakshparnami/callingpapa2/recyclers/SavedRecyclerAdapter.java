package com.lakshparnami.callingpapa2.recyclers;

import android.Manifest;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lakshparnami.callingpapa2.activities.Contact;
import com.lakshparnami.callingpapa2.dialogs.UpdateDialog;
import com.lakshparnami.callingpapa2.databasehandlers.DetailsDatabaseHandler;
import com.lakshparnami.callingpapa2.R;
import com.lakshparnami.callingpapa2.fragments.SavedDataFragment;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


public class SavedRecyclerAdapter extends RecyclerView.Adapter<SavedRecyclerAdapter.ViewHolder> {
    private final ArrayList<CallingData> itemsData;

    public SavedRecyclerAdapter() {
        this.itemsData = SavedDataFragment.itemsData;
    }

    // Create new views (invoked by the layout_recycler manager)
    @Override
    public SavedRecyclerAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        // create a new view
        final View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item_saved, parent, false);
        return new ViewHolder(itemLayoutView);
    }

    // Replace the contents of a view (invoked by the layout_recycler manager)
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        final Context context=itemsData.get(position).getContext();
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED)
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
        }



        // - get data from your itemsData at this position
        // - replace the contents of the view with that itemsData
        viewHolder.name.setText(itemsData.get(position).getName());
        viewHolder.real.setText(itemsData.get(position).getRealNum());
        viewHolder.fake.setText(itemsData.get(position).getFakeNum());
        viewHolder.del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    final String s = viewHolder.real.getText().toString();
                            DetailsDatabaseHandler dbh = new DetailsDatabaseHandler(context);
                            dbh.Open();
                            dbh.deleteData(s);
                            dbh.Close();
                    customToast(itemsData.get(position).getActivity(),itemsData.get(position).getActivity().getResources().getString(R.string.deleted));
                            SavedDataFragment.mAdapter.notifyItemRemoved(position);
                            itemsData.remove(position);
                            notifyItemRangeChanged(position, itemsData.size());
                }
                catch (Exception e) {
                    Log.e("error", "here");
                }

            }
        });
        viewHolder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogFragment dialogFragment = new UpdateDialog();
                Bundle args = new Bundle();
                args.putString("real", "" + viewHolder.real.getText().toString());
                args.putString("fake", "" + viewHolder.fake.getText().toString());
                args.putString("name", "" + viewHolder.name.getText().toString());
                args.putString("imageID", "" + itemsData.get(position).getImageID());
                dialogFragment.setArguments(args);
                dialogFragment.show(itemsData.get(position).getActivity().getFragmentManager(), "Name");
            }
        });
        viewHolder.contactImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(itemsData.get(position).getContext(), Contact.class);

// Pass data object in the bundle and populate details activity.
//                i.putExtra(DetailsActivity.EXTRA_CONTACT, contact);
   /*             ActivityOptionsCompat options =
    ActivityOptionsCompat.makeSceneTransitionAnimation(itemsData.get(position).getActivity(), (View)viewHolder.contactImage, "pic");
*/
                //                startActivity(intent, options.toBundle());
                Pair<View, String> p1 = Pair.create((View)viewHolder.contactImage, "pic");
                /*Pair<View, String> p2 = Pair.create((View)viewHolder.fake, "fake");
                Pair<View, String> p3 = Pair.create((View)viewHolder.name, "name");
                Pair<View, String> p4 = Pair.create((View)viewHolder.real, "real");*/
                Pair<View, String> p5 = Pair.create((View)viewHolder.call, "call");
                Pair<View, String> p6 = Pair.create((View)viewHolder.edit, "fab");
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(itemsData.get(position).getActivity(), p1,p5,p6);
//                    itemsData.get(position).getActivity().startActivity(i, options.toBundle());

                Bundle args = new Bundle();
                args.putString("real", "" + viewHolder.real.getText().toString());
                args.putString("fake", "" + viewHolder.fake.getText().toString());
                args.putString("name", "" + viewHolder.name.getText().toString());
                args.putString("status", "Saved");
                args.putString("imageID", "" + itemsData.get(position).getImageID());
                i.putExtras(args);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    itemsData.get(position).getActivity().startActivity(i, options.toBundle());
                }else{
                    itemsData.get(position).getActivity().startActivity(i);
                     itemsData.get(position).getActivity().overridePendingTransition(R.anim.enter_contact, R.anim.nothing);
                }
            }
        });

        viewHolder.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String s = viewHolder.real.getText().toString();
                final Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + s));
                callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(callIntent);

            }
        });
       /* if (itemsData.get(position).getNoData())
        {
            viewHolder.listItemLayout.removeAllViews();
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = vi.inflate(R.layout.no_data_textview, null);
            viewHolder.listItemLayout.addView(v,0,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            viewHolder.listItemLayout.setBackgroundColor(Color.parseColor("#00000000"));
            viewHolder.listItemLayout.bringChildToFront(v);
        }*/

     }

    private void customToast(Activity activity, String Text)
    {
        LayoutInflater inflater=activity.getLayoutInflater();
        View layout=inflater.inflate(R.layout.toast_red,(ViewGroup)activity.findViewById(R.id.toast_layout_red));
        TextView text=(TextView)layout.findViewById(R.id.toast_textView);
        text.setText(Text);
        Toast toast=new Toast(activity.getApplicationContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, -100);
        toast.setView(layout);
        toast.show();
    }


    // inner class to hold a reference to each item of RecyclerView
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView fake;
        public final TextView real;
        public final TextView name;
        public final ImageButton del;
        public final ImageButton edit;
        public final ImageButton call;
        public final ImageView contactImage;

        public ViewHolder(final View itemLayoutView) {
            super(itemLayoutView);
            name = (TextView) itemLayoutView.findViewById(R.id.nametxt);
            real = (TextView) itemLayoutView.findViewById(R.id.realtxt);
            fake = (TextView) itemLayoutView.findViewById(R.id.faketxt);
            del = (ImageButton) itemLayoutView.findViewById(R.id.delC);
            edit = (ImageButton) itemLayoutView.findViewById(R.id.editC);
            call = (ImageButton) itemLayoutView.findViewById(R.id.callC);
            contactImage = (ImageView) itemLayoutView.findViewById(R.id.contactImage);
        }

    }


    // Return the size of your itemsData (invoked by the layout_recycler manager)
    @Override
    public int getItemCount() {
        return itemsData.size();
    }

    public static class CallingData {



        private final Context context;
        private final String RealNum;
        private final String FakeNum;
        private final String Name;
        private final Activity activity;
        private final String imageID;

        public CallingData(String RealNum,String FakeNum,String Name,String imageID,Context context,Activity activity) {
            this.RealNum=RealNum;
            this.FakeNum=FakeNum;
            this.Name=Name;
            this.imageID=imageID;
            this.context=context;
            this.activity=activity;
        }

        public  Activity getActivity() {
            return activity;
        }

        public  Context getContext() {
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

        public String getImageID() {
            return imageID;
        }
    }
}