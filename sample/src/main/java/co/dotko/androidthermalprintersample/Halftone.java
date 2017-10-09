package co.dotko.androidthermalprintersample;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;

/*******************************************************************************
 * Abstract class that perform the halftoning on a provided bitmap image.
 * Grid size set to 10. Any child class of Halftone must implement paintGrid
 * with the appropriate shape used for halftoning.
 *
 * @author Milena Mitic
 * @since		May 2014
 * @knownBugs	none
 *
 ******************************************************************************/
public abstract class Halftone  {
    private final static int RGB_VALUE = 255;
    private final static float ANGLE_PIX_COUNT = 1024 * 768;
    private Bitmap img;
    protected int grid;
    protected int angle;

    public Halftone(Bitmap tempImg){
        img = tempImg;
        grid = 10;
        angle = 45;
    }

    public void setGrid(int gridSize)
    {
        grid = gridSize;
    }

    public void setAngle(int paraAngle)
    {
        angle = paraAngle;
    }

    /***************************************************************************************
     * Converts image into halftone version.
     *
     * @return The Bitmap image already halftoned.
     ***************************************************************************************/
    public Bitmap convert()
    {
        Bitmap buffer = null;
        if (angle > 0 && angle < 90)
        {

            if (img.getWidth() * img.getHeight() > ANGLE_PIX_COUNT)
            {
                float scale = ANGLE_PIX_COUNT / (img.getWidth() * img.getHeight());
                //scale = (float)Math.sqrt(scale);
                Matrix matrix = new Matrix();
                matrix.setScale(scale, scale);
                buffer = Bitmap.createBitmap((int)(img.getWidth() * scale), (int)(img.getHeight() * scale), img.getConfig());
                Canvas halftone = new Canvas(buffer);
                halftone.drawBitmap(img, matrix, null);
                img = buffer;
            }

            buffer = Bitmap.createBitmap(img.getWidth() * 2, img.getHeight() * 2, img.getConfig());
            Canvas halftone = new Canvas(buffer);
            halftone.rotate(angle, buffer.getWidth() / 2f, buffer.getHeight() / 2f);
            halftone.drawBitmap(img, img.getWidth() /2, img.getHeight() /2, null);
            img = buffer;
        }
        Canvas halftone = new Canvas(img);
        boolean widthExceed = false, heightExceed = false;
        int imgWidth = img.getWidth(), imgHeight = img.getHeight(), gridWidth = imgWidth / grid, gridHeight = imgHeight / grid;
        //Check for the grid size.
        if (grid > imgWidth || grid > imgHeight)
        {
            System.out.println("Grid too large...");
        }
        else
        {
            //Check for the excess rim.
            if (imgWidth % grid > 0)
            {
                widthExceed = true;
            }
            if (imgHeight % grid > 0)
            {
                heightExceed = true;
            }
            //Paint the grid.
            for (int gridX = 0; gridX < gridWidth; gridX++)
            {
                for (int gridY = 0; gridY < gridHeight; gridY++)
                {
                    int limitX = (gridX + 1) * grid, limitY = (gridY + 1) * grid;
                    paintGrid(halftone, gridX, gridY, limitX, limitY);
                }
            }
            //Paint the excess rims if applicable
            if (widthExceed)
            {
                int gridX = gridWidth;
                for (int gridY = 0; gridY < gridHeight; gridY++)
                {
                    int limitX = imgWidth, limitY = (gridY + 1) * grid;
                    paintGrid(halftone, gridX, gridY, limitX, limitY);
                }
            }
            if (heightExceed)
            {
                int gridY = gridHeight;
                for (int gridX = 0; gridX < gridWidth; gridX++)
                {
                    int limitX = (gridX + 1) * grid, limitY = imgHeight;
                    paintGrid(halftone, gridX, gridY, limitX, limitY);
                }
            }
            if (widthExceed && heightExceed)
            {
                int gridX = gridWidth, gridY = gridHeight;
                paintGrid(halftone, gridX, gridY, imgWidth, imgHeight);
            }
        }

        if (angle > 0 && angle < 90)
        {
            buffer = Bitmap.createBitmap(img.getWidth(), img.getHeight(), img.getConfig());
            halftone = new Canvas(buffer);
            halftone.rotate(-angle, buffer.getWidth() / 2f, buffer.getHeight() / 2f);
            halftone.drawBitmap(img, 0, 0, null);
            img = buffer;
            buffer = Bitmap.createBitmap(img.getWidth() / 2, img.getHeight() / 2, img.getConfig());
            int left = img.getWidth() / 2 - buffer.getWidth() / 2, top = img.getHeight() / 2 - buffer.getHeight() / 2;
            Rect src = new Rect(left, top, left + buffer.getWidth(), top + buffer.getHeight());
            Rect dest = new Rect(0, 0, buffer.getWidth(), buffer.getHeight());
            halftone = new Canvas(buffer);
            halftone.drawBitmap(img, src, dest, null);
            img = buffer;
        }
        return img;
    }

    protected void paintGrid(Canvas htgrp, int gridX, int gridY, int limitX, int limitY)
    {
        float average = getGridAverage(gridX, gridY, limitX, limitY);
        paintGrid(htgrp, average, gridX * grid, gridY * grid);
    }

    protected float getGridAverage(int gridX, int gridY, int limitX, int limitY)
    {
        float average = 0, count = 0, startX = gridX * grid, startY = gridY * grid;
        //Taking the average of the R, B and G channel of each pixel in the
        //grid cell to get the grayscale value.
        for (int x = (int) startX; x < limitX; x++)
        {
            for (int y = (int) startY; y < limitY; y++)
            {
                int p = img.getPixel(x, y);
                //average taken with luma coefficient to simulate human perception
                double currAverage = (0.114 * (double)Color.blue(p) + 0.299 * (double)Color.red(p) + 0.587 * (double) Color
                        .green(p));
                //Pre revision 1.1, the summing up is not necessary but the
                //value will be assigned back to each channel of the pixel.
                average += currAverage;
                count++;
            }
        }
        //Taking the average of grayscale values of all pixels in the cell.
        average = average / count;
        return average;
    }

    /*********************************************************************
     * Paint a grid cell of the image with the halftone version.
     * Any inheritted class will implement this method with the appropriate
     * shape used for halftoning.
     *
     * @param htgrp		The Canvas object of the resulting image.
     *********************************************************************/
    abstract protected void paintGrid(Canvas htgrp, float average, int startX, int startY);
}
