package jp.ac.ichinoseki.cc.club.procon2014_kyogi_demo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
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
    private int chooseCost;
    private int swapCost;
    private int totalChooseCost;
    private int totalSwapCost;
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
    private int touchPositionNumber;
    private boolean endFlag;
    private int clearFlag = 0;

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

    private int getRandomDrawable(int n) {
        Random rand = new Random();

        switch (rand.nextInt(n)) {
            case 0:
                return R.drawable.puzzle1;
            case 1:
                return R.drawable.puzzle2;
            case 2:
                return R.drawable.puzzle3;
            case 3:
                return R.drawable.puzzle4;
        }

        return R.drawable.puzzle1;
    }

    public void initPuzzle() {
        holder = this.getHolder();
        holder.addCallback(this);

        Random rand = new Random();
        chooseCost = rand.nextInt(30) * 10 + 10;
        swapCost = rand.nextInt(10) * 10 + 10;
        totalChooseCost = 0;
        totalSwapCost = 0;
        splitWidth = rand.nextInt(3) + 2;
        splitHeight = rand.nextInt(3) + 2;

        new AlertDialog.Builder(getContext())
                .setTitle("パズル情報")
                .setMessage(splitHeight + "x" + splitWidth + "のパズル" + "\n選択コスト: " + chooseCost + "\n交換コスト: " + swapCost)
                .setPositiveButton(
                        "OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                .show();

        res = getResources();
        Bitmap image = BitmapFactory.decodeResource(res, getRandomDrawable(4));

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
        paint.setTextSize(40);
        paint.setStrokeWidth(10);
        float[] arr = new float[]{0, 60, 800, 60, 800, 60, 800, 120, 800, 120, 0, 120};
        canvas.drawLines(arr, paint);
        float[] arr2 = new float[] {0, 120, 800, 120, 800, 120, 800, 180, 800, 180, 0, 180};
        canvas.drawLines(arr2, paint);
        canvas.drawText("合計選択コスト: " + Integer.toString(totalChooseCost), 60, 100, paint);
        canvas.drawText("合計交換コスト: " + Integer.toString(totalSwapCost), 60, 160, paint);

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

        if (clearFlag == 1) {
            new AlertDialog.Builder(getContext())
                    .setTitle("クリアしました")
                    .setPositiveButton(
                            "OK",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                    .show();
            clearFlag = 2;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x, y;

        x = (int)event.getRawX();
        y = (int)event.getRawY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                int nx = xRawSize;
                int ny = drowY + yRawSize;
                touchPositionNumber = 0;
                endFlag = false;
                for (int i = 0; i < splitHeight; i++) {
                    for (int j = 0; j < splitWidth; j++) {
                        if (x < nx && y < ny) {
                            endFlag = true;
                            break;
                        }
                        touchPositionNumber += 1;
                        nx += xRawSize;
                    }
                    if (endFlag) break;

                    nx = xRawSize;
                    ny += yRawSize;
                }

                if (endFlag) {
                    totalChooseCost += chooseCost;
                }

                drawPuzzle();

                break;

            case MotionEvent.ACTION_MOVE:
                if (!endFlag) break;

                int diffX = xRawSize;
                int diffY = drowY + yRawSize;
                int diffTouchPositionNumber = 0;
                boolean diffEndFlag = false;
                for (int i = 0; i < splitHeight; i++) {
                    for (int j = 0; j < splitWidth; j++) {
                        if (x < diffX && y < diffY) {
                            diffEndFlag = true;
                            break;
                        }
                        diffTouchPositionNumber += 1;
                        diffX += xRawSize;
                    }
                    if (diffEndFlag) break;

                    diffX = xRawSize;
                    diffY += yRawSize;
                }

                if (diffEndFlag && touchPositionNumber != diffTouchPositionNumber) {
                    int tmp;
                    tmp = shuffleList[touchPositionNumber];
                    shuffleList[touchPositionNumber] = shuffleList[diffTouchPositionNumber];
                    shuffleList[diffTouchPositionNumber] = tmp;

                    touchPositionNumber = diffTouchPositionNumber;
                    totalSwapCost += swapCost;
                    drawPuzzle();
                }
                break;

            case MotionEvent.ACTION_UP:
                if (!endFlag) break;

                if (clearFlag == 0) {
                    int totalSplitNumber = splitHeight * splitWidth;
                    clearFlag = 1;
                    for (int i = 0; i < totalSplitNumber; i++) {
                        if (shuffleList[i] != i) {
                            clearFlag = 0;
                        }
                    }

                }

                drawPuzzle();

                break;
        }

        return true;
    }
}
