package com.javiertorres.doublependulumandroid;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import com.javiertorres.doublependulumandroid.model.DoublePendulum;
import com.javiertorres.doublependulumandroid.threads.TimeThread;

public class CanvasView extends SurfaceView implements Callback {

    public static final float RADIUS = 25;

    private TimeThread thread;
    private MainActivity mainActivity;
    private DoublePendulum doublePendulum;


    public CanvasView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        getHolder().addCallback(this);
        setFocusable(true);
    }

    public void initialize() {
        mainActivity.defaultParameters();
        initPendulum();

        Canvas canvas = null;
        try {
            canvas = getHolder().lockCanvas();
            draw(canvas);
        } catch (Exception e) {
        } finally {
            if (canvas != null) {
                try {
                    getHolder().unlockCanvasAndPost(canvas);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        thread = new TimeThread(getHolder(), this);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Canvas canvas = null;
        try {
            canvas = getHolder().lockCanvas();
            draw(canvas);
        } catch (Exception e) {
        } finally {
            if (canvas != null) {
                try {
                    getHolder().unlockCanvasAndPost(canvas);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        while (retry) {
            try {
                thread.setPlaying(false);
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            retry = false;
        }
    }

    public void step(double deltaTime) {
        doublePendulum.step(deltaTime);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (canvas != null) {
            float x1 = (float) doublePendulum.getX1();
            float y1 = (float) -doublePendulum.getY1();
            float x2 = (float) doublePendulum.getX2();
            float y2 = (float) -doublePendulum.getY2();
            drawPend(x1, y1, x2, y2, canvas);
        }
    }

    private void drawPend(float rawX1, float rawY1, float rawX2, float rawY2, Canvas canvas) {
        canvas.drawColor(Color.WHITE);
        Paint paint = new Paint();

        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(4);

        float width = (float) getWidth() / 2;
        float height = (float) getHeight() / 2;

        rawX1 *= (height / 2) / doublePendulum.getL1();
        rawY1 *= (height / 2) / doublePendulum.getL1();

        rawX2 *= (height / 2) / doublePendulum.getL1();
        rawY2 *= (height / 2) / doublePendulum.getL1();

        rawX1 += width;
        rawX2 += width;

        float x1 = rawX1;
        float y1 = rawY1;
        float x2 = rawX2;
        float y2 = rawY2;

        canvas.drawLine(width, height * 1 / 2, rawX1, rawY1 + height * 1 / 2, paint);
        canvas.drawLine(rawX1, rawY1 + height * 1 / 2, rawX2, rawY2 + height * 1 / 2, paint);
        paint.setColor(Color.RED);
        canvas.drawCircle(x1, y1 + height * 1 / 2, RADIUS, paint);
        paint.setColor(Color.BLUE);
        canvas.drawCircle(x2, y2 + height * 1 / 2, RADIUS, paint);
    }


    public void start() {
        try {
            stop();
            initPendulum();
            thread = new TimeThread(getHolder(), this);
            thread.start();
        } catch (NumberFormatException e) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage(getContext().getString(R.string.check_inputs))
                    .setCancelable(false)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(android.R.string.dialog_alert_title)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //do things
                        }
                    });

            AlertDialog alert = builder.create();
            alert.show();


        }

    }

    private void initPendulum() {
        mainActivity.getData();
    }

    public void stop() {
        thread.setPlaying(false);
    }

    public MainActivity getMainActivity() {
        return mainActivity;
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }


    public void initPendulum(double angle, double velocity, double length, double mass,
                             double angle2, double velocity2, double length2, double mass2,
                             double gravity, double friction) {
        doublePendulum = new DoublePendulum(angle, velocity, length, mass,
                angle2, velocity, length2, mass2,
                gravity, friction);
    }
}
