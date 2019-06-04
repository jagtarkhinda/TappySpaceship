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

    Player player;

    // Enemy variables
    Enemy enemy1;
    Enemy enemy2;

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

        int initialPlayerX = 100;
        int initialPlayerY = 120;
        this.player = new Player(context,initialPlayerX, initialPlayerY);



        // ----------------
        // ENEMEY SETUP
        // ----------------

        this.enemy1 = new Enemy(context, this.screenWidth - 500, 120);
        this.enemy2 = new Enemy(context, this.screenWidth - 500, this.screenHeight - 400);
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
        // 1. move the player downwards by default and stop at bottom
        if(player.getyPosition()+ player.getImage().getHeight() <= this.screenHeight -200) {
            this.player.setyPosition(this.player.getyPosition() + PLAYER_SPEED);
            this.player.getHitbox().top = this.player.getHitbox().top + PLAYER_SPEED;
            this.player.getHitbox().bottom = this.player.getHitbox().bottom + PLAYER_SPEED;

        }


        // @TODO: Update position of enemy ships

        //move the enemy to the left

        this.enemy1.setxPosition(this.enemy1.getxPosition() - PLAYER_SPEED);
        Log.d("enemy", String.valueOf(this.enemy1.getxPosition()));

        // 2. move the enemy hitbox

        this.enemy1.getHitbox().left = this.enemy1.getHitbox().left - PLAYER_SPEED;
        this.enemy1.getHitbox().right = this.enemy1.getHitbox().right - PLAYER_SPEED;



        // @TODO: Collision detection between player and enemy

        //  between enemy and wall
        if(enemy1.getxPosition()+ enemy1.getImage().getWidth() <= 0)
        {
            // restart enemy from starting position
            this.enemy1.setxPosition(this.screenWidth-500);
            this.enemy1.setyPosition(120);


            Rect hitbox = new Rect(this.enemy1.getxPosition(),
                    this.enemy1.getyPosition(),
                    this.enemy1.getxPosition() + this.enemy1.getImage().getWidth(),
                    this.enemy1.getyPosition() + this.enemy1.getImage().getHeight()
            );
            this.enemy1.setHitbox(hitbox);
        }

        if (player.getHitbox().intersect(this.enemy1.getHitbox()) || player.getHitbox().intersect(this.enemy2.getHitbox())) {
            Log.d(TAG, "COLLISION!!!!!");
            this.lives = this.lives - 1;
            Log.d(TAG, "Lives remaining: " + this.lives);


            // decide if you should be game over:
            if (this.lives == 0) {
                this.gameOver = true;
                return;
            }


            // restart player from starting position
            this.player.setxPosition(100);
            this.player.setyPosition(120);

            // restart hitbox
            this.player.getHitbox().left = this.player.getxPosition();
            this.player.getHitbox().top = this.player.getyPosition();
            this.player.getHitbox().right = this.player.getxPosition()+this.player.getImage().getWidth();
            this.player.getHitbox().bottom = this.player.getxPosition() + player.getImage().getHeight();
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
            canvas.drawBitmap(this.player.getImage(), this.player.getxPosition(), this.player.getyPosition(), paintbrush);

            //@TODO: Draw the enemy

            //enemy 1
            canvas.drawBitmap(this.enemy1.getImage(), this.enemy1.getxPosition(), this.enemy1.getyPosition(), paintbrush);

            //enemy 2
            canvas.drawBitmap(this.enemy2.getImage(), this.enemy2.getxPosition(), this.enemy2.getyPosition(), paintbrush);

            // DRAW THE PLAYER HITBOX
            // ------------------------
            // 1. change the paintbrush settings so we can see the hitbox
            paintbrush.setColor(Color.BLUE);
            paintbrush.setStyle(Paint.Style.STROKE);
            paintbrush.setStrokeWidth(5);

            // 2. draw the hitbox
            canvas.drawRect(this.player.getHitbox().left,
                    this.player.getHitbox().top,
            this.player.getHitbox().right,
            this.player.getHitbox().bottom,paintbrush);


            // Draw enemy1 hitbox
            paintbrush.setColor(Color.RED);
            canvas.drawRect(this.enemy1.getHitbox().left,
                    this.enemy1.getHitbox().top,
                    this.enemy1.getHitbox().right,
                    this.enemy1.getHitbox().bottom,paintbrush);

            // Draw enemy2 hitbox
            paintbrush.setColor(Color.RED);
            canvas.drawRect(this.enemy2.getHitbox().left,
                    this.enemy2.getHitbox().top,
                    this.enemy2.getHitbox().right,
                    this.enemy2.getHitbox().bottom,paintbrush);


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
            this.player.setyPosition(this.player.getyPosition() - PLAYER_SPEED*3);
            this.player.getHitbox().top = this.player.getHitbox().top - PLAYER_SPEED*3;
            this.player.getHitbox().bottom = this.player.getHitbox().bottom - PLAYER_SPEED*3;

        }
        else if (userAction == MotionEvent.ACTION_UP) {
            Log.d(TAG, "Person lifted finger");
        }

        return true;
    }
}

