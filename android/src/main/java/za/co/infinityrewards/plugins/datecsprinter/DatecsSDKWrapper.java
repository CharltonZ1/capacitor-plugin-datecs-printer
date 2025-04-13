package za.co.infinityrewards.plugins.datecsprinter;

import android.app.Application;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
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
    private boolean mRestart;
    private String mAddress;
    private PluginCall mConnectCallback;
    private PluginCall mCallback;
    private final Application app;
    private Activity activity;

    private final ProtocolAdapter.PrinterListener mChannelListener = new ProtocolAdapter.PrinterListener() {
        @Override
        public void onPaperStateChanged(boolean hasNoPaper) {
            if (hasNoPaper) {
                showToast(DatecsUtil.getStringFromStringResource(app, "no_paper"));
            } else {
                showToast(DatecsUtil.getStringFromStringResource(app, "paper_ok"));
            }
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
            Log.e(LOG_TAG, e.getMessage());
            showToast(e.getMessage());
        }
        return json;
    }

    public void getBluetoothPairedDevices(PluginCall call) {
        BluetoothAdapter mBluetoothAdapter = null;
        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                call.reject(errorCode.get(1), "1");
                return;
            }
            if (!mBluetoothAdapter.isEnabled()) {
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
                        e.printStackTrace();
                    }
                    map.put("type", deviceType);
                    map.put("address", device.getAddress());
                    map.put("name", device.getName());
                    String deviceAlias = device.getName();
                    try {
                        java.lang.reflect.Method method = device.getClass().getMethod("getAliasName");
                        if (method != null) {
                            deviceAlias = (String) method.invoke(device);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    map.put("aliasName", deviceAlias);
                    JSONObject jObj = new JSONObject(map);
                    json.put(jObj);
                }
                call.resolve(new JSObject().put("devices", json));
            } else {
                call.reject(errorCode.get(2), "2");
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
            e.printStackTrace();
            call.reject(e.getMessage());
        }
    }

    public void setAddress(String address) {
        mAddress = address;
    }

    public void setWebView(Object webView) {
        // Capacitor doesn't need this, but keep for compatibility
    }

    public void setCallbackContext(PluginCall callback) {
        mCallback = callback;
    }

    public void connect(PluginCall call) {
        mConnectCallback = call;
        closeActiveConnections();
        if (BluetoothAdapter.checkBluetoothAddress(mAddress)) {
            establishBluetoothConnection(mAddress, call);
        }
    }

    public synchronized void closeActiveConnections() {
        closePrinterConnection();
        closeBluetoothConnection();
    }

    private synchronized void closePrinterConnection() {
        if (mPrinter != null) {
            mPrinter.close();
        }
        if (mProtocolAdapter != null) {
            mProtocolAdapter.close();
        }
    }

    private synchronized void closeBluetoothConnection() {
        BluetoothSocket socket = mBluetoothSocket;
        mBluetoothSocket = null;
        if (socket != null) {
            try {
                Thread.sleep(50);
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void establishBluetoothConnection(final String address, final PluginCall call) {
        final DatecsSDKWrapper sdk = this;
        runJob(new Runnable() {
            @Override
            public void run() {
                BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
                BluetoothDevice device = adapter.getRemoteDevice(address);
                UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
                InputStream in = null;
                OutputStream out = null;
                adapter.cancelDiscovery();

                try {
                    mBluetoothSocket = createBluetoothSocket(device, uuid, call);
                    Thread.sleep(50);
                    mBluetoothSocket.connect();
                    in = mBluetoothSocket.getInputStream();
                    out = mBluetoothSocket.getOutputStream();
                } catch (IOException e) {
                    try {
                        mBluetoothSocket = (BluetoothSocket) device.getClass().getMethod("createRfcommSocket", new Class[]{int.class}).invoke(device, 1);
                        Thread.sleep(50);
                        mBluetoothSocket.connect();
                        in = mBluetoothSocket.getInputStream();
                        out = mBluetoothSocket.getOutputStream();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        call.reject(errorCode.get(18) + ex.getMessage(), "18");
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    call.reject(errorCode.get(18) + e.getMessage(), "18");
                    return;
                }

                try {
                    initializePrinter(in, out, call);
                    showToast(DatecsUtil.getStringFromStringResource(app, "printer_connected"));
                    call.resolve();
                } catch (IOException e) {
                    e.printStackTrace();
                    call.reject(errorCode.get(20), "20");
                    return;
                }
            }
        }, DatecsUtil.getStringFromStringResource(app, "printer"), DatecsUtil.getStringFromStringResource(app, "connecting"));
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device, UUID uuid, final PluginCall call) throws IOException {
        try {
            java.lang.reflect.Method method = device.getClass().getMethod("createRfcommSocketToServiceRecord", new Class[]{UUID.class});
            return (BluetoothSocket) method.invoke(device, uuid);
        } catch (Exception e) {
            e.printStackTrace();
            call.reject(errorCode.get(19), "19");
            showError(DatecsUtil.getStringFromStringResource(app, "failed_to_comm") + ": " + e.getMessage(), false);
        }
        return device.createRfcommSocketToServiceRecord(uuid);
    }

    protected void initializePrinter(InputStream inputStream, OutputStream outputStream, PluginCall call) throws IOException {
        mProtocolAdapter = new ProtocolAdapter(inputStream, outputStream);
        if (mProtocolAdapter.isProtocolEnabled()) {
            mProtocolAdapter.setPrinterListener(mChannelListener);
            final ProtocolAdapter.Channel channel = mProtocolAdapter.getChannel(ProtocolAdapter.CHANNEL_PRINTER);
            mPrinter = new Printer(channel.getInputStream(), channel.getOutputStream());
        } else {
            mPrinter = new Printer(mProtocolAdapter.getRawInputStream(), mProtocolAdapter.getRawOutputStream());
        }

        mPrinter.setConnectionListener(new Printer.ConnectionListener() {
            @Override
            public void onDisconnect() {
                // Handle disconnect event if needed
                // sendStatusUpdate(false);
            }
        });
        call.resolve();
    }

    public void feedPaper(int linesQuantity) {
        if (linesQuantity < 0 || linesQuantity > 255) {
            mCallback.reject(errorCode.get(3), "3");
            return;
        }
        try {
            mPrinter.feedPaper(linesQuantity);
            mPrinter.flush();
            mCallback.resolve();
        } catch (Exception e) {
            e.printStackTrace();
            mCallback.reject(errorCode.get(4) + e.getMessage(), "4");
        }
    }

    public void printTaggedText(String text, String charset) {
        try {
            mPrinter.printTaggedText(text, charset);
            mPrinter.flush();
            mCallback.resolve();
        } catch (Exception e) {
            e.printStackTrace();
            mCallback.reject(errorCode.get(5) + e.getMessage(), "5");
        }
    }

    public void writeHex(String s) {
        write(DatecsUtil.hexStringToByteArray(s));
    }

    public void write(byte[] b) {
        try {
            mPrinter.write(b);
            mPrinter.flush();
            mCallback.resolve();
        } catch (Exception e) {
            e.printStackTrace();
            mCallback.reject(errorCode.get(21) + e.getMessage(), "21");
        }
    }

    public void getStatus() {
        try {
            int status = mPrinter.getStatus();
            JSObject ret = new JSObject();
            ret.put("status", status);
            mCallback.resolve(ret);
        } catch (Exception e) {
            e.printStackTrace();
            mCallback.reject(errorCode.get(6) + e.getMessage(), "6");
        }
    }

    public void getTemperature() {
        try {
            int temperature = mPrinter.getTemperature();
            JSObject ret = new JSObject();
            ret.put("temperature", temperature);
            mCallback.resolve(ret);
        } catch (Exception e) {
            e.printStackTrace();
            mCallback.reject(errorCode.get(7) + e.getMessage(), "7");
        }
    }

    public void setBarcode(int align, boolean small, int scale, int hri, int height) {
        try {
            mPrinter.setBarcode(align, small, scale, hri, height);
            mCallback.resolve();
        } catch (Exception e) {
            e.printStackTrace();
            mCallback.reject(errorCode.get(10) + e.getMessage(), "10");
        }
    }

    public void printBarcode(int type, String data) {
        try {
            mPrinter.printBarcode(type, data);
            mPrinter.flush();
            mCallback.resolve();
        } catch (Exception e) {
            e.printStackTrace();
            mCallback.reject(errorCode.get(8) + e.getMessage(), "8");
        }
    }

    public void printQRCode(int size, int eccLv, String data) {
        try {
            mPrinter.printQRCode(size, eccLv, data);
            mPrinter.flush();
            mCallback.resolve();
        } catch (Exception e) {
            e.printStackTrace();
            mCallback.reject(errorCode.get(22) + e.getMessage(), "22");
        }
    }

    public void printSelfTest() {
        try {
            mPrinter.printSelfTest();
            mPrinter.flush();
            mCallback.resolve();
        } catch (Exception e) {
            e.printStackTrace();
            mCallback.reject(errorCode.get(9) + e.getMessage(), "9");
        }
    }

    public void drawPageRectangle(int x, int y, int width, int height, int fillMode) {
        try {
            mPrinter.drawPageRectangle(x, y, width, height, fillMode);
            mCallback.resolve();
        } catch (Exception e) {
            e.printStackTrace();
            mCallback.reject(errorCode.get(12) + e.getMessage(), "12");
        }
    }

    public void drawPageFrame(int x, int y, int width, int height, int fillMode, int thickness) {
        try {
            mPrinter.drawPageFrame(x, y, width, height, fillMode, thickness);
            mCallback.resolve();
        } catch (Exception e) {
            e.printStackTrace();
            mCallback.reject(errorCode.get(16) + e.getMessage(), "16");
        }
    }

    public void selectStandardMode() {
        try {
            mPrinter.selectStandardMode();
            mCallback.resolve();
        } catch (Exception e) {
            e.printStackTrace();
            mCallback.reject(errorCode.get(13) + e.getMessage(), "13");
        }
    }

    public void selectPageMode() {
        try {
            mPrinter.selectPageMode();
            mCallback.resolve();
        } catch (Exception e) {
            e.printStackTrace();
            mCallback.reject(errorCode.get(14) + e.getMessage(), "14");
        }
    }

    public void printPage() {
        try {
            mPrinter.printPage();
            mPrinter.flush();
            mCallback.resolve();
        } catch (Exception e) {
            e.printStackTrace();
            mCallback.reject(errorCode.get(17) + e.getMessage(), "17");
        }
    }

    public void setPageRegion(int x, int y, int width, int height, int direction) {
        try {
            mPrinter.setPageRegion(x, y, width, height, direction);
            mCallback.resolve();
        } catch (Exception e) {
            e.printStackTrace();
            mCallback.reject(errorCode.get(15) + e.getMessage(), "15");
        }
    }

    public void printImage(String image, int width, int height, int align) {
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
            mCallback.resolve();
        } catch (Exception e) {
            e.printStackTrace();
            mCallback.reject(errorCode.get(11) + e.getMessage(), "11");
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
        if (resetConnection) {
            connect(mConnectCallback);
        }
    }

    private void showToast(final String text) {
        Log.d(LOG_TAG, text);
    }
}