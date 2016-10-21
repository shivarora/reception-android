package com.merlinbusinesssoftware.merlinsignin;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridView;
import android.widget.SectionIndexer;

/**
 * Created by aroras on 05/08/16.
 */
public class SideSelector extends View {
    private static String TAG = SideSelector.class.getCanonicalName();

    public static char[] ALPHABET = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
    public static final int BOTTOM_PADDING = 5;

    private SectionIndexer selectionIndexer = null;
    private GridView list;
    private Paint paint;
    private String[] sections;

    public SideSelector(Context context) {
        super(context);
        init();
    }

    public SideSelector(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SideSelector(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setBackgroundColor(0x44FFFFFF);
        paint = new Paint();
        paint.setColor(0xFFA6A9AA);
        paint.setTextSize(20);
        paint.setTextAlign(Paint.Align.CENTER);
    }

    public void setGridView(GridView _list) {
        list = _list;
        selectionIndexer = (SectionIndexer) _list.getAdapter();

        Object[] sectionsArr = selectionIndexer.getSections();
        sections = new String[sectionsArr.length];
        for (int i = 0; i < sectionsArr.length; i++) {
            sections[i] = sectionsArr[i].toString();

            System.out.println("this is the section  for "+ i + " with value " + sections[i]);
        }

    }

    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        int y = (int) event.getY();

        System.out.println("this is Y value for touch event" + y);
        //float selectedIndex = ((float) y / (float) getPaddedHeight()) * ALPHABET.length;
        float selectedIndex = ((float) y ) *  ALPHABET.length;

        if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
            if (selectionIndexer == null) {
                selectionIndexer = (SectionIndexer) list.getAdapter();
            }
            int position = selectionIndexer.getPositionForSection((int) selectedIndex);
            if (position == -1) {
                return true;
            }
            list.setSelection(position);
        }
        return true;
    }


    public void  onClick(String val){

        System.out.println("thisis the val after clikc" + val );
    }

    protected void onDraw(Canvas canvas) {
        int viewHeight = 0;
        float charHeight = (float) 20.0;
        float widthCenter = (float) 0.0;

        paint.setTextAlign(Paint.Align.LEFT);
        for (int i = 0; i < sections.length; i++) {
            canvas.drawText(String.valueOf(sections[i]), widthCenter, charHeight , paint);
            widthCenter = (float) (widthCenter + 35.0);
        }
        super.onDraw(canvas);
    }

    private int getPaddedHeight() {
        return getHeight() - BOTTOM_PADDING;
    }
}
