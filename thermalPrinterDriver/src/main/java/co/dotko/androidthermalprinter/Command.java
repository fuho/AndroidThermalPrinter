package co.dotko.androidthermalprinter;

import static co.dotko.androidthermalprinter.Constant.CONTROL_DC4;
import static co.dotko.androidthermalprinter.Constant.CONTROL_ESC;
import static co.dotko.androidthermalprinter.Constant.SPACE;

/**
 * Created by fuho on 9/30/17.
 */

public class Command {

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

}
