package com.jagtarsingh.tappyspaceship;



import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Random;

public class GameEngine extends SurfaceView implements Runnable {

    // Android debug variables
    final static String TAG="TAPPY-SPACESHIP";

    // screen size
    int screenHeight;
    int screenWidth;

    // game state
    boolean gameIsRunning;

    // threading
    Thread gameThread;


    // drawing variables
    SurfaceHolder holder;
    Canvas canvas;
    Paint paintbrush;



    // -----------------------------------
    // GAME SPECIFIC VARIABLES
    // -----------------------------------

    // ----------------------------
    // ## SPRITES
    // ----------------------------

    // Player variables
    Bitmap playerImage;
    Rect playerHitbox;
    Point playerPos;    // (left,top) of the player

    // Enemy variables
    Bitmap enemyImage;
    Rect enemyHitbox;


    // ----------------------------
    // ## GAME STATS
    // ----------------------------
    int score = 0;
    int lives = 3;

    public GameEngine(Context context, int w, int h) {
        super(context);


        this.holder = this.getHolder();
        this.paintbrush = new Paint();

        this.screenWidth = w;
        this.screenHeight = h;


        this.printScreenInfo();

        // @TODO: Add your sprites
        // @TODO: Any other game setup

        // ----------------
        // PLAYER SETUP
        // ----------------
        this.playerImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.player_ship);

        // setup the initial position of player
        this.playerPos = new Point();
        this.playerPos.x = 100;
        this.playerPos.y = 120;

        // setup the hitbox
        this.playerHitbox = new Rect(
                this.playerPos.x,
                this.playerPos.y,
                this.playerPos.x+this.playerImage.getWidth(),
                this.playerPos.y + playerImage.getHeight());

        // ----------------
        // ENEMEY SETUP
        // ----------------
        this.enemyImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.alien_ship2);

        // version 1 - we use static numbers because the enemy is NOT moving.
        this.enemyHitbox = new Rect(
                this.screenWidth - 500,
                120,
                this.screenWidth - 500 + this.enemyImage.getWidth(),
                120 + this.enemyImage.getHeight()
        );




    }


    private void printScreenInfo() {

        Log.d(TAG, "Screen (w, h) = " + this.screenWidth + "," + this.screenHeight);
    }

    private void spawnPlayer() {
        //@TODO: Start the player at the left side of screen
    }
    private void spawnEnemyShips() {
        Random random = new Random();

        //@TODO: Place the enemies in a random location

    }

    // ------------------------------
    // GAME STATE FUNCTIONS (run, stop, start)
    // ------------------------------
    @Override
    public void run() {
        while (gameIsRunning == true) {
            this.updatePositions();
            this.redrawSprites();
            this.setFPS();
        }
    }


    public void pauseGame() {
        gameIsRunning = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            // Error
        }
    }

    public void startGame() {
        gameIsRunning = true;
        gameThread = new Thread(this);
        gameThread.start();
    }


    // ------------------------------
    // GAME ENGINE FUNCTIONS
    // - update, draw, setFPS
    // ------------------------------
    final int PLAYER_SPEED = 30;
    boolean gameOver = false;

    public void updatePositions() {
        // @TODO: Update position of player
        // 1. move the player
        this.playerPos.x = this.playerPos.x + PLAYER_SPEED;
        // 2. move the hitbox
        this.playerHitbox.left = this.playerHitbox.left + PLAYER_SPEED;
        this.playerHitbox.right = this.playerHitbox.right + PLAYER_SPEED;

        // @TODO: Update position of enemy ships


        // @TODO: Collision detection between player and enemy
        if (playerHitbox.intersect(this.enemyHitbox)) {
            Log.d(TAG, "COLLISION!!!!!");
            this.lives = this.lives - 1;
            Log.d(TAG, "Lives remaining: " + this.lives);


            // decide if you should be game over:
            if (this.lives == 0) {
                this.gameOver = true;
                return;
            }


            // restart player from starting position
            this.playerPos.x = 100;
            this.playerPos.y = 120;

            // restart hitbox
            this.playerHitbox.left = this.playerPos.x;
            this.playerHitbox.top = this.playerPos.y;
            this.playerHitbox.right = this.playerPos.x+this.playerImage.getWidth();
            this.playerHitbox.bottom = this.playerPos.y + playerImage.getHeight();
        }

    }

    public void redrawSprites() {
        if (this.holder.getSurface().isValid()) {
            this.canvas = this.holder.lockCanvas();

            //----------------

            // configure the drawing tools
            this.canvas.drawColor(Color.argb(255,255,255,255));
            paintbrush.setColor(Color.WHITE);


            //@TODO: Draw the player
            canvas.drawBitmap(playerImage, this.playerPos.x, this.playerPos.y, paintbrush);

            //@TODO: Draw the enemy
            canvas.drawBitmap(enemyImage, this.screenWidth - 500, 120, paintbrush);

            // DRAW THE PLAYER HITBOX
            // ------------------------
            // 1. change the paintbrush settings so we can see the hitbox
            paintbrush.setColor(Color.BLUE);
            paintbrush.setStyle(Paint.Style.STROKE);
            paintbrush.setStrokeWidth(5);

            // 2. draw the hitbox
            canvas.drawRect(this.playerHitbox.left,
                    this.playerHitbox.top,
                    this.playerHitbox.right,
                    this.playerHitbox.bottom,
                    paintbrush
            );

            // Draw enemy hitbox
            paintbrush.setColor(Color.RED);
            canvas.drawRect(this.enemyHitbox.left,
                    this.enemyHitbox.top,
                    this.enemyHitbox.right,
                    this.enemyHitbox.bottom,
                    paintbrush
            );


            // DRAW GAME STATS

            paintbrush.setTextSize(100);     // set font size
            paintbrush.setStrokeWidth(5);  // make text narrow
            canvas.drawText("Lives: " + this.lives, 50, 100, paintbrush);

            if (gameOver == true) {
                canvas.drawText("GAME OVER!", 50, 200, paintbrush);
            }








            //----------------
            this.holder.unlockCanvasAndPost(canvas);
        }
    }

    public void setFPS() {
        try {
            gameThread.sleep(120);
        }
        catch (Exception e) {

        }
    }

    // ------------------------------
    // USER INPUT FUNCTIONS
    // ------------------------------

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int userAction = event.getActionMasked();
        //@TODO: What should happen when person touches the screen?
        if (userAction == MotionEvent.ACTION_DOWN) {
            Log.d(TAG, "Person tapped the screen");
        }
        else if (userAction == MotionEvent.ACTION_UP) {
            Log.d(TAG, "Person lifted finger");
        }

        return true;
    }
}

