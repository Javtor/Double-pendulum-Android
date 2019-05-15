package com.javiertorres.doublependulumandroid.threads;


import android.graphics.Canvas;
import android.view.SurfaceHolder;

import com.javiertorres.doublependulumandroid.CanvasView;

public class TimeThread extends Thread {

    public static Canvas canvas;
    private SurfaceHolder surfaceHolder;
    private CanvasView canvasView;
    private double deltaTime;
    private boolean playing;

    public TimeThread(SurfaceHolder surfaceHolder, CanvasView canvasView) {
        super();
        this.surfaceHolder = surfaceHolder;
        this.canvasView = canvasView;
        this.playing = true;
        deltaTime = 0;
    }

    @Override
    public void run() {
        playing = true;
        long t1 = System.currentTimeMillis();
        long t2 = System.currentTimeMillis();
        while (playing) {
            canvas = null;
            t1 = t2;
            t2 = System.currentTimeMillis();
            deltaTime = (t2 - t1) / 1000.0;

            try {
                canvas = this.surfaceHolder.lockCanvas();
                synchronized (surfaceHolder) {
                    this.canvasView.step(deltaTime);
                    this.canvasView.draw(canvas);
                }
            } catch (Exception e) {
            } finally {
                if (canvas != null) {
                    try {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    public boolean isPlaying() {
        return playing;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

}
