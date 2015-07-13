package com.alec.blelights.adapters;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.alec.blelights.R;

import java.util.ArrayList;

/**
 * Created by alec on 7/12/15.
 */
public class BLEDeviceAdapter extends ArrayAdapter<BluetoothDevice> {

    public BLEDeviceAdapter(Context context, ArrayList<BluetoothDevice> devices) {
        super(context, 0, devices);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BluetoothDevice device = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.ble_device, parent, false);
        }

        TextView name = (TextView) convertView.findViewById(R.id.device_name);
        TextView mac = (TextView) convertView.findViewById(R.id.device_mac);

        name.setText(device.getName());
        mac.setText(device.getAddress());

        return convertView;
    }
}
