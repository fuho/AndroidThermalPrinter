package co.dotko.androidthermalprinter;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.things.pio.UartDevice;

import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static co.dotko.androidthermalprinter.Constant.MASK_BOLD;
import static co.dotko.androidthermalprinter.Constant.MASK_DOUBLE_HEIGHT;
import static co.dotko.androidthermalprinter.Constant.MASK_DOUBLE_WIDTH;
import static co.dotko.androidthermalprinter.Constant.MASK_FONT_B;
import static co.dotko.androidthermalprinter.Constant.MASK_INVERSE;
import static co.dotko.androidthermalprinter.Constant.MASK_UPSIDE_DOWN;

/**
 * Created by fuho on 9/30/17.
 */

public class ThermalPrinter implements Closeable {
    private static final String TAG = ThermalPrinter.class.getSimpleName();
    private UartDevice mUart;

    private byte mPrintMode;
    private boolean mIsFontB;
    private boolean mIsInverse;
    private boolean mIsUpsideDown;
    private boolean mIsBold;
    private boolean mIsDoubleHeight;
    private boolean mIsDoubleWidth;
    private boolean mIsUnderline;

    public ThermalPrinter(final UartDevice uart) {
        mUart = uart;
        sendRaw(Command.initializePrinter());
    }

    public static String byteArrayToString(byte[] bytes, final String format) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte eByte : bytes)
            sb.append(String.format(format, eByte));
        return sb.toString();
    }

    @Override
    public void close() throws IOException {
        if (mUart != null) {
            mUart.close();
            mUart = null;
        }
    }

    public void print(final String text) {
        sendRaw(text.getBytes());
    }

    public void sendRaw(final byte oneByte) {
        final byte[] oneByteArray = new byte[1];
        oneByteArray[0] = oneByte;
        sendRaw(oneByteArray);
    }

    public void sendRaw(final byte[] bytes) {
        try {
            logBytes(bytes);
            mUart.write(bytes, bytes.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void logBytes(final byte[] bytes) {
        StringBuilder msg = new StringBuilder();
        msg.append("HEX:").append(byteArrayToString(bytes, " %02x"));
        msg.append("\nDEC:").append(byteArrayToString(bytes, " %02d"));
        msg.append("\nTXT:").append(new String(bytes, StandardCharsets.UTF_8));
        Log.d(TAG, msg.toString());
    }

    public void init() {
        sendRaw(Command.initializePrinter());
    }

    public void adjustPrintMode(final boolean isOn, final byte printMode) {
        if (isOn) {
            setPrintMode(printMode);
        } else {
            unsetPrintMode(printMode);
        }
    }

    public void setPrintMode(final byte printMode) {
        mPrintMode |= printMode;
        sendRaw(Command.setPrintMode(printMode));
    }

    public void unsetPrintMode(final byte printMode) {
        mPrintMode &= ~printMode;
        sendRaw(Command.setPrintMode(printMode));
    }

    public void setInverse(final boolean setInverse) {
        mIsInverse = setInverse;
        adjustPrintMode(setInverse, MASK_INVERSE);
    }

    public void setUpsideDown(final boolean setUpsideDown) {
        mIsUpsideDown = setUpsideDown;
        adjustPrintMode(setUpsideDown, MASK_UPSIDE_DOWN);
    }

    public void setBold(final boolean setBold) {
        mIsBold = setBold;
        adjustPrintMode(setBold, MASK_BOLD);
    }

    public void setDoubleHeight(final boolean setDoubleHeight) {
        mIsDoubleHeight = setDoubleHeight;
        adjustPrintMode(setDoubleHeight, MASK_DOUBLE_HEIGHT);
    }


    public void setDoubleWidth(final boolean setDoubleWidth) {
        mIsDoubleWidth = setDoubleWidth;
        adjustPrintMode(setDoubleWidth, MASK_DOUBLE_WIDTH);
    }


    public void setAlternativeFont(final boolean setFontB) {
        mIsFontB = setFontB;
        adjustPrintMode(setFontB, MASK_FONT_B);
    }

    public void printImage(final Bitmap bitmap) {
        final int mode = 33;
        final int width = 384;
        new AsyncTask<Void,Void,Void>(){

            @Override
            protected Void doInBackground(final Void... voids) {
                byte[] bitmapData;
                byte[] command;
                for (int row = 0; row < 383 - 24; row += 24) {
                    bitmapData = bitmapToByteArray(mode, bitmap, row);
                    command = Command.selectBitImage(mode, width % 256, width / 256, bitmapData);
                    sendRaw(command);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }
        }.execute();
    }

    private static byte[] bitmapToByteArray(final int mode, final Bitmap bitmap, final int row) {
        final byte[] bytes = new byte[384 * 3];
        final int[] pixels = new int[24];
        final int width = bitmap.getWidth();
        for (int x = 0; x < width; x++) {
            bitmap.getPixels(pixels, 0, 1, x, row, 1, 24);
            final byte[] columnBytes = pixelColumnToByte(pixels);
            bytes[x * 3] = columnBytes[0];
            bytes[x * 3 + 1] = columnBytes[1];
            bytes[x * 3 + 2] = columnBytes[2];
        }
        return bytes;
    }

    private static byte[] pixelColumnToByte(final int[] pixels) {
        long bitmap = 0;
        for (int i = 0; i < pixels.length; i++) {
            bitmap <<= 1;
            bitmap |= ~pixels[i] & 0x1;
        }
        return new byte[]{(byte) (bitmap >> 16), (byte) (bitmap >> 8), (byte) bitmap,};
    }
}
