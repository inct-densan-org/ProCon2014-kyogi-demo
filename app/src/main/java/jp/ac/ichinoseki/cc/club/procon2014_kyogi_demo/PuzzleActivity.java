package jp.ac.ichinoseki.cc.club.procon2014_kyogi_demo;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.sql.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;


public class PuzzleActivity extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder holder;
    private Resources res;
    private WindowManager wm;
    private int splitWidth;
    private int splitHeight;
    private Bitmap[] puzzleList;
    private int[] puzzleListNumber;
    private Integer[] shuffleList;
    private int puzzleWidth;
    private int puzzleHeight;
    private int drowX;
    private int drowY;
    private int xRawSize;
    private int yRawSize;

    public PuzzleActivity(Context context) {
        super(context);
        wm  = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        initPuzzle();
    }

    public PuzzleActivity(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        wm  = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        initPuzzle();
    }

    public PuzzleActivity(Context context, AttributeSet attrs) {
        super(context, attrs);
        wm  = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        initPuzzle();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        drawPuzzle();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        drawPuzzle();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    public void initPuzzle() {
        holder = this.getHolder();
        holder.addCallback(this);

        Random rand = new Random();
        splitWidth = 3; // rand.nextInt(13) + 3;
        splitHeight = 3; // rand.nextInt(13) + 3;

        res = getResources();
        Bitmap image = BitmapFactory.decodeResource(res, R.drawable.puzzle1);

        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();

        if (imageWidth > imageHeight) {
            image = Bitmap.createBitmap(image, 0, 0, imageHeight, imageHeight);
        }
        else {
            image = Bitmap.createBitmap(image, 0, 0, imageWidth, imageWidth);
        }

        Display display = wm.getDefaultDisplay();
        Point p = new Point();
        display.getSize(p);

        puzzleWidth = p.x - (p.x % splitWidth);
        puzzleHeight = p.x - (p.x % splitHeight);
        drowY = p.y / 2 - puzzleHeight / 2;

        image = Bitmap.createScaledBitmap(image, puzzleWidth, puzzleHeight, false);

        int totalSplitNumber = splitWidth * splitHeight;
        puzzleList = new Bitmap[totalSplitNumber];
        puzzleListNumber = new int[totalSplitNumber];
        shuffleList = new Integer[totalSplitNumber];

        int x = 0, y = 0;
        xRawSize = p.x / splitWidth;
        yRawSize = p.x / splitHeight;
        for (int i = 0; i < totalSplitNumber; i++) {
            if (i > 0 && i % splitWidth == 0) {
                x = 0;
                y += yRawSize;
            }
            else if (i > 0) {
                x += xRawSize;
            }
            puzzleList[i] = Bitmap.createBitmap(image, x, y, xRawSize, yRawSize);
            puzzleListNumber[i] = i;
            shuffleList[i] = i;
        }

        List<Integer> list  = Arrays.asList(shuffleList);
        Collections.shuffle(list);
        shuffleList = (Integer[])list.toArray(new Integer[list.size()]);
    }

    public void drawPuzzle() {
        Canvas canvas = holder.lockCanvas();

        canvas.drawColor(Color.WHITE);
        int totalSplitNumber = splitWidth * splitHeight;
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(20);
        drowX = 0;
        int x = 0, y = drowY;
        for(int i = 0; i < totalSplitNumber; i++) {
            if(i > 0 && i % splitWidth == 0) {
                x = 0;
                y += yRawSize;
            }
            else if (i > 0){
                x += xRawSize;
            }
            canvas.drawBitmap(puzzleList[shuffleList[i]], x, y, paint);
        }

        holder.unlockCanvasAndPost(canvas);
    }
}
