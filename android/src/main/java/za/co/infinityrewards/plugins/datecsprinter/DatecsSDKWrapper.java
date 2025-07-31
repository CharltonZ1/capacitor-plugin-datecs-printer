package za.co.infinityrewards.plugins.datecsprinter;

import android.app.Activity;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.util.Log;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.datecs.api.printer.ProtocolAdapter;
import com.getcapacitor.PluginCall;
import com.getcapacitor.JSObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

public class DatecsSDKWrapper {
    private static final String LOG_TAG = "BluetoothPrinter";
    private Printer mPrinter;
    private ProtocolAdapter mProtocolAdapter;
    private BluetoothSocket mBluetoothSocket;
    private String mAddress;
    private PluginCall mConnectCallback;
    private final Application app;
    private Activity activity;

    private final ProtocolAdapter.PrinterListener mChannelListener = new ProtocolAdapter.PrinterListener() {
        @Override
        public void onPaperStateChanged(boolean hasNoPaper) {
            showToast(hasNoPaper ? DatecsUtil.getStringFromStringResource(app, "no_paper") : DatecsUtil.getStringFromStringResource(app, "paper_ok"));
        }

        @Override
        public void onThermalHeadStateChanged(boolean overheated) {
            if (overheated) {
                closeActiveConnections();
                showToast(DatecsUtil.getStringFromStringResource(app, "overheating"));
            }
        }

        @Override
        public void onBatteryStateChanged(boolean lowBattery) {
            showToast(DatecsUtil.getStringFromStringResource(app, "low_battery"));
        }
    };

    private Map<Integer, String> errorCode = new HashMap<>();

    public DatecsSDKWrapper(Activity activity) {
        this.activity = activity;
        this.app = activity.getApplication();
        this.errorCode.put(1, DatecsUtil.getStringFromStringResource(app, "err_no_bt_adapter"));
        this.errorCode.put(2, DatecsUtil.getStringFromStringResource(app, "err_no_bt_device"));
        this.errorCode.put(3, DatecsUtil.getStringFromStringResource(app, "err_lines_number"));
        this.errorCode.put(4, DatecsUtil.getStringFromStringResource(app, "err_feed_paper"));
        this.errorCode.put(5, DatecsUtil.getStringFromStringResource(app, "err_print"));
        this.errorCode.put(6, DatecsUtil.getStringFromStringResource(app, "err_fetch_st"));
        this.errorCode.put(7, DatecsUtil.getStringFromStringResource(app, "err_fetch_tmp"));
        this.errorCode.put(8, DatecsUtil.getStringFromStringResource(app, "err_print_barcode"));
        this.errorCode.put(9, DatecsUtil.getStringFromStringResource(app, "err_print_test"));
        this.errorCode.put(10, DatecsUtil.getStringFromStringResource(app, "err_set_barcode"));
        this.errorCode.put(11, DatecsUtil.getStringFromStringResource(app, "err_print_img"));
        this.errorCode.put(12, DatecsUtil.getStringFromStringResource(app, "err_print_rect"));
        this.errorCode.put(13, DatecsUtil.getStringFromStringResource(app, "err_print_rect"));
        this.errorCode.put(14, DatecsUtil.getStringFromStringResource(app, "err_print_rect"));
        this.errorCode.put(15, DatecsUtil.getStringFromStringResource(app, "err_print_rect"));
        this.errorCode.put(16, DatecsUtil.getStringFromStringResource(app, "err_print_rect"));
        this.errorCode.put(17, DatecsUtil.getStringFromStringResource(app, "err_print_rect"));
        this.errorCode.put(18, DatecsUtil.getStringFromStringResource(app, "failed_to_connect"));
        this.errorCode.put(19, DatecsUtil.getStringFromStringResource(app, "err_bt_socket"));
        this.errorCode.put(20, DatecsUtil.getStringFromStringResource(app, "failed_to_initialize"));
        this.errorCode.put(21, DatecsUtil.getStringFromStringResource(app, "err_write"));
        this.errorCode.put(22, DatecsUtil.getStringFromStringResource(app, "err_print_qrcode"));
    }

    private JSONObject getErrorByCode(int code) {
        return getErrorByCode(code, null);
    }

    private JSONObject getErrorByCode(int code, Exception exception) {
        JSONObject json = new JSONObject();
        try {
            json.put("errorCode", code);
            json.put("message", errorCode.get(code));
            if (exception != null) {
                json.put("exception", exception.getMessage());
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error creating error JSON", e);
            showToast(e.getMessage());
        }
        return json;
    }

    public void getBluetoothPairedDevices(PluginCall call) {
        BluetoothAdapter mBluetoothAdapter = null;
        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                Log.e(LOG_TAG, "No Bluetooth adapter found");
                call.reject(errorCode.get(1), "1");
                return;
            }
            if (!mBluetoothAdapter.isEnabled()) {
                Log.d(LOG_TAG, "Bluetooth not enabled, requesting enable");
                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                activity.startActivityForResult(enableBluetooth, 0);
            }
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                JSONArray json = new JSONArray();
                for (BluetoothDevice device : pairedDevices) {
                    Hashtable map = new Hashtable();
                    int deviceType = 0;
                    try {
                        java.lang.reflect.Method method = device.getClass().getMethod("getType");
                        if (method != null) {
                            deviceType = (Integer) method.invoke(device);
                        }
                    } catch (Exception e) {
                        Log.e(LOG_TAG, "Error getting device type", e);
                    }
                    map.put("type", deviceType);
                    map.put("address", device.getAddress());
                    map.put("id", device.getAddress()); // Added for compatibility
                    map.put("name", device.getName() != null ? device.getName() : "Unknown");
                    String deviceAlias = device.getName();
                    try {
                        java.lang.reflect.Method method = device.getClass().getMethod("getAliasName");
                        if (method != null) {
                            deviceAlias = (String) method.invoke(device);
                        }
                    } catch (Exception e) {
                        Log.e(LOG_TAG, "Error getting device alias", e);
                    }
                    map.put("aliasName", deviceAlias != null ? deviceAlias : "Unknown");
                    JSONObject jObj = new JSONObject(map);
                    Log.d(LOG_TAG, "Device: " + jObj.toString());
                    json.put(jObj);
                }
                JSObject result = new JSObject();
                result.put("devices", json);
                Log.d(LOG_TAG, "Devices found: " + json.toString());
                call.resolve(result);
            } else {
                Log.d(LOG_TAG, "No paired devices found, starting discovery for 'Inner Printer'");
                final BroadcastReceiver receiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        String action = intent.getAction();
                        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                            if (device != null && "Inner Printer".equals(device.getName())) {
                                Log.d(LOG_TAG, "Found 'Inner Printer', attempting to pair");
                                device.createBond();
                                context.unregisterReceiver(this);
                                // Re-call getBluetoothPairedDevices after attempting to pair
                                getBluetoothPairedDevices(call);
                            }
                        } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                            Log.d(LOG_TAG, "Discovery finished");
                            context.unregisterReceiver(this);
                            // If the printer is still not found, reject the call
                            if (mBluetoothAdapter.getBondedDevices().size() == 0) {
                                call.reject(errorCode.get(2), "2");
                            }
                        }
                    }
                };
                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
                activity.registerReceiver(receiver, filter);
                mBluetoothAdapter.startDiscovery();
            }
        } catch (SecurityException e) {
            Log.e(LOG_TAG, "Bluetooth permission error", e);
            call.reject("Bluetooth permission not granted");
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error listing devices", e);
            call.reject(e.getMessage());
        }
    }

    public void setAddress(String address) {
        mAddress = address;
        Log.d(LOG_TAG, "Set Bluetooth address: " + address);
    }

    public void setWebView(Object webView) {
        // Capacitor doesn't need this, but keep for compatibility
    }

    public void connect(PluginCall call) {
        mConnectCallback = call;
        if (mAddress == null || !BluetoothAdapter.checkBluetoothAddress(mAddress)) {
            Log.e(LOG_TAG, "Invalid or missing Bluetooth address");
            call.reject("Invalid or missing Bluetooth address", "18");
            return;
        }
        closeActiveConnections();
        establishBluetoothConnection(mAddress, call);
    }

    public synchronized void closeActiveConnections() {
        closePrinterConnection();
        closeBluetoothConnection();
    }

    private synchronized void closePrinterConnection() {
        if (mPrinter != null) {
            try {
                mPrinter.close();
                Log.d(LOG_TAG, "Printer connection closed");
            } catch (Exception e) {
                Log.e(LOG_TAG, "Error closing printer connection", e);
            }
            mPrinter = null;
        }
        if (mProtocolAdapter != null) {
            try {
                mProtocolAdapter.close();
                Log.d(LOG_TAG, "Protocol adapter closed");
            } catch (Exception e) {
                Log.e(LOG_TAG, "Error closing protocol adapter", e);
            }
            mProtocolAdapter = null;
        }
    }

    private synchronized void closeBluetoothConnection() {
        BluetoothSocket socket = mBluetoothSocket;
        mBluetoothSocket = null;
        if (socket != null) {
            try {
                Thread.sleep(50);
                socket.close();
                Log.d(LOG_TAG, "Bluetooth socket closed");
            } catch (Exception e) {
                Log.e(LOG_TAG, "Error closing Bluetooth socket", e);
            }
        }
    }

    private void establishBluetoothConnection(final String address, final PluginCall call) {
        runJob(new Runnable() {
            @Override
            public void run() {
                BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
                if (adapter == null) {
                    Log.e(LOG_TAG, "No Bluetooth adapter found");
                    call.reject(errorCode.get(1), "1");
                    return;
                }
                BluetoothDevice device;
                try {
                    device = adapter.getRemoteDevice(address);
                    Log.d(LOG_TAG, "Connecting to device: " + device.getName() + " (" + address + ")");
                } catch (IllegalArgumentException e) {
                    Log.e(LOG_TAG, "Invalid device address: " + address, e);
                    call.reject("Invalid device address", "18");
                    return;
                }
                UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
                InputStream in = null;
                OutputStream out = null;
                adapter.cancelDiscovery();

                try {
                    mBluetoothSocket = createBluetoothSocket(device, uuid, call);
                    Thread.sleep(50);
                    mBluetoothSocket.connect();
                    if (!mBluetoothSocket.isConnected()) {
                        throw new IOException("Socket not connected");
                    }
                    in = mBluetoothSocket.getInputStream();
                    out = mBluetoothSocket.getOutputStream();
                    Log.d(LOG_TAG, "Bluetooth socket connected");
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Primary connection failed, trying fallback", e);
                    try {
                        mBluetoothSocket = (BluetoothSocket) device.getClass().getMethod("createRfcommSocket", new Class[]{int.class}).invoke(device, 1);
                        Thread.sleep(50);
                        mBluetoothSocket.connect();
                        if (!mBluetoothSocket.isConnected()) {
                            throw new IOException("Socket not connected");
                        }
                        in = mBluetoothSocket.getInputStream();
                        out = mBluetoothSocket.getOutputStream();
                        Log.d(LOG_TAG, "Fallback Bluetooth socket connected");
                    } catch (Exception ex) {
                        Log.e(LOG_TAG, "Fallback connection failed", ex);
                        call.reject(errorCode.get(18) + ": " + ex.getMessage(), "18");
                        return;
                    }
                } catch (Exception e) {
                    Log.e(LOG_TAG, "Connection error", e);
                    call.reject(errorCode.get(18) + ": " + e.getMessage(), "18");
                    return;
                } catch (Exception e) {
                    Log.e(LOG_TAG, "Connection error", e);
                    call.reject(errorCode.get(18) + ": " + e.getMessage(), "18");
                    return;
                }

                try {
                    initializePrinter(in, out, call);
                    showToast(DatecsUtil.getStringFromStringResource(app, "printer_connected"));
                    call.resolve();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Printer initialization failed", e);
                    call.reject(errorCode.get(20) + ": " + e.getMessage(), "20");
                }
            }
        }, DatecsUtil.getStringFromStringResource(app, "printer"), DatecsUtil.getStringFromStringResource(app, "connecting"));
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device, UUID uuid, final PluginCall call) throws IOException {
        try {
            java.lang.reflect.Method method = device.getClass().getMethod("createRfcommSocketToServiceRecord", new Class[]{UUID.class});
            return (BluetoothSocket) method.invoke(device, uuid);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error creating Bluetooth socket", e);
            call.reject(errorCode.get(19) + ": " + e.getMessage(), "19");
            showError(DatecsUtil.getStringFromStringResource(app, "failed_to_comm") + ": " + e.getMessage(), false);
        }
        return device.createRfcommSocketToServiceRecord(uuid);
    }

    protected void initializePrinter(InputStream inputStream, OutputStream outputStream, PluginCall call) throws IOException {
        Log.d(LOG_TAG, "Initializing printer...");
        mProtocolAdapter = new ProtocolAdapter(inputStream, outputStream);
        if (mProtocolAdapter.isProtocolEnabled()) {
            Log.d(LOG_TAG, "Protocol enabled, setting printer listener");
            mProtocolAdapter.setPrinterListener(mChannelListener);
            final ProtocolAdapter.Channel channel = mProtocolAdapter.getChannel(ProtocolAdapter.CHANNEL_PRINTER);
            mPrinter = new Printer(channel.getInputStream(), channel.getOutputStream());
        } else {
            Log.d(LOG_TAG, "Protocol disabled, using raw streams");
            mPrinter = new Printer(mProtocolAdapter.getRawInputStream(), mProtocolAdapter.getRawOutputStream());
        }

        if (mPrinter == null) {
            call.reject(errorCode.get(20), "20");
            return;
        }

        mPrinter.setConnectionListener(new Printer.ConnectionListener() {
            @Override
            public void onDisconnect() {
                Log.d(LOG_TAG, "Printer disconnected");
                if (mConnectCallback != null) {
                    mConnectCallback.reject("Printer disconnected", "18");
                }
            }
        });
        Log.d(LOG_TAG, "Printer initialized successfully");
        call.resolve();
    }

    public void feedPaper(int linesQuantity, PluginCall call) {
        if (mPrinter == null) {
            Log.e(LOG_TAG, "Cannot feed paper: Printer is not initialized");
            call.reject(errorCode.get(20), "20");
            return;
        }
        if (linesQuantity < 0 || linesQuantity > 255) {
            Log.e(LOG_TAG, "Invalid lines quantity: " + linesQuantity);
            call.reject(errorCode.get(3), "3");
            return;
        }
        try {
            mPrinter.feedPaper(linesQuantity);
            mPrinter.flush();
            call.resolve();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error feeding paper", e);
            call.reject(errorCode.get(4) + ": " + e.getMessage(), "4");
        }
    }

    public void printTaggedText(String text, String charset, PluginCall call) {
        if (mPrinter == null) {
            Log.e(LOG_TAG, "Cannot print text: Printer is not initialized");
            call.reject(errorCode.get(20), "20");
            return;
        }
        try {
            mPrinter.printTaggedText(text, charset != null ? charset : "UTF-8");
            mPrinter.flush();
            call.resolve();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error printing text", e);
            call.reject(errorCode.get(5) + ": " + e.getMessage(), "5");
        }
    }

    public void writeHex(String s, PluginCall call) {
        write(DatecsUtil.hexStringToByteArray(s), call);
    }

    public void write(byte[] b, PluginCall call) {
        if (mPrinter == null) {
            Log.e(LOG_TAG, "Cannot write data: Printer is not initialized");
            call.reject(errorCode.get(20), "20");
            return;
        }
        try {
            mPrinter.write(b);
            mPrinter.flush();
            call.resolve();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error writing data", e);
            call.reject(errorCode.get(21) + ": " + e.getMessage(), "21");
        }
    }

    public void getStatus(PluginCall call) {
        if (mPrinter == null) {
            Log.e(LOG_TAG, "Cannot get status: Printer is not initialized");
            call.reject(errorCode.get(20), "20");
            return;
        }
        try {
            int status = mPrinter.getStatus();
            JSObject ret = new JSObject();
            ret.put("status", status);
            call.resolve(ret);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error fetching status", e);
            call.reject(errorCode.get(6) + ": " + e.getMessage(), "6");
        }
    }

    public void getTemperature(PluginCall call) {
        if (mPrinter == null) {
            Log.e(LOG_TAG, "Cannot get temperature: Printer is not initialized");
            call.reject(errorCode.get(20), "20");
            return;
        }
        try {
            int temperature = mPrinter.getTemperature();
            JSObject ret = new JSObject();
            ret.put("temperature", temperature);
            call.resolve(ret);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error fetching temperature", e);
            call.reject(errorCode.get(7) + ": " + e.getMessage(), "7");
        }
    }

    public void setBarcode(int align, boolean small, int scale, int hri, int height, PluginCall call) {
        if (mPrinter == null) {
            Log.e(LOG_TAG, "Cannot set barcode: Printer is not initialized");
            call.reject(errorCode.get(20), "20");
            return;
        }
        try {
            mPrinter.setBarcode(align, small, scale, hri, height);
            call.resolve();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error setting barcode", e);
            call.reject(errorCode.get(10) + ": " + e.getMessage(), "10");
        }
    }

    public void printBarcode(int type, String data, PluginCall call) {
        if (mPrinter == null) {
            Log.e(LOG_TAG, "Cannot print barcode: Printer is not initialized");
            call.reject(errorCode.get(20), "20");
            return;
        }
        try {
            mPrinter.printBarcode(type, data);
            mPrinter.flush();
            call.resolve();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error printing barcode", e);
            call.reject(errorCode.get(8) + ": " + e.getMessage(), "8");
        }
    }

    public void printQRCode(int size, int eccLv, String data, PluginCall call) {
        if (mPrinter == null) {
            Log.e(LOG_TAG, "Cannot print QR code: Printer is not initialized");
            call.reject(errorCode.get(20), "20");
            return;
        }
        try {
            mPrinter.printQRCode(size, eccLv, data);
            mPrinter.flush();
            call.resolve();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error printing QR code", e);
            call.reject(errorCode.get(22) + ": " + e.getMessage(), "22");
        }
    }

    public void printSelfTest(PluginCall call) {
        if (mPrinter == null) {
            Log.e(LOG_TAG, "Cannot print self-test: Printer is not initialized");
            call.reject(errorCode.get(20), "20");
            return;
        }
        try {
            mPrinter.printSelfTest();
            mPrinter.flush();
            call.resolve();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error printing self-test", e);
            call.reject(errorCode.get(9) + ": " + e.getMessage(), "9");
        }
    }

    public void drawPageRectangle(int x, int y, int width, int height, int fillMode, PluginCall call) {
        if (mPrinter == null) {
            Log.e(LOG_TAG, "Cannot draw rectangle: Printer is not initialized");
            call.reject(errorCode.get(20), "20");
            return;
        }
        try {
            mPrinter.drawPageRectangle(x, y, width, height, fillMode);
            call.resolve();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error drawing rectangle", e);
            call.reject(errorCode.get(12) + ": " + e.getMessage(), "12");
        }
    }

    public void drawPageFrame(int x, int y, int width, int height, int fillMode, int thickness, PluginCall call) {
        if (mPrinter == null) {
            Log.e(LOG_TAG, "Cannot draw frame: Printer is not initialized");
            call.reject(errorCode.get(20), "20");
            return;
        }
        try {
            mPrinter.drawPageFrame(x, y, width, height, fillMode, thickness);
            call.resolve();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error drawing frame", e);
            call.reject(errorCode.get(16) + ": " + e.getMessage(), "16");
        }
    }

    public void selectStandardMode(PluginCall call) {
        if (mPrinter == null) {
            Log.e(LOG_TAG, "Cannot select standard mode: Printer is not initialized");
            call.reject(errorCode.get(20), "20");
            return;
        }
        try {
            mPrinter.selectStandardMode();
            call.resolve();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error selecting standard mode", e);
            call.reject(errorCode.get(13) + ": " + e.getMessage(), "13");
        }
    }

    public void selectPageMode(PluginCall call) {
        if (mPrinter == null) {
            Log.e(LOG_TAG, "Cannot select page mode: Printer is not initialized");
            call.reject(errorCode.get(20), "20");
            return;
        }
        try {
            mPrinter.selectPageMode();
            call.resolve();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error selecting page mode", e);
            call.reject(errorCode.get(14) + ": " + e.getMessage(), "14");
        }
    }

    public void printPage(PluginCall call) {
        if (mPrinter == null) {
            Log.e(LOG_TAG, "Cannot print page: Printer is not initialized");
            call.reject(errorCode.get(20), "20");
            return;
        }
        try {
            mPrinter.printPage();
            mPrinter.flush();
            call.resolve();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error printing page", e);
            call.reject(errorCode.get(17) + ": " + e.getMessage(), "17");
        }
    }

    public void setPageRegion(int x, int y, int width, int height, int direction, PluginCall call) {
        if (mPrinter == null) {
            Log.e(LOG_TAG, "Cannot set page region: Printer is not initialized");
            call.reject(errorCode.get(20), "20");
            return;
        }
        try {
            mPrinter.setPageRegion(x, y, width, height, direction);
            call.resolve();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error setting page region", e);
            call.reject(errorCode.get(15) + ": " + e.getMessage(), "15");
        }
    }

    public void printImage(String image, int width, int height, int align, PluginCall call) {
        if (mPrinter == null) {
            Log.e(LOG_TAG, "Cannot print image: Printer is not initialized");
            call.reject(errorCode.get(20), "20");
            return;
        }
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;
            byte[] decodedByte = Base64.decode(image, 0);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
            final int imgWidth = bitmap.getWidth();
            final int imgHeight = bitmap.getHeight();
            final int[] argb = new int[imgWidth * imgHeight];

            bitmap.getPixels(argb, 0, imgWidth, 0, 0, imgWidth, imgHeight);
            bitmap.recycle();

            mPrinter.printImage(argb, width, height, align, true);
            mPrinter.flush();
            call.resolve();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error printing image", e);
            call.reject(errorCode.get(11) + ": " + e.getMessage(), "11");
        }
    }

    private void runJob(final Runnable job, final String jobTitle, final String jobName) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Thread t = new Thread(() -> {
                    try {
                        job.run();
                    } finally {
                        // No dialog in Capacitor
                    }
                });
                t.start();
            }
        });
    }

    private void showError(final String text, boolean resetConnection) {
        Log.e(LOG_TAG, "Error: " + text);
        if (resetConnection && mConnectCallback != null) {
            connect(mConnectCallback);
        }
    }

    private void showToast(final String text) {
        Log.d(LOG_TAG, text);
    }

    public void disconnect(PluginCall call) {
        closeActiveConnections();
        call.resolve();
    }
}