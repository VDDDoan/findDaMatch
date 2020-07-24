package com.cmpt276.finddamatch.ui;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.cmpt276.finddamatch.R;

public class DialogFlickrSearch extends DialogFragment {

    private EditText searchText;
    private String searchWord;
    private Button cancelButton;
    private Button searchButton;

    private SearchDialogListener listener;

    public interface SearchDialogListener{
        void onFinishSearchDialog(String inputText, boolean newSearch);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.dialog_search_flickr,container,false);

        searchText = view.findViewById(R.id.search_text);
        searchText.addTextChangedListener(new TextWatcher(){

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchWord = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        cancelButton = view.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(v->{
            getActivity().onBackPressed();
        });
        searchButton = view.findViewById(R.id.search_button);
        searchButton.setOnClickListener(v->{
            if(!errorFunc()){
                listener.onFinishSearchDialog(searchWord, true);
                this.dismiss();
            }
        });
        return view;
    }

    boolean errorFunc(){
        String errorMessage = "Error : String cannot be empty.";
        boolean error = false;
        if (searchWord == null || searchWord.isEmpty()){
            error = true;
            Toast.makeText(getContext(),errorMessage, Toast.LENGTH_LONG).show();
        }
        return error;
    }

}
