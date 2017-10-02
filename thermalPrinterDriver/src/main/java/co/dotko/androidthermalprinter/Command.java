package co.dotko.androidthermalprinter;

import java.util.ArrayList;

import static co.dotko.androidthermalprinter.Constant.CONTROL_ESC;
import static co.dotko.androidthermalprinter.Constant.SPACE;

/**
 * Created by fuho on 9/30/17.
 */

public class Command {

    public static byte[] initializePrinter() {
        return new byte[]{CONTROL_ESC, '@'};
    }

    public static byte[] setPrintMode(final byte printMode) {
        return new byte[]{CONTROL_ESC, '!', printMode};
    }

    public static byte[] setDefaultLineSpacing() {
        return new byte[]{CONTROL_ESC, 0x02};
    }

    public static byte[] setLineSpacingInDots(byte dots) {
        return new byte[]{CONTROL_ESC, 0x03, dots};
    }

    public static byte[] setRightSideCharSpacingInDots(byte numDots) {
        return new byte[]{CONTROL_ESC, SPACE, numDots};
    }

    public static byte[] setUnderline(int numDotsHeight) {
        return new byte[]{CONTROL_ESC, '-', (byte) numDotsHeight};
    }

    /**
     * @param mode=0,1,2,3,4,6,32,33,38,39,40,71,72,73
     * @param bitmapData                               - Graphical data
     */
    public static byte[] selectBitImage(
            final int mode,
            final int nL,
            final int nH,
            final byte[] bitmapData
    ) {
        final byte[] commandPrefix = {CONTROL_ESC, '*', (byte) mode, (byte) nL, (byte) nH};
        final byte[] commandFull = new byte[commandPrefix.length + bitmapData.length];
        System.arraycopy(commandPrefix, 0, commandFull, 0, commandPrefix.length);
        System.arraycopy(bitmapData, 0, commandFull, commandPrefix.length, bitmapData.length);
        return commandFull;
    }
}
