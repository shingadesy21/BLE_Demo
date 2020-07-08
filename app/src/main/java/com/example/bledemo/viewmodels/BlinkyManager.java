package com.example.bledemo.viewmodels;


import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Deque;
import java.util.LinkedList;
import java.util.UUID;

import no.nordicsemi.android.ble.Request;
import no.nordicsemi.android.ble.livedata.ObservableBleManager;
import no.nordicsemi.android.log.LogContract;
import no.nordicsemi.android.log.LogSession;
import no.nordicsemi.android.log.Logger;

public class BlinkyManager extends ObservableBleManager {

    public final static UUID SERVICE_UUID = UUID.fromString("0000180D-0000-1000-8000-00805f9b34fb");
    private static final UUID MEASUREMENT_CHARACTERISTIC_UUID = UUID.fromString("00002A37-0000-1000-8000-00805f9b34fb");


    private BluetoothGattCharacteristic mCharacteristic;
    private LogSession logSession;
    private boolean supported;
    private boolean ledOn;

    public BlinkyManager(@NonNull final Context context) {
        super(context);
    }



    @NonNull
    @Override
    protected BleManagerGattCallback getGattCallback() {
        return new BlinkyBleManagerGattCallback();
    }

    public void setLogger(@Nullable final LogSession session) {
        logSession = session;
    }

    @Override
    public void log(final int priority, @NonNull final String message) {
        // The priority is a Log.X constant, while the Logger accepts it's log levels.
        Logger.log(logSession, LogContract.Log.Level.fromPriority(priority), message);
    }

    @Override
    protected boolean shouldClearCacheWhenDisconnected() {
        return !supported;
    }

    private class BlinkyBleManagerGattCallback extends BleManagerGattCallback {
        @Override
        protected Deque<Request> initGatt(final BluetoothGatt gatt) {
            final LinkedList<Request> requests = new LinkedList<>();
            // TODO initialize your device, enable required notifications and indications, write what needs to be written to start working
            requests.add(Request.newEnableNotificationsRequest(mCharacteristic));
            return requests;
        }

        @Override
        public boolean isRequiredServiceSupported(@NonNull final BluetoothGatt gatt) {
            final BluetoothGattService service = gatt.getService(SERVICE_UUID);
            if (service != null) {
                mCharacteristic = service.getCharacteristic(MEASUREMENT_CHARACTERISTIC_UUID);
            }
            return mCharacteristic != null;
        }


        @Override
        protected void onDeviceDisconnected() {
            mCharacteristic = null;
        }
    }



}
