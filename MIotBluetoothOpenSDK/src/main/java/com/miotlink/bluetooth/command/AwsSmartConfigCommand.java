package com.miotlink.bluetooth.command;

import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;

/**
 * AWS SmartConfig 命令
 */
public class AwsSmartConfigCommand extends AbsMessage {

    private static final String TAG = "AwsSmartConfigCommand";
    private static final String CODE_VALUE = "AwsSmartConfig";

    private final String mac;
    private final int mtu;
    private final String command;

    public AwsSmartConfigCommand(String command) {
        this("", 20, command);
    }

    public AwsSmartConfigCommand(String mac, int mtu, String command) {
        this.mac = mac != null ? mac : "";
        this.mtu = mtu > 0 ? mtu : 20;
        this.command = command != null ? command : "";
    }

    @Override
    public String toString() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Code", CODE_VALUE);

            JSONObject data = new JSONObject();
            data.put("macCode", mac);
            data.put("mtu", mtu);
            data.put("command", command);

            jsonObject.put("Data", data);
            return jsonObject.toString();
        } catch (JSONException e) {
            Log.e(TAG, "Failed to create JSON", e);
            return "";
        }
    }

    @Override
    protected byte[] getCommandData() throws Exception {
        throw new UnsupportedOperationException("Use packs() for AWS SmartConfig");
    }
}