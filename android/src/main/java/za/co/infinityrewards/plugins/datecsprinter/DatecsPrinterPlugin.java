package za.co.infinityrewards.plugins.datecsprinter;

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
//                android.Manifest.permission.BLUETOOTH,
//                android.Manifest.permission.BLUETOOTH_ADMIN,
                android.Manifest.permission.BLUETOOTH_SCAN,
                android.Manifest.permission.BLUETOOTH_CONNECT,
        }, alias = "bluetooth")
})
public class DatecsPrinterPlugin extends Plugin {

    private DatecsSDKWrapper printer;

    @Override
    public void load() {
        printer = new DatecsSDKWrapper(getActivity());
    }

    @PluginMethod
    public void listBluetoothDevices(PluginCall call) {
        if (!hasPermission("bluetooth")) {
            requestPermissionForAlias("bluetooth", call, "listBluetoothDevicesCallback");
            return;
        }

        printer.getBluetoothPairedDevices(call);
    }

    @PermissionCallback
    private void listBluetoothDevicesCallback(PluginCall call) {
        if (getPermissionState("bluetooth") == PermissionState.GRANTED) {
            printer.getBluetoothPairedDevices(call);
        } else {
            call.reject("Bluetooth permission not granted");
        }
    }

    @PluginMethod
    public void connect(PluginCall call) {

        if (!hasPermission("bluetooth")) {
            requestPermissionForAlias("bluetooth", call, "connectCallback");
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

    @PermissionCallback
    private void connectCallback(PluginCall call) {
        if (getPermissionState("bluetooth") == PermissionState.GRANTED) {
            String address = call.getString("address");
            if (address != null) {
                printer.setAddress(address);
                printer.connect(call);
            } else {
                call.reject("Address is required");
            }
        } else {
            call.reject("Bluetooth permission not granted");
        }
    }

    @PluginMethod
    public void feedPaper(PluginCall call) {
        int lines = call.getInt("lines", 0);
        printer.setCallbackContext(call);
        printer.feedPaper(lines);
    }

    @PluginMethod
    public void printText(PluginCall call) {
        String text = call.getString("text");
        String charset = call.getString("charset", "windows-1251");
        printer.setCallbackContext(call);
        printer.printTaggedText(text, charset);
    }

    @PluginMethod
    public void writeHex(PluginCall call) {
        String hex = call.getString("hex");
        printer.setCallbackContext(call);
        printer.writeHex(hex);
    }

    @PluginMethod
    public void write(PluginCall call) {
        String data = call.getString("data");
        if (data != null) {
            printer.setCallbackContext(call);
            printer.write(data.getBytes());
        } else {
            call.reject("Data is required");
        }
    }

    @PluginMethod
    public void getStatus(PluginCall call) {
        printer.setCallbackContext(call);
        printer.getStatus();
    }

    @PluginMethod
    public void getTemperature(PluginCall call) {
        printer.setCallbackContext(call);
        printer.getTemperature();
    }

    @PluginMethod
    public void setBarcode(PluginCall call) {
        int align = call.getInt("align", 0);
        boolean small = call.getBoolean("small", false);
        int scale = call.getInt("scale", 0);
        int hri = call.getInt("hri", 0);
        int height = call.getInt("height", 0);
        printer.setCallbackContext(call);
        printer.setBarcode(align, small, scale, hri, height);
    }

    @PluginMethod
    public void printBarcode(PluginCall call) {
        int type = call.getInt("type", 0);
        String data = call.getString("data");
        if (data != null) {
            printer.setCallbackContext(call);
            printer.printBarcode(type, data);
        } else {
            call.reject("Data is required");
        }
    }

    @PluginMethod
    public void printQRCode(PluginCall call) {
        int size = call.getInt("size", 0);
        int eccLv = call.getInt("eccLv", 0);
        String data = call.getString("data");
        if (data != null) {
            printer.setCallbackContext(call);
            printer.printQRCode(size, eccLv, data);
        } else {
            call.reject("Data is required");
        }
    }

    @PluginMethod
    public void printSelfTest(PluginCall call) {
        printer.setCallbackContext(call);
        printer.printSelfTest();
    }

    @PluginMethod
    public void drawPageRectangle(PluginCall call) {
        int x = call.getInt("x", 0);
        int y = call.getInt("y", 0);
        int width = call.getInt("width", 0);
        int height = call.getInt("height", 0);
        int fillMode = call.getInt("fillMode", 0);
        printer.setCallbackContext(call);
        printer.drawPageRectangle(x, y, width, height, fillMode);
    }

    @PluginMethod
    public void drawPageFrame(PluginCall call) {
        int x = call.getInt("x", 0);
        int y = call.getInt("y", 0);
        int width = call.getInt("width", 0);
        int height = call.getInt("height", 0);
        int fillMode = call.getInt("fillMode", 0);
        int thickness = call.getInt("thickness", 0);
        printer.setCallbackContext(call);
        printer.drawPageFrame(x, y, width, height, fillMode, thickness);
    }

    @PluginMethod
    public void selectStandardMode(PluginCall call) {
        printer.setCallbackContext(call);
        printer.selectStandardMode();
    }

    @PluginMethod
    public void selectPageMode(PluginCall call) {
        printer.setCallbackContext(call);
        printer.selectPageMode();
    }

    @PluginMethod
    public void printPage(PluginCall call) {
        printer.setCallbackContext(call);
        printer.printPage();
    }

    @PluginMethod
    public void setPageRegion(PluginCall call) {
        int x = call.getInt("x", 0);
        int y = call.getInt("y", 0);
        int width = call.getInt("width", 0);
        int height = call.getInt("height", 0);
        int direction = call.getInt("direction", 0);
        printer.setCallbackContext(call);
        printer.setPageRegion(x, y, width, height, direction);
    }

    @PluginMethod
    public void printImage(PluginCall call) {
        String image = call.getString("image");
        int width = call.getInt("width", 0);
        int height = call.getInt("height", 0);
        int align = call.getInt("align", 0);
        if (image != null) {
            printer.setCallbackContext(call);
            printer.printImage(image, width, height, align);
        } else {
            call.reject("Image data is required");
        }
    }

    @PluginMethod
    public void disconnect(PluginCall call) {
        printer.disconnect(call);
    }
}
