package co.dotko.androidthermalprintersample;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.KeyEvent;

import com.google.android.things.contrib.driver.button.ButtonInputDriver;
import com.google.android.things.contrib.driver.ht16k33.AlphanumericDisplay;
import com.google.android.things.contrib.driver.rainbowhat.RainbowHat;
import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;
import com.google.android.things.pio.UartDevice;

import java.io.IOException;
import java.util.List;

import co.dotko.androidthermalprinter.Command;
import co.dotko.androidthermalprinter.ThermalPrinter;

import static android.view.KeyEvent.KEYCODE_A;
import static android.view.KeyEvent.KEYCODE_B;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String UART_DEVICE_NAME = "UART6";

    private UartDevice mUart;
    private Gpio mLedRGpio, mLedGGpio;
    private ButtonInputDriver mButtonAInputDriver, mButtonBInputDriver;
    private AlphanumericDisplay mAlphanumericDisplay;
    private ThermalPrinter mPrinter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "Starting ButtonActivity");
        initUart();
        initPrinter(mUart);
        initLeds();
        initButtons();
        initAlphanumericDisplay();
        logUartDevices();
    }

    private void initPrinter(@NonNull final UartDevice uart) {
        Log.i(TAG, "Registering Thermal Printer");
        mPrinter = new ThermalPrinter(uart);
    }

    private void printTable() {
        mPrinter.print("Normal - NORMAL\n");
        mPrinter.setInverse(true);
        mPrinter.print("+ inversed INVERSED\n");
        mPrinter.setUpsideDown(true);
        mPrinter.print("+ upside DOWN\n");
        mPrinter.setBold(true);
        mPrinter.print("+ bold BOLD\n");
        mPrinter.setDoubleHeight(true);
        mPrinter.print("+ double Height\n");
        mPrinter.setDoubleWidth(true);
        mPrinter.print("+ double Width\n");
        mPrinter.sendRaw(Command.setUnderline(1));
        mPrinter.print("* Underlined ONE\n");
        mPrinter.sendRaw(Command.setUnderline(2));
        mPrinter.print("* Underlined TWO\n");
        mPrinter.sendRaw(Command.setUnderline(0));
        mPrinter.print("* Underlined ZERO\n");
        mPrinter.setAlternativeFont(true);
        mPrinter.print("+ alternative FONT\n");
        mPrinter.print("Normal - NORMAL\n");
        mPrinter.setInverse(false);
        mPrinter.print("- inversed INVERSED\n");
        mPrinter.setUpsideDown(false);
        mPrinter.print("- upside DOWN\n");
        mPrinter.setBold(false);
        mPrinter.print("- bold BOLD\n");
        mPrinter.setDoubleHeight(false);
        mPrinter.print("- double Height\n");
        mPrinter.setDoubleWidth(false);
        mPrinter.print("- double Width\n");
        mPrinter.sendRaw(Command.setUnderline(1));
        mPrinter.print("* Underlined ONE\n");
        mPrinter.sendRaw(Command.setUnderline(2));
        mPrinter.print("* Underlined TWO\n");
        mPrinter.setAlternativeFont(false);
        mPrinter.print("- alternative FONT\n");
    }

    private void initUart() {
        Log.i(TAG, "Registering UART device");
        try {
            PeripheralManagerService managerService = new PeripheralManagerService();
            mUart = managerService.openUartDevice(UART_DEVICE_NAME);
            mUart.setBaudrate(9600);
            mUart.setDataSize(8);
            mUart.setParity(UartDevice.PARITY_NONE);
            mUart.setStopBits(1);
        } catch (IOException e) {
            Log.w(TAG, String.format("Unable to access UART device: %s", UART_DEVICE_NAME), e);
        }
    }

    private void initAlphanumericDisplay() {
        try {
            mAlphanumericDisplay = RainbowHat.openDisplay();
            mAlphanumericDisplay.setEnabled(true);
            mAlphanumericDisplay.clear();
        } catch (IOException e) {
            Log.e(TAG, "Error configuring Alphanumeric Display", e);
        }
    }

    private void initButtons() {
        Log.i(TAG, "Registering button driver");
        try {
            mButtonAInputDriver = RainbowHat.createButtonAInputDriver(KeyEvent.KEYCODE_A);
            mButtonAInputDriver.register();
            mButtonBInputDriver = RainbowHat.createButtonBInputDriver(KEYCODE_B);
            mButtonBInputDriver.register();
        } catch (IOException e) {
            Log.e(TAG, "Error registering buttons", e);
        }
    }

    private void initLeds() {
        Log.i(TAG, "Configuring LED pins");
        try {
            mLedRGpio = RainbowHat.openLedRed();
            mLedRGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            mLedGGpio = RainbowHat.openLedGreen();
            mLedGGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
        } catch (IOException e) {
            Log.e(TAG, "Error configuring LEDs", e);
        }
    }

    private void logUartDevices() {
        PeripheralManagerService manager = new PeripheralManagerService();
        List<String> deviceList = manager.getUartDeviceList();
        if (deviceList.isEmpty()) {
            Log.i(TAG, "No UART port available on this device.");
        } else {
            Log.i(TAG, "List of available devices: " + deviceList);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        displayText("");
        switch (keyCode) {
            case KEYCODE_A:
                displayText("LedA");
                return setLedValue(mLedRGpio, true);
            case KEYCODE_B:
                displayText("LedB");
                printTable();
                return setLedValue(mLedGGpio, true);
            default:
                return false;
        }
    }

    private void displayText(final String text) {
        try {
            mAlphanumericDisplay.display(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KEYCODE_A:
                return setLedValue(mLedRGpio, false);
            case KEYCODE_B:
                return setLedValue(mLedGGpio, false);
            default:
                return false;
        }
    }

    private boolean setLedValue(final Gpio led, final boolean setOn) {
        try {
            led.setValue(setOn);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Error setting led");
            return false;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mButtonAInputDriver != null) {
            mButtonAInputDriver.unregister();
            try {
                mButtonAInputDriver.close();
            } catch (IOException e) {
                Log.e(TAG, "Error closing Button driver", e);
            } finally {
                mButtonAInputDriver = null;
            }
        }

        if (mLedRGpio != null) {
            try {
                mLedRGpio.close();
            } catch (IOException e) {
                Log.e(TAG, "Error closing Red LED GPIO", e);
            } finally {
                mLedRGpio = null;
            }
        }

        if (mLedGGpio != null) {
            try {
                mLedGGpio.close();
            } catch (IOException e) {
                Log.e(TAG, "Error closing Green LED GPIO", e);
            } finally {
                mLedGGpio = null;
            }
        }

        if (mUart != null) {
            try {
                mUart.close();
            } catch (IOException e) {
                Log.e(TAG, "Error closing UART device", e);
            } finally {
                mUart = null;
            }
        }
    }
}
