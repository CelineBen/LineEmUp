package com.example.LineEmUp;

import com.example.LineEmUp.R;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.Button;

/*
-	Initiated when the “play” button is selected in the MainActivity.
-	Displays the GameSurfaceView object, two score buttons, and a “reset”, “restart” and “help” button. 
-	Implements the activity_grid layout
*/
public class GridActivity extends Activity {

	private static final String TAG = GridActivity.class.getSimpleName();	
	private android.app.FragmentManager fragmentManager = getFragmentManager();
	private int score1 = 0;
	private int score2 = 0;
	protected GameSurfaceView gamePointer;
	protected Button player_one = null;
	protected Button player_two = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {    	
		Log.d(TAG, "onCreate of GridActivity");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_grid);
		gamePointer = (GameSurfaceView) findViewById(R.id.GameSurfaceView1);
		player_one = (Button)findViewById(R.id.point_1);
		player_two = (Button)findViewById(R.id.point_2);
		player_one.setText(""+ score1);
		player_two.setText(""+ score2);
		Log.d(TAG, "View added");        
	}

	// Called when the user clicks the "Reset" button 
	public void resetAction(View view) {
		this.reset();		
	}
	
	// Called when the user clicks the "New Game" button
	public void newGame(View view) {
		this.restart();		
	}

	// Called when the user clicks the "How To Play" button 
	public void helpAction(View view) {	
		Intent intent = new Intent(GridActivity.this, HelpActivity.class);
		startActivity(intent);
	}  

	// Labels used when saving application data
	private static final String GRID_STATE = "gridstate";
	private static final String PLAYER1 = "player1";
	private static final String PLAYER2 = "player2";
	private static final String COLUMNFILL = "columnfill";
	private static final String CURRENTPLAYER = "currentplayer";
	private static final String WINNER = "winner";


	protected void onPause(){
		Log.d(TAG, "onPause called...");
		super.onPause();
		gamePointer.thread.setRunning(false);

		/** Saving all required application data **/		
		SharedPreferences storageFile = this.getPreferences(Context.MODE_PRIVATE);  	  	
		SharedPreferences.Editor editor = storageFile.edit(); 
		//loop until the grid is actually created... useful on exit after win, gives time to reset
		while( !(this.gamePointer.created) ){                		   
		}	//empty loop
		//Current State
		int[] temp = gamePointer.theGrid.getCurrentState();
		for (int i = 0; i < temp.length; i++){
			editor.putInt((GRID_STATE + i), temp[i]);  	  		
		}  	
		// player1 State
		int[] temp1 = gamePointer.theGrid.getPlayer1Grid();
		for (int i = 0; i < temp1.length; i++){
			editor.putInt((PLAYER1 + i), temp1[i]);  	  		
		}  	
		// player2 State
		int[] temp2 = gamePointer.theGrid.getPlayer2Grid();
		for (int i = 0; i < temp2.length; i++){  	  		
			editor.putInt((PLAYER2 + i), temp2[i]);  	  		
		}
		// columnFill State
		int[] temp3 = gamePointer.theGrid.getColumnFill();
		for (int i = 0; i < temp3.length; i++){
			editor.putInt((COLUMNFILL + i), temp3[i]);  	  		
		}  	
		editor.putInt((CURRENTPLAYER), gamePointer.getPlayer());  	
		editor.putInt((WINNER), gamePointer.theGrid.getWinner());
		// save everything
		editor.commit();  	  	
	}

	// function that loads all previously saved application data
	protected void loadSavedState(){
		SharedPreferences storageFile = this.getPreferences(Context.MODE_PRIVATE);

		// Restore current state
		int[] temp = new int[42];	 	
		for (int i = 0; i < temp.length; i++){
			temp[i] = storageFile.getInt((GRID_STATE + i), 0);	  		
		}	 	
		//set	  	
		gamePointer.theGrid.setCurrentState(temp);

		// Restore player1 state
		int[] temp1 = new int[42];	 	
		for (int i = 0; i < temp1.length; i++){
			temp1[i] = storageFile.getInt((PLAYER1 + i), 0);	  		
		}	 	
		//set	  	
		gamePointer.theGrid.setPlayer1Grid(temp1);

		// Restore player2 state
		int[] temp2 = new int[42];	 	
		for (int i = 0; i < temp2.length; i++){
			temp2[i] = storageFile.getInt((PLAYER2 + i), 0);	  		
		}	 	
		//set	  	
		gamePointer.theGrid.setPlayer2Grid(temp2);

		// Restore columnfill state
		int[] temp3 = new int[7];	 	
		for (int i = 0; i < temp3.length; i++){
			temp3[i] = storageFile.getInt((COLUMNFILL + i), 0);	  		
		}	 	
		//set	  	
		gamePointer.theGrid.setColumnFill(temp3);
		
		gamePointer.setPlayer(storageFile.getInt((CURRENTPLAYER), 0));
		gamePointer.theGrid.setWinner(storageFile.getInt((WINNER), 0));

		return;    	
	}

	@Override
	public void onBackPressed() {
		Log.d(TAG, "onBackPressed called...");
		super.onBackPressed();		
		// reset grid player and score
		resetGrid();
		gamePointer.setPlayer(1);
		score1 = 0;
		score2 = 0;
		// save changes
		this.onPause();
		this.finish();
	}

	protected void onResume(){
		Log.d(TAG, "onResume called...");
		super.onResume();		
		// end thread
		gamePointer.endGame();
		setContentView(R.layout.activity_grid);
		gamePointer = (GameSurfaceView) findViewById(R.id.GameSurfaceView1);
		player_one = (Button)findViewById(R.id.point_1);
		player_two = (Button)findViewById(R.id.point_2);	
		// update score
		player_one.setText(""+ score1);
		player_two.setText(""+ score2);	
	}

	@Override
	protected void onDestroy() {
		Log.d(TAG, "Destroying...");
		super.onDestroy();
		this.finish();
	}

	@Override
	protected void onStop() {		
		Log.d(TAG, "Stopping... onStop called!!");
		super.onStop();	
	}

	@Override
	protected void onStart() {		
		Log.d(TAG, "onStart called...");
		super.onStart();				
		//load the saved Game state data	  
	}

	@Override
	protected void onRestart(){
		super.onRestart();
	}
	
	//set all the grid values to 0
	public void resetGrid(){
		int[] emptyGrid = new int[42];
		gamePointer.theGrid.setCurrentState(emptyGrid);
		gamePointer.theGrid.setPlayer1Grid(emptyGrid);
		gamePointer.theGrid.setPlayer2Grid(emptyGrid);
		int[] emptyColumn = new int[7];
		gamePointer.theGrid.setColumnFill(emptyColumn);
		gamePointer.theGrid.setWinner(0);
	}

	//Reset the whole game (set points to 0)
	public void reset(){
		//Reset everything (grid, player, score)
		resetGrid();
		gamePointer.setPlayer(1);	
		score1 = 0;
		score2 = 0;
		// to save state call onPause
		this.onPause();
		this.onResume();
	}

	//Clear the board (keep the points)
	public void restart(){
		//Reset grid
		resetGrid();
		//Pause to save new states (empty grid)
		this.onPause();
		this.onResume();
	}

	// Called when there is a winner 
	public void displayWinnerMenu(int winner) {	
		WinnerDialogFragment winnerDialog = new WinnerDialogFragment();
		winnerDialog.setWinner(winner);
		winnerDialog.setStyle(2, 0);
		winnerDialog.show(fragmentManager, "tag");
	}
	
	// update the scores
	public void displayScore(int winner){
		if (winner == 1) {
			score1++;
		}
		else if (winner == 2){
			score2++;
		}
		player_one.setText(""+ score1);
		player_two.setText(""+ score2);		
	}
}
