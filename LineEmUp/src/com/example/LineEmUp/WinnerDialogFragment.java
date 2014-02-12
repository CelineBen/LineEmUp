package com.example.LineEmUp;

import com.example.LineEmUp.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;



public class WinnerDialogFragment extends DialogFragment {

	private int winner;
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        
		this.setCancelable(false);
		
		// Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),  AlertDialog.THEME_HOLO_DARK);
        
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View temp;
     	switch(winner){
	    	case 1: temp = inflater.inflate(R.layout.token1_winner, null); break;
	    	case 2: temp = inflater.inflate(R.layout.token2_winner, null); break;
	    	case 3: temp = inflater.inflate(R.layout.tie_winner, null); break;
	    	default: temp = inflater.inflate(R.layout.tie_winner, null); break;
     	}
     	builder.setView(temp);
        
     	// add "close" button to pop-up
        builder.setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
        	public void onClick(DialogInterface dialog, int id) {}
        	// it only closes the pop-up
        });
        
        // Create the AlertDialog object and return it
        AlertDialog dialog = builder.create();
        	return dialog;
	}
    
	// Set winner to num. (1,2 or 3)
    public void setWinner(int num){
    	winner = num;
    }
}