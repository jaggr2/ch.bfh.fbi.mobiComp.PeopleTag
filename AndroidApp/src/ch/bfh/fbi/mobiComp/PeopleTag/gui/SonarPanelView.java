package ch.bfh.fbi.mobiComp.PeopleTag.gui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.util.AttributeSet;
import android.view.View;
import ch.bfh.fbi.mobiComp.PeopleTag.model.UserData;

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

    private static int SCALE_FACTOR = 3;
    private int midPos = 170*SCALE_FACTOR;
    private int radius = 150*SCALE_FACTOR;
    private int SPACER_LARGE = 30*SCALE_FACTOR;
    private int SPACER_SMALL = 10*SCALE_FACTOR;
    @Override
    public void onDraw(Canvas canvas) {
        drawSonarBackground(canvas);
        drawTargetLines(canvas);
        drawUserPosition(canvas, null, null, 1111111111);
    }

    private void drawSonarBackground(Canvas canvas) {
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(3);
        canvas.drawCircle(midPos, midPos, radius, paint);
        paint.setColor(Color.BLACK);
        canvas.drawCircle(midPos, midPos, (radius-SPACER_SMALL), paint);
        paint.setColor(Color.GREEN);
        canvas.drawCircle(midPos, midPos, (radius-(SPACER_SMALL+SPACER_LARGE)), paint);
        paint.setColor(Color.BLACK);
        canvas.drawCircle(midPos, midPos, (radius-((2*SPACER_SMALL)+(SPACER_LARGE))), paint);
        paint.setColor(Color.GREEN);
        canvas.drawCircle(midPos, midPos, (radius-((2*SPACER_SMALL)+(2*SPACER_LARGE))), paint);
        paint.setColor(Color.BLACK);
        canvas.drawCircle(midPos, midPos, (radius-((3*SPACER_SMALL)+(2*SPACER_LARGE))), paint);
        paint.setColor(Color.GREEN);
        canvas.drawCircle(midPos,midPos, (radius-((3*SPACER_SMALL)+(3*SPACER_LARGE))),paint);
    }

    private void drawTargetLines(Canvas canvas) {
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(20);
        canvas.drawLine(midPos,(2*midPos),midPos,0,paint);
        canvas.drawLine(0,midPos,(2*midPos),midPos,paint);
    }

    // Draw userposition if distanceToUser is in range..
    private void drawUserPosition(Canvas canvas, UserData userLoc, Location currentLocation, int resolutionInMeter) {
       if(userLoc != null && userLoc.getDistanceToUserLocation(currentLocation) < resolutionInMeter) {
            // userLoc.getAngleFromCurrentLocationToUserLoaction();
            paint.setColor(Color.RED);
            paint.setStrokeWidth(6 * SCALE_FACTOR);
            canvas.drawPoint((30 * SCALE_FACTOR), (70 * SCALE_FACTOR), paint);
        }
    }

}