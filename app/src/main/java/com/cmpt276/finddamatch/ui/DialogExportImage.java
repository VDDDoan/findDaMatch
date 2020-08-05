package com.cmpt276.finddamatch.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.DialogFragment;

import com.cmpt276.finddamatch.R;

public class DialogExportImage  extends DialogFragment {

    private Button yesButton;
    private Button noButton;

    public interface ExportDialogListener{
        void onFinishExportDialog(boolean flag);
    }

    private ExportDialogListener listener;
    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        listener = (ExportDialogListener) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.dialog_export_images,container,false);
        yesButton = view.findViewById(R.id.yes_button);
        yesButton.setOnClickListener(v->{
            listener.onFinishExportDialog(true);
            DialogExportImage.this.dismiss();
        });
        noButton = view.findViewById(R.id.no_button);
        noButton.setOnClickListener(v->{
            listener.onFinishExportDialog(false);
            DialogExportImage.this.dismiss();
        });
        return view;
    }
}
