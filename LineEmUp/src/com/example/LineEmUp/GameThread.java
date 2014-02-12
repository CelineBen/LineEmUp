package com.example.LineEmUp;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;


public class GameThread extends Thread {

	private static final String TAG = GameThread.class.getSimpleName();

	private GameSurfaceView gamePanel;

	private SurfaceHolder surfaceHolder;

	// flag to hold game state
	private boolean running;

	// Constructor
	public GameThread(SurfaceHolder surfaceHolder, GameSurfaceView gamePanel) {
		super();
		this.surfaceHolder = surfaceHolder;
		this.gamePanel = gamePanel;
	}

	// Sets the game state flag (running)
	public void setRunning(boolean b) {
		this.running = b;
	}

	// returns the dimensions of the grid (board) in an array
	// board width followed by board height
	// [(grid width), (grid height)]
	public double[] getGridDimensions( Canvas c ){
		
		// 50:43 ratio (board width):(board height)
		// ratio optained from image
		double[] dimensions = new double[2];
		int h = c.getHeight();				// canvas width
		int w = c.getWidth();				// canvas height

		// if can use full screen width
		if((h*6/7.5/43) >= (w/50)){
			// use width to calculate dimensions
			dimensions[0] = w;				// grid width
			dimensions[1] = w*43/50;		// grid height
		}
		else {
			// use height to calculate dimensions
			dimensions[0] = h*6/7.5*50/43;	// grid width
			dimensions[1] = h*6/7.5;			// grid height
		}
		return dimensions;
	}

	// returns the size of the tokens
	public double getTokenSize( double gridWidth ){
		// 7:50 ratio (token width):(board width)
		// ratio obtained from image
		return gridWidth*7/50;
	} 

	public void run() {
		long tickCount = 0L;
		Canvas canvas;

		Log.d(TAG, "Starting game loop");

		/**Check the size of the surface and calculated the size of various parts on the screen
		 * **/
		canvas = null;
		// try locking the canvas for exclusive pixel editing on the surface
		try {
			canvas = this.surfaceHolder.lockCanvas();
			synchronized (surfaceHolder) {

				//get size of canvas
				double[] dimensions = getGridDimensions(canvas);
				int gridWidth = (int)dimensions[0];
				int gridHeight = (int)dimensions[1];

				int rowheight = (int)getTokenSize(gridWidth)+1;
				int columnwidth = rowheight;

				// set the token size
				this.gamePanel.setSizes(gridHeight, gridWidth, rowheight, columnwidth);

				// draws the canvas on the panel
				this.gamePanel.draw(canvas);
			}
		} finally {
			// in case of an exception the surface is not left in an inconsistent state
			if (canvas != null) {
				surfaceHolder.unlockCanvasAndPost(canvas);
			}
		} // end finally
		while (running) {
			tickCount++;
			canvas = null;
			// try locking the canvas for exclusive pixel editing on the surface
			try {
				canvas = this.surfaceHolder.lockCanvas();
				synchronized (surfaceHolder) {
					// update canvas
					this.gamePanel.draw(canvas);
				}
			} finally {
				// in case of an exception the surface is not left in an inconsistent state
				if (canvas != null) {
					surfaceHolder.unlockCanvasAndPost(canvas);
				}
			} // end finally
		}
		Log.d(TAG, "Game loop executed " + tickCount + " times");
	}	 
}