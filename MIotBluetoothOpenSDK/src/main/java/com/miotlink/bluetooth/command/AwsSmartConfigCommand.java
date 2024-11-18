package com.miotlink.bluetooth.command;

import org.json.JSONException;
import org.json.JSONObject;
public class AwsSmartConfigCommand extends AbsMessage {


    private String mac;
    private int mtu = 20;
    private String command = "";

    public AwsSmartConfigCommand(String command) {
        this.command = command;
    }


    public AwsSmartConfigCommand(String macCode, int mtu, String command) {
        this.command = command;
        this.mac = macCode;
        this.mtu = mtu;
    }


    @Override
    public String toString() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Code", "AwsSmartConfig");
            JSONObject jsonObjectValue = new JSONObject();
            jsonObjectValue.put("macCode", mac);
            jsonObjectValue.put("mtu", mtu);
            jsonObjectValue.put("command", command);
            jsonObject.put("Data", jsonObjectValue);
            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return "";

    }
}
