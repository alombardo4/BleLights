package com.alec.blelights.controllers;


import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.app.Fragment;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.alec.blelights.R;
import com.alec.blelights.ble.RBLService;
import com.alec.blelights.model.BLELight;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LightControlFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LightControlFragment extends Fragment {

    private BLELight light;
    private View view;
    private SeekBar red, green, blue;
    private ImageView rI, gI, bI, overallColor;
    private EditText groupNumber;
    private Button updateButton, allOffButton, on100Button, on50Button;
    private RBLService mBluetoothLeService;

    private Map<UUID, BluetoothGattCharacteristic> map = new HashMap<UUID, BluetoothGattCharacteristic>();

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LightControlFragment.
     */
    public static LightControlFragment newInstance(BLELight light) {
        LightControlFragment fragment = new LightControlFragment();
        Bundle args = new Bundle();
        args.putSerializable("light", light);
        fragment.setArguments(args);
        return fragment;
    }

    public LightControlFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            light = (BLELight) getArguments().getSerializable("light");
        }
        Intent gattServiceIntent = new Intent(getActivity(), RBLService.class);
        getActivity().bindService(gattServiceIntent, mServiceConnection, getActivity().BIND_AUTO_CREATE);
        getActivity().registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_light_control, container, false);
        red = (SeekBar) view.findViewById(R.id.red_seek);
        red.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                rI.setBackground(new ColorDrawable(Color.rgb((int)255.0 * progress / 100, 0, 0)));
                overallColor.setBackground(new ColorDrawable(Color.rgb((int)255.0*red.getProgress()/100, (int)255.0*green.getProgress()/100, (int)255.0*blue.getProgress()/100)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        green = (SeekBar) view.findViewById(R.id.green_seek);
        green.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                gI.setBackground(new ColorDrawable(Color.rgb(0, (int) 255.0 * progress / 100, 0)));
                overallColor.setBackground(new ColorDrawable(Color.rgb((int)255.0*red.getProgress()/100, (int)255.0*green.getProgress()/100, (int)255.0*blue.getProgress()/100)));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        blue = (SeekBar) view.findViewById(R.id.blue_seek);
        blue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                bI.setBackground(new ColorDrawable(Color.rgb(0, 0, (int)255.0 * progress / 100)));
                overallColor.setBackground(new ColorDrawable(Color.rgb((int)255.0*red.getProgress()/100, (int)255.0*green.getProgress()/100, (int)255.0*blue.getProgress()/100)));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        rI = (ImageView) view.findViewById(R.id.red_image);
        gI = (ImageView) view.findViewById(R.id.green_image);
        bI = (ImageView) view.findViewById(R.id.blue_image);
        overallColor = (ImageView) view.findViewById(R.id.overall_color);
        updateButton = (Button) view.findViewById(R.id.update_button);
        allOffButton = (Button) view.findViewById(R.id.button_alloff);
        allOffButton.setOnClickListener(new AllOffButtonClicked());
        on100Button = (Button) view.findViewById(R.id.button_allon100);
        on100Button.setOnClickListener(new On100ButtonClicked());
        on50Button = (Button) view.findViewById(R.id.button_allon50);
        on50Button.setOnClickListener(new On50ButtonClicked());
        updateButton.setOnClickListener(new UpdateLightsClicked());
        groupNumber = (EditText) view.findViewById(R.id.group_id_edittext);

        return view;
    }

    private class UpdateLightsClicked implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            BluetoothGattCharacteristic characteristic = map.get(RBLService.UUID_BLE_SHIELD_TX);
            // 0 R G B Prog SlaveID Brightness
            byte[] tx = new byte[7];
            tx[0] = 0x00;
            tx[1] = (byte) red.getProgress();
            tx[2] = (byte) green.getProgress();
            tx[3] = (byte) blue.getProgress();
            tx[4] = 0x00;
            tx[5] = 0x00;
            try {
                byte group = Byte.parseByte(groupNumber.getText().toString());
                tx[5] = group;
            } catch (Exception e) {
                tx[5] = 0x00;
            }
            tx[6] = (byte) 100;
            mBluetoothLeService.connect(light.getAddress());
            characteristic.setValue(tx);
            mBluetoothLeService.writeCharacteristic(characteristic);
            Toast.makeText(getActivity(), "SENT UPDATE", Toast.LENGTH_SHORT).show();
        }
    }

    private abstract class SetAllLightsButtonClicked implements View.OnClickListener {

        public void setAll(int value) {
            blue.setProgress(value);
            green.setProgress(value);
            red.setProgress(value);
            groupNumber.setText("0");
        }
    }

    private class AllOffButtonClicked extends SetAllLightsButtonClicked {

        @Override
        public void onClick(View v) {
            setAll(0);
        }
    }
    private class On50ButtonClicked extends SetAllLightsButtonClicked {

        @Override
        public void onClick(View v) {
            setAll(50);
        }
    }
    private class On100ButtonClicked extends SetAllLightsButtonClicked {

        @Override
        public void onClick(View v) {
            setAll(100);
        }
    }
    private void getGattService(BluetoothGattService gattService) {
        if (gattService == null)
            return;

        BluetoothGattCharacteristic characteristic = gattService
                .getCharacteristic(RBLService.UUID_BLE_SHIELD_TX);
        map.put(characteristic.getUuid(), characteristic);

        BluetoothGattCharacteristic characteristicRx = gattService
                .getCharacteristic(RBLService.UUID_BLE_SHIELD_RX);
        mBluetoothLeService.setCharacteristicNotification(characteristicRx,
                true);
        mBluetoothLeService.readCharacteristic(characteristicRx);
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(RBLService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(RBLService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(RBLService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(RBLService.ACTION_DATA_AVAILABLE);

        return intentFilter;
    }

    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName,
                                       IBinder service) {
            mBluetoothLeService = ((RBLService.LocalBinder) service)
                    .getService();
            if (!mBluetoothLeService.initialize()) {
                System.out.println("Unable to initialize Bluetooth");
                getActivity().finish();
            }
            // Automatically connects to the device upon successful start-up
            // initialization.
            System.out.println(light);
            mBluetoothLeService.connect(light.getAddress());
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (RBLService.ACTION_GATT_DISCONNECTED.equals(action)) {
            } else if (RBLService.ACTION_GATT_SERVICES_DISCOVERED
                    .equals(action)) {
                getGattService(mBluetoothLeService.getSupportedGattService());
            } else if (RBLService.ACTION_DATA_AVAILABLE.equals(action)) {
                System.out.println(intent.getByteArrayExtra(RBLService.EXTRA_DATA));
            }
        }
    };
}
