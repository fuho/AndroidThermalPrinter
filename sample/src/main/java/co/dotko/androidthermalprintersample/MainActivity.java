package co.dotko.androidthermalprintersample;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Handler;
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
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import co.dotko.androidthermalprinter.Command;
import co.dotko.androidthermalprinter.ThermalPrinter;

import static android.view.KeyEvent.KEYCODE_A;
import static android.view.KeyEvent.KEYCODE_B;
import static android.view.KeyEvent.KEYCODE_C;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String UART_DEVICE_NAME = "UART6";

    private UartDevice mUart;
    private Gpio mLedRGpio, mLedGGpio, mLedBGpio;
    private ButtonInputDriver mButtonAInputDriver, mButtonBInputDriver, mButtonCInputDriver;
    private AlphanumericDisplay mAlphanumericDisplay;
    private ThermalPrinter mPrinter;
    private CameraHandler mCameraHandler;
    private ImagePreprocessor mImagePreprocessor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "Starting ButtonActivity");
        initUart();
        initPrinter(mUart);
        initLeds();
        initButtons();
        initAlphanumericDisplay();
        initCamera();
        logUartDevices();
    }

    private void initCamera() {
        mImagePreprocessor = new ImagePreprocessor();
        mCameraHandler = CameraHandler.getInstance();
        Handler threadLooper = new Handler(getMainLooper());

        mCameraHandler.initializeCamera(
                this,
                threadLooper,
                new ImageReader.OnImageAvailableListener() {
                    @Override
                    public void onImageAvailable(ImageReader imageReader) {
                        Bitmap bitmap = mImagePreprocessor.preprocessImage(imageReader.acquireNextImage());
                        printBitmap(bitmap);
                    }
                }
        );
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

    private void printSampleImage() {
        final Bitmap bitmap = BitmapFactory.decodeResource(
                getResources(),
                R.drawable.sample_dithered_384x384
        );
        mPrinter.init();
        printBitmap(bitmap);
    }

    private void printBitmap(final Bitmap bitmap) {
        mPrinter.printImage(bitmap);
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
            mButtonCInputDriver = RainbowHat.createButtonCInputDriver(KEYCODE_C);
            mButtonCInputDriver.register();
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
            mLedBGpio = RainbowHat.openLedBlue();
            mLedBGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
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
                displayText("RED");
                return setLedValue(mLedRGpio, true);
            case KEYCODE_B:
                displayText("GRN");
                //printTable();
                //printSampleImage();
                return setLedValue(mLedGGpio, true);
            case KEYCODE_C:
                displayText("SNAP");
                loadPhoto();
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KEYCODE_A:
                displayText();
                return setLedValue(mLedRGpio, false);
            case KEYCODE_B:
                displayText();
                return setLedValue(mLedGGpio, false);
            case KEYCODE_C:
                displayText();
                return setLedValue(mLedGGpio, false);
            default:
                return false;
        }
    }

    private void displayText() {
        displayText(null);
    }

    private void displayText(final String text) {
        try {
            if (text == null) {
                mAlphanumericDisplay.clear();
            } else {
                mAlphanumericDisplay.display(text);
            }
        } catch (IOException e) {
            e.printStackTrace();
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

    private void closeCamera() {
        mCameraHandler.shutDown();
    }

    private void loadPhoto() {
        mCameraHandler.takePicture();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        destroyButtonDriver(mButtonAInputDriver);
        mButtonAInputDriver = null;
        destroyButtonDriver(mButtonBInputDriver);
        mButtonBInputDriver = null;
        destroyButtonDriver(mButtonCInputDriver);
        mButtonCInputDriver = null;

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
        if (mCameraHandler != null) {
            try {
                closeCamera();
            } catch (Throwable t) {
                Log.e(TAG, "Error closing camera", t);
            }
        }
    }

    private void destroyButtonDriver(ButtonInputDriver buttonDriver) {
        if (buttonDriver != null) {
            buttonDriver.unregister();
            try {
                buttonDriver.close();
            } catch (IOException e) {
                Log.e(TAG, "Error closing Button driver", e);
            }
        }
    }
}
