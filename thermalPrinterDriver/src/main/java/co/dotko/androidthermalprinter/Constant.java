package co.dotko.androidthermalprinter;

/**
 * Created by fuho on 9/30/17.
 */

public class Constant {
    public static final byte CONTROL_NUL = 0x00; // null    dec:0 bin: 00000000
    public static final byte CONTROL_SOH = 0x01; // start of header dec:1 bin: 00000001
    public static final byte CONTROL_STX = 0x02; // start of text   dec:2 bin: 00000010
    public static final byte CONTROL_ETX = 0x03; // end of text dec:3 bin: 00000011
    public static final byte CONTROL_EOT = 0x04; // end of transmission dec:4 bin: 00000100
    public static final byte CONTROL_ENQ = 0x05; // enquiry dec:5 bin: 00000101
    public static final byte CONTROL_ACK = 0x06; // acknowledge dec:6 bin: 00000110
    public static final byte CONTROL_BEL = 0x07; // bell    dec:7 bin: 00000111
    public static final byte CONTROL_BS = 0x08; // backspace   dec:8 bin: 00001000
    public static final byte CONTROL_HT = 0x09; // horizontal tab  dec:9 bin: 00001001
    public static final byte CONTROL_LF = 0x0A; // line feed   dec:10 bin: 00001010
    public static final byte CONTROL_VT = 0x0B; // vertical tab    dec:11 bin: 00001011
    public static final byte CONTROL_FF = 0x0C; // form feed   dec:12 bin: 00001100
    public static final byte CONTROL_CR = 0x0D; // enter / carriage return dec:13 bin: 00001101
    public static final byte CONTROL_SO = 0x0E; // shift out   dec:14 bin: 00001110
    public static final byte CONTROL_SI = 0x0F; // shift in    dec:15 bin: 00001111
    public static final byte CONTROL_DLE = 0x10; // data link escape    dec:16 bin: 00010000
    public static final byte CONTROL_DC1 = 0x11; // device control 1    dec:17 bin: 00010001
    public static final byte CONTROL_DC2 = 0x12; // device control 2    dec:18 bin: 00010010
    public static final byte CONTROL_DC3 = 0x13; // device control 3    dec:19 bin: 00010011
    public static final byte CONTROL_DC4 = 0x14; // device control 4    dec:20 bin: 00010100
    public static final byte CONTROL_NAK = 0x15; // negative acknowledge    dec:21 bin: 00010101
    public static final byte CONTROL_SYN = 0x16; // synchronize dec:22 bin: 00010110
    public static final byte CONTROL_ETB = 0x17; // end of trans. block dec:23 bin: 00010111
    public static final byte CONTROL_CAN = 0x18; // cancel  dec:24 bin: 00011000
    public static final byte CONTROL_EM = 0x19; // end of medium   dec:25 bin: 00011001
    public static final byte CONTROL_SUB = 0x1A; // substitute  dec:26 bin: 00011010
    public static final byte CONTROL_ESC = 0x1B; // escape  dec:27 bin: 00011011
    public static final byte CONTROL_FS = 0x1C; // file separator  dec:28 bin: 00011100
    public static final byte CONTROL_GS = 0x1D; // group separator dec:29 bin: 00011101
    public static final byte CONTROL_RS = 0x1E; // record separator    dec:30 bin: 00011110
    public static final byte CONTROL_US = 0x1F; // unit separator  dec:31 bin: 00011111
    public static final byte CONTROL_DEL = 0x7F; // delete  dec:127 bin: 01111111
    public static final byte SPACE = 0x20; // space  dec:32 bin: 00100000

    // Masks
    public static final byte MASK_FONT_B = 1;
    public static final byte MASK_INVERSE = (1 << 1);
    public static final byte MASK_UPSIDE_DOWN = (1 << 2);
    public static final byte MASK_BOLD = (1 << 3);
    public static final byte MASK_DOUBLE_HEIGHT = (1 << 4);
    public static final byte MASK_DOUBLE_WIDTH = (1 << 5);
    //public static final byte MASK_UNDERLINE = (1 << 6);

    private Constant() {
    }
}
