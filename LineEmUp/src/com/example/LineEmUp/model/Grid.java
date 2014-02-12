package com.example.LineEmUp.model;

import com.example.LineEmUp.GameSurfaceView;
import com.example.LineEmUp.R;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;

public class Grid {

	private static final String TAG = Grid.class.getSimpleName();
	
	private GameSurfaceView gamePanel;

	protected int[][] currentState = new int[6][7];		// stores the state of our grid
	protected int[][] player1Grid = new int [6][7];		// stores the location of player 1's tokens
	protected int[][] player2Grid = new int [6][7];		// stores the location of player 2's tokens
	protected int[] columnFill = {0, 0, 0, 0, 0, 0, 0};	// stores how full each column is.

	// winner = 1 if player 1 wins
	// winner = 2 if player 2 wins
	// winner = 3 if it's a tie
	// winner = 0 otherwise
	private int winner = 0;
	
	// Bitmaps (images)
	private Bitmap boardfront;		// the front of the board
	private Bitmap boardback;		// the back side of the board as seen from the front through the board's holes.
	private Bitmap emptysquare; 	// 
	private Bitmap token1_square;	// token 1 when inside board (actually a square and not a disc)
	private Bitmap token2_square;	// token 2 when inside board (actually a square and not a disc)
	
	// board information
	private int gridHeight;		// board height
	private int gridWidth;		// board width
	private int border;			// border width
	private int rowHeight;   	// the board's row height
	private int columnWidth;   	// the board's column width
	private boolean touched; 	// true if board is touched
	
	// board coordinates
	// board's upper left corner coordinate (xcoordmin, ycoordmin)
	// board's lower left corner coordinate (xcoordmin, ycoordmax)
	// board's upper right corner coordinate (xcoordmax, ycoordmin)
	// board's lower right corner coordinate (xcoordmax, ycoordmax)
	private int xcoordmin;		
	private int xcoordmax;
	private int ycoordmin;
	private int ycoordmax;	

	public Grid(GameSurfaceView gameSurface, int gridh, int gridw, int h, int w) {
		Log.d(TAG, "** A Grid is Created!!! **");
		this.gamePanel = gameSurface;
		this.gridHeight = gridh;
		this.gridWidth = gridw;
		this.rowHeight = h;
		this.columnWidth = w;
		border = (gridWidth/100); // 1:100 ratio (border):(board width) of image
		setBoardCoordinates((int)(1.5*rowHeight));
		
		// set Bitmaps
		token1_square = (BitmapFactory.decodeResource(gamePanel.getResources(), R.drawable.redsquare));
		token2_square = (BitmapFactory.decodeResource(gamePanel.getResources(), R.drawable.yellowsquare));
		boardfront = (BitmapFactory.decodeResource(gamePanel.getResources(), R.drawable.boardfront));
		boardback = (BitmapFactory.decodeResource(gamePanel.getResources(), R.drawable.boardback));
		
		//resize Bitmaps
		token1_square	= Bitmap.createScaledBitmap(token1_square, columnWidth, rowHeight, true);
		token2_square	= Bitmap.createScaledBitmap(token2_square, columnWidth, rowHeight, true);
		boardfront	= Bitmap.createScaledBitmap(boardfront, gridWidth, gridHeight, true);
		boardback	= Bitmap.createScaledBitmap(boardback, gridWidth, gridHeight, true);
	}
	
	// Sets the x and y coordinates of the boards corners
	// the board's upper left corner coordinate (xcoordmin, ycoordmin)
	// topPad is the space between the top of the screen and the start of the board
	public void setBoardCoordinates( int topPad ){
		xcoordmin = 0;
		xcoordmax = gridWidth;
		ycoordmin = topPad;
		ycoordmax = ycoordmin + gridHeight;
	}

	// Draws the board
	public void draw(Canvas canvas) {
		Bitmap temp = null;	 
		// draw back of board
		canvas.drawBitmap(boardback, xcoordmin, ycoordmin, null);
		// fill board with tokens
		for(int i = 0; i < 6; i++){		// for each row i
			for(int k = 0; k < 7; k++){	// for each column k
				switch(currentState[i][k]){
					case  1: temp = token1_square; break;
					case  2: temp = token2_square; break;
					default: temp = emptysquare; break;
				}
				if(temp != emptysquare ){
					int xcoord = xcoordmin + border + (k * columnWidth);
					int ycoord = ycoordmin + border + (i * rowHeight);
					canvas.drawBitmap(temp, xcoord, ycoord, null); 
				}
			}
		}
		// draw front of board
		canvas.drawBitmap(boardfront, xcoordmin, ycoordmin, null);
	}
	
	// returns the column over which the token is placed/dropped
	public int getDropColumn( int x ){
		return (x - xcoordmin - border)/columnWidth;
	}

	//function that checks if the token is dropped inside a drop zone and updates the grid and state array
	public void tokenDropped(int x){
		int i = getDropColumn(x); // get column number
		
		//check if column is full
		if(columnFill[i] < 6){
			//if not full add to column
			currentState[(5 - columnFill[i])][i] = gamePanel.getPlayer();
			
			//if player 1 update its grid and check if winner
			if (gamePanel.getPlayer() == 1) {
				player1Grid[(5 - columnFill[i])][i] = 1;
				// check for winner 
				if(checkforwin(player1Grid))
					winner = 1;
			}
			//else player 2 so update its grid, and check if winner
			else { 		 
				player2Grid[(5 - columnFill[i])][i] = 1;
				// check for winner 
				if(checkforwin(player2Grid))
					winner = 2;
			}
			// increment the column fill array
			columnFill[i]++;
			
			//
			if(isGridFull() && winner == 0 )
				winner = 3;

			// check for winner 
			if (winner != 0){
				// display winner
				Log.d(TAG, "winner = "+winner);
				gamePanel.displayWinner(winner);			 		 		 		 
			}

			//move complete			 	 
			//change player
			gamePanel.changePlayer();
		}
	}
	
	// returns true if the board is full
	public boolean isGridFull(){
		for ( int i = 0; i < 7; i++ )
			if ( columnFill[i] != 6 )
				return false;
		return true;
	}

	public int getGridWidth(){
		return gridWidth;
	}

	// returns true if board is touched
	public boolean isTouched() {
		return touched;
	}

	// Sets the board's touched boolean to b.
	public void setTouched(boolean b) {
		this.touched = b;
	}

	// Handles On Touch ( when user touches the board)
	public void handleActionDown(int eventX, int eventY) {
		// if touching board
		if( (eventX <=  xcoordmax ) && (eventY >= ycoordmin) && (eventY <= ycoordmax) ) {
			setTouched(true);
			Log.d(TAG, "Grid set to touched = true ");
			setTokenX(eventX);
		}
		else {
			setTouched(false);
			Log.d(TAG, "Grid set to touched = false ");
		}
	}

	// Handles On Drag ( when user is dragging his or her finger across the board)
	public void handleActionMove(int eventX, int eventY) {
		// if touching board
		if( (eventX <=  xcoordmax ) && (eventY > ycoordmin) && (eventY < ycoordmax) ) {
			setTouched(true);
			// continuously update the position of the token
			setTokenX(eventX);
		}
		else {
			setTouched(false);
			Log.d(TAG, "Grid set to touched = false ");
		}
	}

	public void setTokenX(int eventX){
		// continuously update the position of the token		
		int x = getDropColumn( eventX );
		gamePanel.setTokenX((int)( xcoordmin + border + ( x + 0.5 ) * columnWidth ));
	}

	//function to check if the player's grid has 4 in a row.
	private boolean checkforwin(int[][] playerGrid){		 
		//Return true if it finds 4 aligned tokens (horizontally, vertically or diagonally)
		if(winHorizontal(playerGrid) || winVertical(playerGrid) || winDiagonal(playerGrid))
			return true;
		return false;
	}	 

	//Returns true if it finds 4 tokens aligned horizontally
	public static boolean winHorizontal(int[][] board){	
		int count = 0;
		for(int row = 5; row >= 0; row--){
			count = 0; //Re-initialize count to 0 every time we change rows
			for(int column = 0; column < 7; column++){
				if(board[row][column] == 1){
					count++;
					if (count == 4){
						Log.d(TAG, "winVertical = TRUE");
						return true;
					}
				}		 
				//Re-initialize count if 0 found in column
				else{ 
					count = 0;
				}				 
			}
		}
		Log.d(TAG, "winVertical = FALSE");
		return false;
	}

	//Returns true if it finds 4 tokens aligned vertically
	public static boolean winVertical(int[][] board){	 
		int count = 0;
		for(int column = 0; column < 7; column++){
			count = 0; //Re-initialize count to 0 every time we change columns
			for(int row = 0; row < 6; row++){
				if(board[row][column] == 1){
					count++;
					if (count == 4){
						Log.d(TAG, "winVertical = TRUE");
						return true;
					}
				}		 
				//Re-initialize count if 0 found in column
				else{ 
					count = 0;
				}				 
			}
		}
		Log.d(TAG, "winVertical = FALSE");
		return false;
	}
	
	//Returns true if it finds 4 tokens aligned diagonally
	public static boolean winDiagonal(int[][] board){
	int k = 0; //Diagonal count

	for (int i = 5; i >= 0; i--){ //rows beginning from bottom of board			 
		for (int j = 0; j < 7; j++){ //column 
			if(board[i][j] == 1){

				// Diagonal up right
				if(i > 2 && j < 4){
					while((k < 4) && (board[i-k][j+k]==1)){
						k++;
						if (k == 4){
							return true;
						}
					}
				} 

				// Re-initialize k 
			 	// (f.ex: if there were 3 tokens aligned diagonally up and right, 
				// the values of k  would have changed.

				k=0;

				//Diagonal up left 
				if(i > 2 && j > 2){ 
					while((k < 4) && (board[i-k][j-k]==1)){	
						k++;
						if(k==4){
							return true;
						}
					}
				}
			}
		}
	}
	return false;
} 
	
	public int[] getCurrentState() {
		// convert from 2 dimensional array to single dimension and return
		int[] temp = new int[42];
		for(int i = 0; i < 6; i++){		// loop for rows
			for(int k = 0; k < 7; k++){	//loop for columns
				// 
				//temp[i+k] = currentState[i][k];
				temp[i+k+(i*6)] = currentState[i][k];
			}	 
		}	 	
		return temp;
	}


	public void setCurrentState(int[] temp) { 		
		// convert from 1 dimensional array to 2 dimension		 	
		for(int i = 0; i < 6; i++){		// loop for rows
			for(int k = 0; k < 7; k++){	//loop for columns					
				currentState[i][k] = temp[i+k+(i*6)];		 
			}	 
		}	 	
		return;			
	}


	public int[] getPlayer1Grid() {
		// convert from 2 dimensional array to single dimension and return
		int[] temp = new int[42];
		for(int i = 0; i < 6; i++){		// loop for rows
			for(int k = 0; k < 7; k++){	//loop for columns
				temp[i+k+(i*6)] = player1Grid[i][k];
			}	 
		}	 	
		return temp;
	}

	public void setPlayer1Grid(int[] temp) { 		
		// convert from 1 dimensional array to 2 dimension		 	
		for(int i = 0; i < 6; i++){		// loop for rows
			for(int k = 0; k < 7; k++){	//loop for columns					
				player1Grid[i][k] = temp[i+k+(i*6)];		 
			}	 
		}	 	
		return;			
	}

	public int[] getPlayer2Grid() {
		// convert from 2 dimensional array to single dimension and return
		int[] temp = new int[42];
		for(int i = 0; i < 6; i++){		// loop for rows
			for(int k = 0; k < 7; k++){	//loop for columns
				// 
				//temp[i+k] = currentState[i][k];
				temp[i+k+(i*6)] = player2Grid[i][k];
			}	 
		}	 	
		return temp;
	}

	public void setPlayer2Grid(int[] temp) { 		
		// convert from 1 dimensional array to 2 dimension		 	
		for(int i = 0; i < 6; i++){		// loop for rows
			for(int k = 0; k < 7; k++){	//loop for columns					
				player2Grid[i][k] = temp[i+k+(i*6)];		 
			}	 
		}	 	
		return;			
	}

	// returns how full the board's columns are.
	public int[] getColumnFill() { 	
		return columnFill;
	}

	// Sets columnFill to temp.
	public void setColumnFill(int[] temp) { 			 	
		for(int i = 0; i < 7; i++){		 					
			columnFill[i] = temp[i];				 	 
		}	
		return;
	}
	
	// returns the winner (0,1,2 or 3)
	public int getWinner(){
		return winner;
	}
	
	// sets the winner to i.
	public void setWinner(int i){
		this.winner = i;
	}

}