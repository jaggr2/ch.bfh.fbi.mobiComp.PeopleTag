package ch.bfh.fbi.mobiComp.PeopleTag.gui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by heroku on 28.03.14.
 */
public class SonarPanelView extends View {
    Paint paint = new Paint();

    public SonarPanelView(Context context) {
        super(context);
    }
    public SonarPanelView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SonarPanelView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onDraw(Canvas canvas) {
        drawSonarBackground(canvas);

    }

    private void drawSonarBackground(Canvas canvas) {
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(3);
        canvas.drawCircle(170,170,150,paint);
        paint.setColor(Color.BLACK);
        canvas.drawCircle(170,170,140,paint);
        paint.setColor(Color.GREEN);
        canvas.drawCircle(170,170,110,paint);
        paint.setColor(Color.BLACK);
        canvas.drawCircle(170,170,100,paint);
        paint.setColor(Color.GREEN);
        canvas.drawCircle(170,170,70,paint);
        paint.setColor(Color.BLACK);
        canvas.drawCircle(170,170,60,paint);
        paint.setColor(Color.GREEN);
        canvas.drawCircle(170,170,30,paint);
    }
}