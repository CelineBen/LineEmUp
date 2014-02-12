package com.example.LineEmUp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import com.example.LineEmUp.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/*
-	Initiated when the Help button is selected in the MainActivity.
-	Displays the text from the help.txt resource and a Back button. 
-	Implements the activity_help layout.
 */
public class HelpActivity extends Activity {
	
	//Class attributes
	private Button back = null;
	private TextView help = null;

	////Initialization of HelpActivity
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
		
		//Link class attributes to layout elements
		back = (Button)findViewById(R.id.back1);
		help = (TextView)findViewById(R.id.rules);
		
		//Set text of TextView by reading a file
		help.setText(readTxt(R.raw.help));
		
		//On clicking the back button
		back.setOnClickListener(new OnClickListener() {

	        @Override
	        public void onClick(View v) {
	        	finish();
	        }
	    });
	}
	
	//Reads text from a text file in the res/raw folder
	public String readTxt(int id){

		InputStream inputStream = getResources().openRawResource(id);
		BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
	    String readLine = null;
	    String out = "";

	    try {
	        while ((readLine = br.readLine()) != null) {
	        	out = out + "\n" + readLine;
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }	    
	    return out;
	}
}
