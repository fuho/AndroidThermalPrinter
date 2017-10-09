package co.dotko.androidthermalprintersample;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

/*******************************************************************************
 * Class inherited halftone that perform halftoning on a Bitmap image with diamond
 * shapes
 *
 * @author Milena Mitic
 * @since May 2014
 * @knownBugs none
 *
 ******************************************************************************/
public class HalftoneDiamond extends Halftone {

    public HalftoneDiamond(final Bitmap src) {
        super(src);
    }

    /*********************************************************************
     * Paint a grid cell of the image with the halftone version with diamond
     * shape.
     *
     * @param canvas        The Canvas object of the resulting image.
     *********************************************************************/
    protected void paintGrid(Canvas canvas, float average, int startX, int startY) {
        Paint paint = new Paint();
        if (average == 0 || grid == 1 && average <= 127) {
            paint.setColor(Color.BLACK);
            canvas.drawRect(startX, startY, startX + grid, startY + grid, paint);
        }
        //If the cell is to be absolute black.
        else if (average == 255 || grid == 1 && average > 127) {
            paint.setColor(Color.WHITE);
            canvas.drawRect(startX, startY, startX + grid, startY + grid, paint);
        } else {
            //Take the percentage of the blackness of the cell and scale the
            //area of the circle, thus calculating the width of the circle.
            double percentage = 1 - (double) average / 255, diamondArea, p, q;
            float x1, x2, x3, x4, y1, y2, y3, y4;
            diamondArea = percentage * (grid * grid);
            //area of a rhomboid shape is A=p*q/2; q is longer diagonal; in this case p=0.6q, to get approximate diamond shape
            q = Math.sqrt(2 * diamondArea / 0.6);
            p = 0.6 * q;
            //Find coordinates of four points of the diamond shape.
            x1 = startX + grid / 2;
            y1 = (float) (startY + (grid - q) / 2);
            x2 = (float) (startX + p + (grid - p) / 2);
            y2 = startY + grid / 2;
            x3 = startX + grid / 2;
            y3 = (float) (startY + q + (grid - q) / 2);
            x4 = (float) (startX + (grid - p) / 2);
            y4 = startY + grid / 2;
            //Paint the background of the grid cell.
            paint.setColor(Color.WHITE);
            RectF rect = new RectF(startX, startY, startX + grid, startY + grid);
            canvas.drawRect(rect, paint);
            //paint the diamond.
            Path diamond = new Path();
            diamond.moveTo(x1, y1);
            diamond.lineTo(x2, y2);
            diamond.lineTo(x3, y3);
            diamond.lineTo(x4, y4);
            //rect = new RectF(anchorX, anchorY, (float) (anchorX+circleWidth-1), (float) (anchorY+circleWidth-1));
            paint.setColor(Color.BLACK);
            canvas.drawPath(diamond, paint);
        }
    }
}
