package com.merlinbusinesssoftware.merlinsignin;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;

/**
 * Created by aroras on 04/08/16.
 */
public class ColorTransformation implements com.squareup.picasso.Transformation {

    private final int radius;
    private final int margin;  // dp

    // radius is corner radii in dp
    // margin is the board in dp
    public ColorTransformation(final int radius, final int margin) {
        this.radius = radius;
        this.margin = margin;

    }

    @Override
    public Bitmap transform(final Bitmap source) {

        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));

        Bitmap output = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        canvas.drawRoundRect(new RectF(margin, margin, source.getWidth() - margin, source.getHeight() - margin), radius, radius, paint);

        Paint paint1 = new Paint();
        paint1.setColor(Color.GREEN);
        paint1.setStyle(Paint.Style.STROKE);
        paint1.setAntiAlias(true);
        paint1.setStrokeWidth(6);
        canvas.drawRect(new RectF(25, margin, 173, source.getHeight() - margin),  paint1);

        if (source != output) {
            source.recycle();
        }

        return output;
    }

    @Override
    public String key() {
        return "rounded";
    }
}
