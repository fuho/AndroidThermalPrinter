/*
 * Copyright 2017 The Android Things Samples Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package co.dotko.androidthermalprintersample;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.Image;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import junit.framework.Assert;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class ImagePreprocessor {
    private static final int IMAGE_SIZE = 384;
    private static final boolean SAVE_PREVIEW_BITMAP = true;

    private Bitmap mRgbFrameBitmap,mCroppedBitmap,mHalftoneBitmap;

    public ImagePreprocessor() {
        mCroppedBitmap = Bitmap.createBitmap(IMAGE_SIZE, IMAGE_SIZE, Config.ARGB_8888);
        mHalftoneBitmap = Bitmap.createBitmap(IMAGE_SIZE, IMAGE_SIZE, Config.ARGB_8888);
        mRgbFrameBitmap = Bitmap.createBitmap(
                CameraHandler.IMAGE_WIDTH,
                CameraHandler.IMAGE_HEIGHT,
                Config.ARGB_8888
        );
    }

    public static void saveBitmap(final Bitmap bitmap) {
        final File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "tensorflow_preview.png");
        Log.d("ImageHelper", String.format("Saving %dx%d bitmap to %s.",
                bitmap.getWidth(), bitmap.getHeight(), file.getAbsolutePath()
        ));

        if (file.exists()) {
            file.delete();
        }
        try (FileOutputStream fs = new FileOutputStream(file);
             BufferedOutputStream out = new BufferedOutputStream(fs)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 99, out);
        } catch (final Exception e) {
            Log.w("ImageHelper", "Could not save image for debugging. " + e.getMessage());
        }
    }

    public static void cropAndRescaleBitmap(
            final Bitmap src,
            final Bitmap dst
    ) {
        Assert.assertEquals(dst.getWidth(), dst.getHeight());
        final float minDim = Math.min(src.getWidth(), src.getHeight());

        final Matrix matrix = new Matrix();

        // We only want the center square out of the original rectangle.
        final float translateX = -Math.max(0, (src.getWidth() - minDim) / 2);
        final float translateY = -Math.max(0, (src.getHeight() - minDim) / 2);
        matrix.preTranslate(translateX, translateY);

        final float scaleFactor = dst.getHeight() / minDim;
        matrix.postScale(scaleFactor, scaleFactor);
        final Canvas canvas = new Canvas(dst);
        canvas.drawBitmap(src, matrix, null);
    }

    public static Bitmap convertToMonochrome(final Bitmap src) {
        final HalftoneDiamond halftone = new HalftoneDiamond(src);
        halftone.setGrid(3);
        halftone.setAngle(75);
        return halftone.convert();
    }

    public Bitmap preprocessImage(final Image image) {
        if (image == null) {
            return null;
        }

        Assert.assertEquals("Invalid size width", mRgbFrameBitmap.getWidth(), image.getWidth());
        Assert.assertEquals("Invalid size height", mRgbFrameBitmap.getHeight(), image.getHeight());

        if (mCroppedBitmap != null && mRgbFrameBitmap != null) {
            ByteBuffer bb = image.getPlanes()[0].getBuffer();
            mRgbFrameBitmap = BitmapFactory.decodeStream(new ByteBufferBackedInputStream(bb));
            cropAndRescaleBitmap(mRgbFrameBitmap, mCroppedBitmap);
            mHalftoneBitmap = convertToMonochrome(mCroppedBitmap);
        }

        image.close();

        // For debugging
        if (SAVE_PREVIEW_BITMAP) {
            saveBitmap(mHalftoneBitmap);
        }
        return mHalftoneBitmap;
    }

    private static class ByteBufferBackedInputStream extends InputStream {

        ByteBuffer buf;

        public ByteBufferBackedInputStream(ByteBuffer buf) {
            this.buf = buf;
        }

        public int read() throws IOException {
            if (!buf.hasRemaining()) {
                return -1;
            }
            return buf.get() & 0xFF;
        }

        public int read(@NonNull byte[] bytes, int off, int len)
                throws IOException {
            if (!buf.hasRemaining()) {
                return -1;
            }

            len = Math.min(len, buf.remaining());
            buf.get(bytes, off, len);
            return len;
        }
    }
}
