package za.co.infinityrewards.plugins.datecsprinter;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.getcapacitor.JSObject;
import com.getcapacitor.PermissionState;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;
import com.getcapacitor.annotation.PermissionCallback;

@CapacitorPlugin(name = "DatecsPrinter", permissions = {
        @Permission(strings = {
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
        }, alias = "bluetooth")
})
public class DatecsPrinterPlugin extends Plugin {
    private DatecsSDKWrapper printer;

    @Override
    public void load() {
        try {
            printer = new DatecsSDKWrapper(getActivity());
            Log.d("DatecsPrinterPlugin", "DatecsSDKWrapper initialized");
        } catch (Exception e) {
            Log.e("DatecsPrinterPlugin", "Failed to initialize DatecsSDKWrapper", e);
        }
    }

    @PluginMethod
    public void listBluetoothDevices(PluginCall call) {
        if (!hasPermission("bluetooth")) {
            Log.d("DatecsPrinterPlugin", "Requesting bluetooth permission");
            requestPermissionForAlias("bluetooth", call, "listBluetoothDevicesCallback");
            return;
        }
        if (printer == null) {
            Log.e("DatecsPrinterPlugin", "Printer is not initialized");
            call.reject("Printer is not initialized");
            return;
        }
        printer.getBluetoothPairedDevices(call);
    }

    @PermissionCallback
    private void listBluetoothDevicesCallback(PluginCall call) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(getContext(),
                    Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                call.reject("BLUETOOTH_SCAN permission not granted");
                return;
            }
        } else {
            if (ContextCompat.checkSelfPermission(getContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                call.reject("ACCESS_FINE_LOCATION permission not granted");
                return;
            }
        }

        if (printer == null) {
            Log.e("DatecsPrinterPlugin", "Printer is not initialized");
            call.reject("Printer is not initialized");
            return;
        }

        printer.getBluetoothPairedDevices(call);
    }

    @PluginMethod
    public void connect(PluginCall call) {
        if (!hasPermission("bluetooth")) {
            Log.d("DatecsPrinterPlugin", "Requesting bluetooth permission");
            requestPermissionForAlias("bluetooth", call, "connectCallback");
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(getContext(),
                    Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(getContext(),
                            Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                Log.e("DatecsPrinterPlugin", "Missing Bluetooth permissions");
                call.reject("Missing Bluetooth permissions");
                return;
            }
        }
        if (printer == null) {
            Log.e("DatecsPrinterPlugin", "Printer is not initialized");
            call.reject("Printer is not initialized");
            return;
        }
        String address = call.getString("address");
        if (address != null) {
            printer.setAddress(address);
            printer.connect(call);
        } else {
            Log.e("DatecsPrinterPlugin", "Address is required");
            call.reject("Address is required");
        }
    }

    @PermissionCallback
    private void connectCallback(PluginCall call) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(getContext(),
                    Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                call.reject("BLUETOOTH_CONNECT permission not granted");
                return;
            }
        }

        if (printer == null) {
            Log.e("DatecsPrinterPlugin", "Printer is not initialized");
            call.reject("Printer is not initialized");
            return;
        }

        String address = call.getString("address");
        if (address != null) {
            printer.setAddress(address);
            printer.connect(call);
        } else {
            call.reject("Address is required");
        }
    }

    @PluginMethod
    public void feedPaper(PluginCall call) {
        if (printer == null) {
            Log.e("DatecsPrinterPlugin", "Printer is not initialized");
            call.reject("Printer is not initialized");
            return;
        }
        int lines = call.getInt("lines", 0);
        printer.feedPaper(lines, call);
    }

    @PluginMethod
    public void printText(PluginCall call) {
        if (printer == null) {
            Log.e("DatecsPrinterPlugin", "Printer is not initialized");
            call.reject("Printer is not initialized");
            return;
        }
        String text = call.getString("text");
        String charset = call.getString("charset", "UTF-8"); // "windows-1251"

        // Log the text and charset for debugging
        // Log.d("DatecsPrinterPlugin", "Text: " + text);
        // Log.d("DatecsPrinterPlugin", "Charset: " + charset);

        if (text != null) {
            printer.printTaggedText(text, charset, call);
        } else {
            Log.e("DatecsPrinterPlugin", "Text is required");
            call.reject("Text is required");
        }
    }

    @PluginMethod
    public void writeHex(PluginCall call) {
        if (printer == null) {
            Log.e("DatecsPrinterPlugin", "Printer is not initialized");
            call.reject("Printer is not initialized");
            return;
        }
        String hex = call.getString("hex");
        if (hex != null) {
            printer.writeHex(hex, call);
        } else {
            Log.e("DatecsPrinterPlugin", "Hex data is required");
            call.reject("Hex data is required");
        }
    }

    @PluginMethod
    public void write(PluginCall call) {
        if (printer == null) {
            Log.e("DatecsPrinterPlugin", "Printer is not initialized");
            call.reject("Printer is not initialized");
            return;
        }
        String data = call.getString("data");
        if (data != null) {
            printer.write(data.getBytes(), call);
        } else {
            Log.e("DatecsPrinterPlugin", "Data is required");
            call.reject("Data is required");
        }
    }

    @PluginMethod
    public void getStatus(PluginCall call) {
        if (printer == null) {
            Log.e("DatecsPrinterPlugin", "Printer is not initialized");
            call.reject("Printer is not initialized");
            return;
        }
        printer.getStatus(call);
    }

    @PluginMethod
    public void getTemperature(PluginCall call) {
        if (printer == null) {
            Log.e("DatecsPrinterPlugin", "Printer is not initialized");
            call.reject("Printer is not initialized");
            return;
        }
        printer.getTemperature(call);
    }

    @PluginMethod
    public void setBarcode(PluginCall call) {
        if (printer == null) {
            Log.e("DatecsPrinterPlugin", "Printer is not initialized");
            call.reject("Printer is not initialized");
            return;
        }
        int align = call.getInt("align", 0);
        boolean small = call.getBoolean("small", false);
        int scale = call.getInt("scale", 0);
        int hri = call.getInt("hri", 0);
        int height = call.getInt("height", 0);
        printer.setBarcode(align, small, scale, hri, height, call);
    }

    @PluginMethod
    public void printBarcode(PluginCall call) {
        if (printer == null) {
            Log.e("DatecsPrinterPlugin", "Printer is not initialized");
            call.reject("Printer is not initialized");
            return;
        }
        int type = call.getInt("type", 0);
        String data = call.getString("data");
        if (data != null) {
            printer.printBarcode(type, data, call);
        } else {
            Log.e("DatecsPrinterPlugin", "Data is required");
            call.reject("Data is required");
        }
    }

    @PluginMethod
    public void printQRCode(PluginCall call) {
        if (printer == null) {
            Log.e("DatecsPrinterPlugin", "Printer is not initialized");
            call.reject("Printer is not initialized");
            return;
        }
        int size = call.getInt("size", 0);
        int eccLv = call.getInt("eccLv", 0);
        String data = call.getString("data");
        if (data != null) {
            printer.printQRCode(size, eccLv, data, call);
        } else {
            Log.e("DatecsPrinterPlugin", "Data is required");
            call.reject("Data is required");
        }
    }

    @PluginMethod
    public void printSelfTest(PluginCall call) {
        if (printer == null) {
            Log.e("DatecsPrinterPlugin", "Printer is not initialized");
            call.reject("Printer is not initialized");
            return;
        }
        printer.printSelfTest(call);
    }

    @PluginMethod
    public void drawPageRectangle(PluginCall call) {
        if (printer == null) {
            Log.e("DatecsPrinterPlugin", "Printer is not initialized");
            call.reject("Printer is not initialized");
            return;
        }
        int x = call.getInt("x", 0);
        int y = call.getInt("y", 0);
        int width = call.getInt("width", 0);
        int height = call.getInt("height", 0);
        int fillMode = call.getInt("fillMode", 0);
        printer.drawPageRectangle(x, y, width, height, fillMode, call);
    }

    @PluginMethod
    public void drawPageFrame(PluginCall call) {
        if (printer == null) {
            Log.e("DatecsPrinterPlugin", "Printer is not initialized");
            call.reject("Printer is not initialized");
            return;
        }
        int x = call.getInt("x", 0);
        int y = call.getInt("y", 0);
        int width = call.getInt("width", 0);
        int height = call.getInt("height", 0);
        int fillMode = call.getInt("fillMode", 0);
        int thickness = call.getInt("thickness", 0);
        printer.drawPageFrame(x, y, width, height, fillMode, thickness, call);
    }

    @PluginMethod
    public void selectStandardMode(PluginCall call) {
        if (printer == null) {
            Log.e("DatecsPrinterPlugin", "Printer is not initialized");
            call.reject("Printer is not initialized");
            return;
        }
        printer.selectStandardMode(call);
    }

    @PluginMethod
    public void selectPageMode(PluginCall call) {
        if (printer == null) {
            Log.e("DatecsPrinterPlugin", "Printer is not initialized");
            call.reject("Printer is not initialized");
            return;
        }
        printer.selectPageMode(call);
    }

    @PluginMethod
    public void printPage(PluginCall call) {
        if (printer == null) {
            Log.e("DatecsPrinterPlugin", "Printer is not initialized");
            call.reject("Printer is not initialized");
            return;
        }
        printer.printPage(call);
    }

    @PluginMethod
    public void setPageRegion(PluginCall call) {
        if (printer == null) {
            Log.e("DatecsPrinterPlugin", "Printer is not initialized");
            call.reject("Printer is not initialized");
            return;
        }
        int x = call.getInt("x", 0);
        int y = call.getInt("y", 0);
        int width = call.getInt("width", 0);
        int height = call.getInt("height", 0);
        int direction = call.getInt("direction", 0);
        printer.setPageRegion(x, y, width, height, direction, call);
    }

    @PluginMethod
    public void printImage(PluginCall call) {
        if (printer == null) {
            Log.e("DatecsPrinterPlugin", "Printer is not initialized");
            call.reject("Printer is not initialized");
            return;
        }
        String image = call.getString("image");
        int width = call.getInt("width", 0);
        int height = call.getInt("height", 0);
        int align = call.getInt("align", 0);
        if (image != null) {
            printer.printImage(image, width, height, align, call);
        } else {
            Log.e("DatecsPrinterPlugin", "Image data is required");
            call.reject("Image data is required");
        }
    }

    @PluginMethod
    public void disconnect(PluginCall call) {
        if (printer == null) {
            Log.e("DatecsPrinterPlugin", "Printer is not initialized");
            call.reject("Printer is not initialized");
            return;
        }
        printer.disconnect(call);
    }
}