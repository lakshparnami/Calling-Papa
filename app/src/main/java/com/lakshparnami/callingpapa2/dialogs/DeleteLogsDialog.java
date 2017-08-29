package com.lakshparnami.callingpapa2.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.lakshparnami.callingpapa2.R;
import com.lakshparnami.callingpapa2.databasehandlers.LogsDatabaseHandler;
import com.lakshparnami.callingpapa2.fragments.LogsFragment;
import com.lakshparnami.callingpapa2.fragments.SuperFragment;

public class DeleteLogsDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            // Get the layout inflater

            // Inflate and set the layout for the dialog
        final View v= getActivity().getLayoutInflater().inflate(R.layout.dialog_delete, null);
            // Pass null as the parent view because its going in the dialog layout
        TextView are= (TextView) v.findViewById(R.id.areyousure);
        are.setText(R.string.rusurelogs);
            builder.setView(v)
                    // Add action buttons
                    .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            final LogsDatabaseHandler dbh = new LogsDatabaseHandler(getActivity().getApplicationContext());
                            dbh.Open();
                            LogsFragment.itemsData.clear();
                            LogsFragment.mAdapter.notifyDataSetChanged();
                            dbh.deleteAll();
                            SuperFragment.customToast(getActivity().getResources().getString(R.string.logs_cleared), getActivity());
                            dbh.Close();
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            DeleteLogsDialog.this.getDialog().cancel();
                        }
                    });
            return builder.create();

    }
}
