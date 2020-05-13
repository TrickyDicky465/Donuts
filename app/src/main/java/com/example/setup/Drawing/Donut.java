package com.example.setup.Drawing;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposePathEffect;
import android.graphics.CornerPathEffect;
import android.graphics.DiscretePathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Region;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Random;

public class Donut extends View {

    final String TAG = "Donuts";

    private Integer mImageSize = 400;
    private Rect mBounds;
    private Integer mSprinkleCount = 40;
    private ArrayList<Sprinkle> mSprinkleList;
    private Integer mImageRadius;
    private Integer mHoleRadius;
    private Integer mIcingPadding;
    private Integer mDonutBaseColour = 0xFFc6853b;
    private Integer mDonutIcingColour = 0xFF53250F;

    public class Sprinkle {
        int color;
        float angle;
        float distance;
        float rotation;

        public String toString() {
            String result = "Color = " + color + " Angle = " + angle + " Distance = " + distance + " Rotation = " + rotation;
            return result;
        }
    }

    int SprinkleColors[] = { Color.RED, Color.WHITE, Color.YELLOW, Color.BLUE, Color.CYAN, Color.GREEN, Color.MAGENTA };

    public Donut(Context context) {
        super(context);

        init(null);
    }

    public Donut(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(attrs);
    }

    public Donut(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(attrs);
    }

    public Donut(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init(attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawDonut(canvas);
    }

    private void init(@Nullable AttributeSet set) {

    }

    private void drawBase( Canvas canvas, int color ) {

        Paint paint = new Paint();
        paint.setColor(color);

        canvas.drawCircle( mBounds.centerX(), mBounds.centerY(), mImageRadius, paint );
    }

    private void drawIcing( Canvas canvas, int color ) {
        //DashPathEffect dash = new DashPathEffect(new float[]{5, 5, 5, 5}, 1);

        Paint icing = new Paint(Paint.ANTI_ALIAS_FLAG);

        CornerPathEffect cornerPath = new CornerPathEffect(80f);
        DiscretePathEffect discreetPath = new DiscretePathEffect(60f, 25f);
        ComposePathEffect pathEffect = new ComposePathEffect( cornerPath, discreetPath);

        icing.setPathEffect(pathEffect);
        icing.setColor(color);

        canvas.drawCircle( mBounds.centerX(), mBounds.centerY(), mImageRadius-mIcingPadding, icing );
    }

    private void clipHole(Canvas canvas) {

        Path hole = new Path();
        hole.addCircle( mBounds.centerX(), mBounds.centerY(), mHoleRadius, Path.Direction.CW);
        canvas.clipPath(hole, Region.Op.DIFFERENCE);
    }

    private ArrayList<Sprinkle> generateSprinkles() {

        Random ranGen = new Random();
        ArrayList<Sprinkle> sprinkleList = new ArrayList<Sprinkle>();

        for (int idx=0; idx<mSprinkleCount; ++idx)
        {
            Sprinkle sprinkle = new Sprinkle();
            sprinkle.color = SprinkleColors[idx % SprinkleColors.length];
            sprinkle.angle = ranGen.nextFloat() * 360f;
            sprinkle.distance = ranGen.nextFloat();
            sprinkle.rotation = ranGen.nextFloat() * 360f;

            Log.e(TAG,"Sprinkle [" + idx + "] " + sprinkle.toString());
            sprinkleList.add(sprinkle);
        }
        return sprinkleList;
    }

    private void drawSprinkles(Canvas canvas)  {

        mSprinkleList = generateSprinkles();
        int cx = mBounds.centerX();
        int cy = mBounds.centerY();
        Paint sprinklePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        for (Sprinkle it : mSprinkleList) {
            canvas.save();                                                                                                              // Save the canvas

            canvas.rotate(it.angle, cx,cy);                                                                                             // Rotate the entire canvas around the centre

            float modDistance = mHoleRadius + mIcingPadding + ((mImageRadius-mHoleRadius-mIcingPadding) * it.distance);
            canvas.translate(0f, modDistance );                                                                                     // move outwards from the centre to draw
            canvas.rotate(it.rotation + 360f, cx, cy );                                                                         // Rotate by the sprinkle angle
            sprinklePaint.setColor(it.color);
            canvas.drawRoundRect(cx - 7f, cy - 22f, cx + 7f, cy + 22f, 10f, 10f, sprinklePaint);         // Draw the sprinkle as a rounded rectangle

            canvas.restore();                                                                                                           // Restore the canvas
        }
    }

    private void drawDonut(Canvas canvas) {

        int saveState = canvas.getSaveCount();

        mBounds = canvas.getClipBounds();
        mImageRadius = (mBounds.width() - mBounds.centerX()) / 2;
        mHoleRadius = mImageRadius / 3;
        mIcingPadding = 20;

        clipHole(canvas);
        drawBase(canvas, mDonutBaseColour);
        drawIcing(canvas, mDonutIcingColour);
        drawSprinkles(canvas);

        canvas.restoreToCount(saveState);
    }


}
