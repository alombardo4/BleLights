package com.alec.blelights.controllers;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.alec.blelights.R;
import com.alec.blelights.adapters.BLEDeviceAdapter;
import com.alec.blelights.database.LightDBAdapter;
import com.alec.blelights.model.BLELight;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.widget.Toast.LENGTH_SHORT;


/**
 * A placeholder fragment containing a simple view.
 */
public class NewLightFragment extends Fragment {
    private BluetoothAdapter mBluetoothAdapter;
    private static List<BluetoothDevice> mDevices;
    private View mView;
    private ListView list;
    private BLEDeviceAdapter adapter;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int SCAN_PERIOD = 3000;

    public NewLightFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_main, container, false);

        list = (ListView) mView.findViewById(R.id.deviceListView);
        mDevices = new ArrayList<>();
        adapter = new BLEDeviceAdapter(getActivity(), (ArrayList<BluetoothDevice>) mDevices);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LightDBAdapter db = LightDBAdapter.getInstance(getActivity());
                BluetoothDevice device = mDevices.get(position);

                BLELight light = new BLELight(device.getName(), device.getAddress(), device.getAddress(), false);
                db.addDevice(light);

                FragmentTransaction fr = getFragmentManager().beginTransaction();
                fr.replace(R.id.container, LightControlFragment.newInstance(light), null)
                        .addToBackStack(null)
                        .commit();
            }
        });

        final BluetoothManager mBluetoothManager = (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if(mBluetoothAdapter == null) {
            Toast.makeText(getActivity(), "BLE not supported", LENGTH_SHORT).show();
            getActivity().finish();
            return null;
        }
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        scanLEDevice();

        Timer mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("TIMER FINISHED");
                for (BluetoothDevice device : mDevices) {
                    System.out.println(device.getName());
                }
            }


        }, SCAN_PERIOD);
        return mView;
    }


    private void scanLEDevice() {
        new Thread() {
            @Override
            public void run() {
                mBluetoothAdapter.startLeScan(mLeScanCallback);

                try{
                    Thread.sleep(SCAN_PERIOD);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi,
                             byte[] scanRecord) {
            if (device != null) {
                if (mDevices.indexOf(device) == -1) {
                    mDevices.add(device);
                    adapter.notifyDataSetChanged();
                }
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            getActivity().finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
