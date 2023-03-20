package com.example.testn2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import com.example.testn2.BTRW;

public class DrawingView extends View {
    private final int paintColor = Color.BLACK;
    private Paint drawPaint;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    float pointX;
    float pointY;

    public int x,y;
    Point2D p1 = null;
    Point2D p2 = null;
    View mDrawing = findViewById(R.id.mdraw);

    BTRW extraUse = new BTRW();



    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setupPaint();

    }

    public class Point2D{
        float x,y;
        Point2D(float x1, float y1){
            x = x1; y = y1;
        }
        float getX(){
            return x;
        }
        float getY(){
            return y;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }
    private void setupPaint() {
        // Setup paint with color and stroke styles
        drawPaint = new Paint();
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(10);
        drawPaint.setStyle(Paint.Style.FILL);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        x = (int)event.getX();
        y = (int) event.getY();

        int y1 = 1152*(int) event.getY()/mDrawing.getHeight();//结果归化到1080*1920
        int x1 = 576*(int) event.getX()/mDrawing.getWidth();

        if(x1>=576){x1=576;}if(x1<0){x1=0;}
        if(y1>=1152){y1=1152;}if(y1<0){y1=0;}//越界


        pointX = event.getX();
        pointY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.e("touch", "起始位置x:" + x1 + ",y:" + y1);
                p1 = new Point2D(pointX, pointY);
                extraUse.getParam(x1,y1,1);
                break;
            case MotionEvent.ACTION_MOVE:
                Log.e("touch", "当前位置x:" + x1 + ",y:" + y1);
                break;
            case MotionEvent.ACTION_UP:
                Log.e("touch", "结束位置x:" + x1 + ",y:" + y1);
                p2 = new Point2D(pointX, pointY);
                extraUse.getParam(x1,y1,0);
                postInvalidate();
                break;
            default:
                return false;
        }
        return true;
    }

    public void clear(){
        setDrawingCacheEnabled(false);
        onSizeChanged(getWidth(),getHeight(),getWidth(),getHeight());
        invalidate();
        setDrawingCacheEnabled(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(p1!=null && p2!=null) {
            canvas.drawRect(p1.getX(), p1.getY(), p2.getX(), p2.getY(), drawPaint);
            p1=null;
            p2=null;
        }
    }



}