package com.example.LineEmUp;

import com.example.LineEmUp.R;
import com.example.LineEmUp.model.Grid;
import com.example.LineEmUp.model.Token;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.view.MotionEvent;

/*
 * 
 */
public class GameSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

	private static final String TAG = GameSurfaceView.class.getSimpleName();
	
	protected GameThread thread;
	protected Grid theGrid;
	private Context gridActivity;
	
	private int currentplayer;	// the current player (1 or 2)
	private Token theToken;	    // current player's token (above board)
	protected boolean created;	// true if view is fully created (all elements are present)
	
	// Bitmaps (images)
	private Bitmap token1 	  = (BitmapFactory.decodeResource(getResources(), R.drawable.redtoken));
	private Bitmap token2 	  = (BitmapFactory.decodeResource(getResources(), R.drawable.yellowtoken));
	private Bitmap background = (BitmapFactory.decodeResource(getResources(), R.drawable.background));
	
	// Constructor
	public GameSurfaceView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		Log.d(TAG, "New GameSurfaceView created!!!");

		gridActivity = context;
		created = false;

		// adding the callback (this) to the surface holder to intercept events
		getHolder().addCallback(this);

		// create a token and load bitmap	  	  
		theToken = new Token(token1, 50, 50);

		// create the game loop thread
		thread = new GameThread(getHolder(), this);

		// make the GamePanel focusable so it can handle events
		setFocusable(true);
	}

	// Constructor
	public GameSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		Log.d(TAG, "New GameSurfaceView created!!!");

		gridActivity = context;
		created = false;

		// adding the callback (this) to the surface holder to intercept events
		getHolder().addCallback(this);

		// create a token and load bitmap	  	  
		theToken = new Token(token1, 50, 50);

		// create the game loop thread
		thread = new GameThread(getHolder(), this);

		// make the GamePanel focusable so it can handle events
		setFocusable(true);
	}

	// Constructor
	public GameSurfaceView(Context context) {
		super(context);
		Log.d(TAG, "New GameSurfaceView created!!!");

		gridActivity = context;
		created = false;

		// adding the callback (this) to the surface holder to intercept events
		getHolder().addCallback(this);

		// create a token and load bitmap	  	  
		theToken = new Token(token1, 50, 50);

		// create the game loop thread
		thread = new GameThread(getHolder(), this);

		// make the GamePanel focusable so it can handle events
		setFocusable(true);
	}

	// Sets the size of the grid (board) and all the Bitmaps involved
	public void setSizes(int gridH, int gridW, int h, int w){
		
		// resize Bitmaps
		token1 = Bitmap.createScaledBitmap(token1, w, h, true);
		token2 = Bitmap.createScaledBitmap(token2, w, h, true);

		//set the current player and token
		currentplayer = 1;
		theToken.setBitmap(token1);

		theGrid = new Grid(this, gridH, gridW, h, w);		 

		// LOAD from saved state		 
		((GridActivity) gridActivity).loadSavedState();
		created = true;
		Log.d(TAG, "Grid is created and fully loaded!!!");	 
	}

	// Function that ends the game
	public void endGame(){		 
		thread.setRunning(false);
	}

	// Displays Winner in pop-up
	public void displayWinner(int player){		
		((GridActivity) gridActivity).displayWinnerMenu(player);
		((GridActivity) gridActivity).displayScore(player);	
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// at this point the surface is created and
		// we can safely start the game loop

		String temp1 = "NEW";			  
		Log.d(TAG, "starting thread!!! state: " + thread.getState());

		//if the tread is new start it
		if( ((thread.getState()).toString()).equals(temp1)){
			thread.setRunning(true);
			thread.start();
		}
		//else create new thread
		else{
			thread = new GameThread(getHolder(), this);
			thread.setRunning(true);
			thread.start();
		}
	}


	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.d(TAG, "Surface is being destroyed");
		// tell the thread to shut down and wait for it to finish
		// this is a clean shutdown
		boolean retry = true;
		while (retry) {
			try {
				thread.join();
				retry = false;
			} catch (InterruptedException e) {
				// try again shutting down the thread
			}
		}
		Log.d(TAG, "Thread was shut down cleanly");
	}

	// Sets the x-coordinate of the current player's token
	public void setTokenX (int x){
		theToken.setX(x);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		if(theGrid.getWinner() == 0){
			// On Click (On Touch)
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				theGrid.handleActionDown((int)event.getX(), (int)event.getY());
				Log.d(TAG, "Coords: x=" + event.getX() + ",y=" + event.getY());
			}

			// On Drag
			if (event.getAction() == MotionEvent.ACTION_MOVE) {
				theGrid.handleActionMove((int)event.getX(), (int)event.getY());
			}

			// On Release
			if (event.getAction() == MotionEvent.ACTION_UP) {
				// touch was released
				if (theGrid.isTouched()) {
					theGrid.setTouched(false);

					//check if inside a drop zone and update grid and state array
					theGrid.tokenDropped((int)theToken.getX());
					Log.d(TAG, "Mouse.onRelease");
				}
			}	  
			return true;
		}
		else return false;		
	}

	//method that changes the current player
	public void changePlayer(){
		if (currentplayer == 1){
			currentplayer = 2;
			theToken.setBitmap(token2);
		}
		else{
			currentplayer = 1;
			theToken.setBitmap(token1);
		}		 
		//re-initialize the token to start location
		theToken.setX((int)((theGrid.getGridWidth()+1)/2));
		theToken.setY(50);
	}

	//Sets the current player (1 or 2)
	protected void setPlayer(int i){
		if (i == 2){
			currentplayer = 2;
			theToken.setBitmap(token2);
		}
		else{
			currentplayer = 1;
			theToken.setBitmap(token1);
		}		 
		//re-initialize the token to start location
		theToken.setX((int)((theGrid.getGridWidth()+1)/2));
		theToken.setY(50);
	}

	// returns the current player
	public int getPlayer(){
		return this.currentplayer;
	}

	// Draws all the elements to the surface
	public void draw(Canvas canvas){
		int height = canvas.getHeight();
		int width = canvas.getWidth();
		
		// add background image to canvas
		Rect backgroundRec = new Rect(0, 0, width, height);
		canvas.drawBitmap(background, null, backgroundRec, null);
		
		if (created){
			//draw the grid (board)
			theGrid.draw(canvas);
			// if no winner yet
			if(theGrid.getWinner() == 0){
				//draw token above board
				theToken.draw(canvas); 
			}
			else{
				// there is a winner, do not display token.
				// instead display message "Press New Game" above the board
				Paint textPaint = new Paint();
				textPaint.setTextSize(30);
				textPaint.setTextScaleX((float) 0.8);
				textPaint.setColor(Color.BLACK);
				textPaint.setTextAlign(Paint.Align.CENTER);
				textPaint.setTypeface(Typeface.MONOSPACE);
				canvas.drawText("Press New Game.", width/2, 70, textPaint);			
			}
		}
	}
}
