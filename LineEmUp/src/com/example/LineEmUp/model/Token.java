package com.example.LineEmUp.model;

import com.example.LineEmUp.GameSurfaceView;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

public class Token {

	private static final String TAG = GameSurfaceView.class.getSimpleName();

	private Bitmap token; // the actual image (Bitmap)
	private int x;   // the X coordinate
	private int y;   // the Y coordinate

	// Constructor
	public Token(Bitmap token, int x, int y) {
		this.token = token;
		this.x = x;
		this.y = y;
	}

	// Gets the token's image (Bitmap)
	public Bitmap getBitmap() {
		return token;
	}
	
	// Sets the token's image (Bitmap)
	public void setBitmap(Bitmap token) {
		this.token = token;
	}
	
	//Gets the token's x-coordinate
	public int getX() {
		return x;
	}
	
	//Sets the token's x-coordinate 
	public void setX(int x) {
		this.x = x;
	}
	
	//Gets the token's y-coordinate
	public int getY() {
		return y;
	}
	
	//Sets the token's y-coordinate
	public void setY(int y) {
		this.y = y;
	}

	// draws the current player's token above the board
	public void draw(Canvas canvas) {
		canvas.drawBitmap(token, x - (token.getWidth() / 2), y - (token.getHeight() / 2), null);
		Log.d(TAG, "Created current player's token (above board)");
	}

}