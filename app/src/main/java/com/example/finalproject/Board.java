package com.example.finalproject;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

/**
 * finalproject created by EvaHu and SiyuanDing
 */
public class Board extends View {

    private int boardWidth;
    private float boardHeight;
    private int MAX_LINE = 12;
    private int MAX_COUNT = 6;

    private Paint paint = new Paint();

    private Bitmap whiteCat;
    private Bitmap blackCat;

    private float pieceScaleRatio = 3 * 1.0f / 4;

    private ArrayList<Point> whiteCatArray = new ArrayList<>();
    private ArrayList<Point> blackCatArray = new ArrayList<>();
    private boolean whiteCatFirst = true;

    private boolean gameOver;
    private boolean whiteCatWins;

    public Board(Context context, AttributeSet attrs) {
        super(context, attrs);
        initial();
    }

    private void initial() {
        paint.setColor(0x88000000);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStyle(Paint.Style.STROKE);
        whiteCat = BitmapFactory.decodeResource(getResources(), R.drawable.whitecat);
        blackCat = BitmapFactory.decodeResource(getResources(), R.drawable.blackcat);
    }

    @Override
    protected void onMeasure(int setWidth, int setHeight) {
        int widthSize = MeasureSpec.getSize(setWidth);
        int widthMode = MeasureSpec.getMode(setWidth);
        int heightSize = MeasureSpec.getSize(setHeight);
        int heightMode = MeasureSpec.getMode(setHeight);
        int width = Math.min(widthSize, heightSize);
        if (widthMode == MeasureSpec.UNSPECIFIED){
            width = heightSize;
        } else if (heightMode == MeasureSpec.UNSPECIFIED) {
            width = widthSize;
        }
        setMeasuredDimension(width,width);
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        boardWidth = width;
        boardHeight = boardWidth * 1.0f /  MAX_LINE;
        int pieceWidth = (int)(boardHeight * pieceScaleRatio);
        whiteCat = Bitmap.createScaledBitmap(whiteCat, pieceWidth, pieceWidth, false);
        blackCat = Bitmap.createScaledBitmap(blackCat, pieceWidth, pieceWidth, false);
    }

    private void drawBoard(Canvas canvas) {
        int width = boardWidth;
        float lineLength = boardHeight;
        for (int i = 0; i < MAX_LINE; i++) {
            int startX= (int)(lineLength / 2);
            int endX = (int)(width - lineLength / 2);
            int y = (int)((0.5 + i) * lineLength);
            canvas.drawLine(startX, y, endX, y, paint);
            canvas.drawLine(y, startX, y, endX, paint);
        }
    }

    private void drawPieces(Canvas canvas) {
        for (int i = 0, n = whiteCatArray.size(); i < n; i++) {
            Point whitePiece = whiteCatArray.get(i);
            canvas.drawBitmap(whiteCat,
                    (whitePiece.x+(1-pieceScaleRatio)/2)*boardHeight,
                    (whitePiece.y+(1-pieceScaleRatio)/2)*boardHeight, null);
        }
        for (int i = 0, n = blackCatArray.size(); i < n; i++) {
            Point blackPiece = blackCatArray.get(i);
            canvas.drawBitmap(blackCat,
                    (blackPiece.x+(1-pieceScaleRatio)/2)*boardHeight,
                    (blackPiece.y+(1-pieceScaleRatio)/2)*boardHeight, null);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (gameOver) {
            return false;
        }
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            Point piece = getValidPoint(x, y);

            if (whiteCatArray.contains(piece) || blackCatArray.contains(piece)) {
                return false;
            }
            if (whiteCatFirst) {
                whiteCatArray.add(piece);
            } else {
                blackCatArray.add(piece);
            }
            invalidate();
            whiteCatFirst = !whiteCatFirst;
            return true;
        }
        return super.onTouchEvent(event);
    }

    private Point getValidPoint(int x, int y){
        return new Point((int) (x/boardHeight), (int) (y/boardHeight));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBoard(canvas);
        drawPieces(canvas);
        checkGameOver();
    }

    private void checkGameOver() {
        boolean whiteWin = checkSixInLine(whiteCatArray);
        boolean blackWin = checkSixInLine(blackCatArray);
        if (whiteWin || blackWin) {
            gameOver = true;
            whiteCatWins = whiteWin;
            String text = whiteCatWins ? "whiteWins" : "blackWins";
            Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkSixInLine(List<Point> points) {
        for(Point piece : points) {
            int x = piece.x;
            int y = piece.y;
            boolean win = checkHorizontal(x, y, points);
            if (win) {
                return true;
            }
            win = checkVertical(x, y, points);
            if (win) {
                return true;
            }
            win = checkLeftDiagonal(x, y, points);
            if (win) {
                return true;
            }
            win = checkRightDiagonal(x, y, points);
            if (win) {
                return true;
            }
        }
        return false;
    }

    /**
     * check whether there are same six pieces in a horizontal line.
     * @param x the points position in the X axis
     * @param y the points position in the Y axis
     * @param points the points
     * @return whether there are same six pieces in a horizontal line or not.
     */
    private boolean checkHorizontal(int x, int y, List<Point> points) {
        int count = 1;
        for (int i = 1; i < MAX_COUNT; i++) {
            if (points.contains(new Point(x-i, y))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT) {
            return true;
        }
        for (int i = 1; i < MAX_COUNT; i++) {
            if (points.contains(new Point(x+i, y))) {
                count++;
            } else {
                break;
            }
        }
        if (count >= MAX_COUNT) {
            return true;
        }
        return false;
    }

    /**
     * check whether there are same six pieces in a vertical line.
     * @param x the points position in the X axis
     * @param y the points position in the Y axis
     * @param points the points
     * @return whether there are same six pieces in a vertical line or not.
     */
    private boolean checkVertical(int x, int y, List<Point> points) {
        int count = 1;
        for (int i = 1; i < MAX_COUNT; i++){
            if (points.contains(new Point(x, y-i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT) {
            return true;
        }
        for (int i = 1; i < MAX_COUNT; i++) {
            if (points.contains(new Point(x, y+i))) {
                count++;
            } else {
                break;
            }
        }
        if (count >= MAX_COUNT) {
            return true;
        }
        return false;
    }

    /**
     * check whether there are same pieces in a left diagonal line.
     * @param x the points position in the X axis
     * @param y the points position in the Y axis
     * @param points the points
     * @return whether there are same six pieces in a left diagonal line or not.
     */
    private boolean checkLeftDiagonal(int x, int y, List<Point> points) {
        int count = 1;
        for (int i = 1; i < MAX_COUNT; i++){
            if (points.contains(new Point(x-i, y+i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT) {
            return true;
        }
        for (int i=1; i<MAX_COUNT; i++) {
            if (points.contains(new Point(x+i, y-i))) {
                count++;
            } else {
                break;
            }
        }
        if (count >= MAX_COUNT) {
            return true;
        }
        return false;
    }

    /**
     * check whether there are same pieces in a right diagonal line.
     * @param x the points position in the X axis
     * @param y the points position in the Y axis
     * @param points the points
     * @return whether there are same six pieces in a right diagonal line or not.
     */
    private boolean checkRightDiagonal(int x, int y, List<Point> points) {
        int count = 1;
        for (int i = 1; i < MAX_COUNT; i++){
            if (points.contains(new Point(x+i, y+i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT) {
            return true;
        }
        for (int i=1; i<MAX_COUNT; i++) {
            if (points.contains(new Point(x-i, y-i))) {
                count++;
            } else {
                break;
            }
        }
        if (count >= MAX_COUNT) {
            return true;
        }
        return false;
    }

    private static final String INSTANCE = "instance";
    private static final String INSTANCE_GAME_OVER = "instance_game_over";
    private static final String INSTANCE_WHITE_ARRAY = "instance_white_array";
    private static final String INSTANCE_BLACK_ARRAY = "instance_black_array";

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE, super.onSaveInstanceState());
        bundle.putBoolean(INSTANCE_GAME_OVER, gameOver);
        bundle.putParcelableArrayList(INSTANCE_WHITE_ARRAY, whiteCatArray);
        bundle.putParcelableArrayList(INSTANCE_BLACK_ARRAY, blackCatArray);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle){
            Bundle bundle = (Bundle) state;
            gameOver = bundle.getBoolean(INSTANCE_GAME_OVER);
            whiteCatArray = bundle.getParcelableArrayList(INSTANCE_WHITE_ARRAY);
            blackCatArray = bundle.getParcelableArrayList(INSTANCE_BLACK_ARRAY);
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE));
            return ;
        }
        super.onRestoreInstanceState(state);
    }
}
