package com.lakshparnami.callingpapa2.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import com.lakshparnami.callingpapa2.R;
import com.lakshparnami.callingpapa2.databasehandlers.DetailsDatabaseHandler;
import com.lakshparnami.callingpapa2.fragments.SavedDataFragment;
import com.lakshparnami.callingpapa2.fragments.SuperFragment;

public class DeleteContactsDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            // Get the layout inflater
        this.setStyle(R.style.OverlayDialog,R.style.AppTheme);
            // Inflate and set the layout for the dialog
        final View v= getActivity().getLayoutInflater().inflate(R.layout.dialog_delete, null);
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(v)
                    // Add action buttons
                    .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            final DetailsDatabaseHandler dbh = new DetailsDatabaseHandler(getActivity().getApplicationContext());
                            dbh.Open();
                            SavedDataFragment.itemsData.clear();
                            SavedDataFragment.mAdapter.notifyDataSetChanged();
                            dbh.deleteAll();
                            SuperFragment.customToast(getActivity().getResources().getString(R.string.data_cleared), getActivity());
                            dbh.Close();
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            DeleteContactsDialog.this.getDialog().cancel();
                        }
                    });
            return builder.create();

    }
}
