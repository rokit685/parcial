package com.example.parcialgame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameSurfaceView extends SurfaceView implements Runnable {
    private boolean isPlaying;
    private Nave nave;
    private Misil misil;
    private Paint paint;
    private Paint paintStart;
    private Canvas canvas;
    private SurfaceHolder holder;
    private Thread gameplayThread = null;
    private  int times=0;
    private int lives;
    private int score=0;
    private Asteroide[] asteroides= new Asteroide[9];
    private NaveEnemiga[] navesEnemigas= new NaveEnemiga[9];
    private MisilEnemigo[] misilEnemigo= new MisilEnemigo[9];
    boolean active=false;
    boolean asteroideTouched=false;
    int screenWith=0;
    int level=1;
    boolean disparar=true;

    private Bitmap bgImage;
    /**
     * Contructor
     * @param context
     */
    public GameSurfaceView(Context context, float screenWith, float screenHeight) {
        super(context);
        this.screenWith=(int)screenWith;

        bgImage = BitmapFactory.decodeResource(getResources(),R.drawable.bg);
        nave = new Nave(context, screenWith, screenHeight);
        misil = new Misil(context, screenWith, screenHeight);
        paint = new Paint();
        paintStart= new Paint();
        paintStart.setTextSize(100);
        paint.setColor(Color.WHITE);
        holder = getHolder();
        isPlaying = true;


        for (int i=0;i<asteroides.length;i=i+1){
            asteroides[i]= new Asteroide(context, screenWith, screenHeight);
        }
        for (int i=0;i<navesEnemigas.length;i=i+1){
            navesEnemigas[i]= new NaveEnemiga(context, screenWith, screenHeight);
        }
        for (int i=0;i<misilEnemigo.length;i=i+1) {
            misilEnemigo[i] = new MisilEnemigo(context, screenWith, screenHeight);
        }

    }

    /**
     * Method impleasteroideted from runnable interface
     */
    @Override
    public void run() {
        while (isPlaying) {
            updateInfo();
            paintFrame();
        }

    }

    private void updateInfo() {

        if(active){
            nave.updateInfo();
            misil.updateInfo(nave.getPositionX(), nave.getPositionY(),level);
            for(int i=0; i < this.level ;i=i+1) {
                if(this.score<0){
                    active=false;
                    score=0;
                }
            }

            for(int i=0;i<this.level;i=i+1) {
                if( misilEnemigo[i].getPositionX()<0){
                    misilEnemigo[i].setPositionX(navesEnemigas[i].getPositionX());
                    misilEnemigo[i].setPositionY(navesEnemigas[i].getPositionY());
                }
            }

            if( disparar ){
                misil.setPositionY(nave.getPositionY()+50);
                misil.setPositionX(200);
                disparar=false;
            }

            for(int i=0;i<this.level;i=i+1) {
                if((misilEnemigo[i].getPositionY() +20> nave.getPositionY() && misilEnemigo[i].getPositionY() -20 < nave.getPositionY() )&& (misilEnemigo[i].getPositionX() +20> nave.getPositionX() && misilEnemigo[i].getPositionX() -20 < nave.getPositionX()) ){
                    misilEnemigo[i].setPositionX(navesEnemigas[i].getPositionX());
                    misilEnemigo[i].setPositionY(navesEnemigas[i].getPositionY());
                }
            }

            for(int i=0; i < this.level ;i=i+1) {
                this.score+= asteroides[i].updateInfo(nave.getPositionX(), nave.getPositionY(),level,(int)misil.getPositionX(),(int)misil.getPositionY());
            }
            for(int i=0; i < this.level ;i=i+1) {
                this.score+= navesEnemigas[i].updateInfo(nave.getPositionX(), nave.getPositionY(),level,(int)misil.getPositionX(),(int)misil.getPositionY());
            }
            for(int i=0; i < this.level ;i=i+1) {
                this.score+= misilEnemigo[i].updateInfo(nave.getPositionX(), nave.getPositionY(),level);
            }


        }else {
            score=0;
            level=0;
            times=0;

            for (int i=0;i<asteroides.length;i=i+1){
                asteroides[i].setPositionX(screenWith);
            }
            for (int i=0;i<asteroides.length;i=i+1){
                navesEnemigas[i].setPositionX(screenWith);
            }
            for (int i=0;i<asteroides.length;i=i+1){
                misilEnemigo[i].setPositionX(screenWith);
            }

        }
        if((score/20)+1>level){
            level=(score/150)+1;
        }
    }

    private void paintFrame() {
        if (holder.getSurface().isValid()){

            canvas = holder.lockCanvas();
            canvas.drawColor(Color.BLACK);
            canvas.drawText("Puntos "+this.score+"", 40,40,paint);
            canvas.drawText("Nivel "+this.level+"", 40,80,paint);
            if(!active){canvas.drawText("Iniciar", 300,300,paint);}
            paint.setTextSize(40);
            bgImage = BitmapFactory.decodeResource(getResources(),R.drawable.bg);
            canvas.drawBitmap(nave.getSpritenave(),nave.getPositionX(),nave.getPositionY(),paint);
            canvas.drawBitmap(misil.getSpritemisil(),misil.getPositionX(),misil.getPositionY(),paint);

            for(int i=0;i<this.level;i=i+1) {
                canvas.drawBitmap(asteroides[i].getSpriteasteroide(),asteroides[i].getPositionX(),asteroides[i].getPositionY(),paint);
            }
            for(int i=0;i<this.level;i=i+1) {
                canvas.drawBitmap(navesEnemigas[i].getSpriteNaveEnemiga(),navesEnemigas[i].getPositionX(),navesEnemigas[i].getPositionY(),paint);
            }
            for(int i=0;i<this.level;i=i+1) {
                canvas.drawBitmap(misilEnemigo[i].getSpriteMisilEnemigo(),misilEnemigo[i].getPositionX(),misilEnemigo[i].getPositionY(),paint);
            }
            holder.unlockCanvasAndPost(canvas);
        }
    }

    public void pause() {
        isPlaying = false;
        try {
            gameplayThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resume() {
        isPlaying = true;
        gameplayThread = new Thread(this);
        gameplayThread.start();
    }

    /**
     * Detect the action of the touch event
     * @param motionEvent
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        this.active=true;

        float x = motionEvent.getX();
        float y = motionEvent.getY();

        if (x >= 0 && x <=100) {
            disparar=true;
            return true;
        }

        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                nave.setJumping(false);
                break;
            case MotionEvent.ACTION_DOWN:
                nave.setJumping(true);
                break;
        }
        return true;
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:
                    return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }
}
