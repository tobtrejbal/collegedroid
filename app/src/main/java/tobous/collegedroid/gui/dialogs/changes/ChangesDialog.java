package tobous.collegedroid.gui.dialogs.changes;

import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import tobous.collegedroid.R;
import tobous.collegedroid.functions.changes.encapsulation.Change;


/**
 * Created by osmola on 1.2.16.
 */
public class ChangesDialog extends DialogFragment {

    private TextView txtName;
    private TextView txtAuthor;
    private TextView txtStartDate;
    private TextView txtEndDate;
    private TextView txtDescription;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the current date as the default date in the picker

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_changes_detail, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setView(dialogView);

        Button btnOK = (Button) dialogView.findViewById(R.id.changes_detail_btn_ok);

        final Dialog dialog = builder.create();

        dialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, 1200);

        txtName = (TextView) dialogView.findViewById(R.id.changes_name);
        txtAuthor = (TextView) dialogView.findViewById(R.id.changes_author);
        txtStartDate = (TextView) dialogView.findViewById(R.id.changes_start_date);
        txtEndDate = (TextView) dialogView.findViewById(R.id.changes_end_date);
        txtDescription = (TextView) dialogView.findViewById(R.id.changes_decription);

        Change change = (Change) getArguments().getSerializable("change");

        txtName.setText(change.getName());
        txtAuthor.setText(change.getAuthor());
        txtStartDate.setText(change.getStartDate());
        txtEndDate.setText(change.getEndDate());
        txtDescription.setText(change.getReason());

        // if button is clicked, close the custom dialog
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        return dialog;
    }
    }
