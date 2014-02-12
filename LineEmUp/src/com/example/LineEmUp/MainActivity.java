/**
  Application created by:
	- Mario Marchal-Goulet
	- Jill Perreault
	- Celine Bensoussan 
	
	The application was created in Winter of 2013 for a Software Engineering class taught by Lin Jensen at Bishop's University
 */

package com.example.LineEmUp;

import com.example.LineEmUp.R;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/*
-	First object created when the application is initiated.
-	Displays the relevant buttons which when selected initiate subsequent activities. 
-	Implements the activity_main layout
*/
public class MainActivity extends Activity {

	// Class attributes
	private Button play = null;
	private Button help = null;
	private Button about = null;
	private Button quit = null;
	
	//Initialization of MainActivity
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.activity_main);
		
		//Link class attributes to layout elements
		play = (Button)findViewById(R.id.play);
		help = (Button)findViewById(R.id.help);
		about = (Button)findViewById(R.id.about);
		quit = (Button)findViewById(R.id.exit);
		
		//On clicking the play (Let's play) button
		play.setOnClickListener(new OnClickListener() {	
	        @Override
	        public void onClick(View v) {;
	        	Intent intent = new Intent(MainActivity.this, GridActivity.class);
	        	startActivity(intent);
	        }
	    });
		
		//On clicking the help (How to play) button
		help.setOnClickListener(new OnClickListener() {	
	        @Override
	        public void onClick(View v) {;
	        	Intent intent = new Intent(MainActivity.this, HelpActivity.class);
	        	startActivity(intent);
	        }
	    });
		
		//On clicking the about button
		about.setOnClickListener(new OnClickListener() {
	        @Override
	        public void onClick(View v) {;
	        	Intent intent = new Intent(MainActivity.this, AboutActivity.class);
	        	startActivity(intent);
	        }
	    });
		
		//On clicking the quit button
		quit.setOnClickListener(new OnClickListener() {	
	        @Override
	        public void onClick(View v) {;
	        finish();
            System.exit(0);
	        }
	    });
	}
}
