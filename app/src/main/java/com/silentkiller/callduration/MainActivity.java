package com.silentkiller.callduration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.util.Log;
import android.widget.TextView;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    TextView durationTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        durationTv = findViewById(R.id.duration_tv);

        TextView nameTv = findViewById(R.id.name_tv);

        if (!checkPermissions()) {
            requestPermissions();
        } else {

            getCallDetails();
        }
    }

    private void getCallDetails() {
        float totalSeconds = 0;
        Uri contacts = CallLog.Calls.CONTENT_URI;
        Cursor managedCursor = this.getContentResolver().query(contacts, null, null, null, null);
        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);

        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);

        while (managedCursor.moveToNext()) {

            String phNumber = managedCursor.getString(number);

            if (phNumber.equals("+91XXXXXXXXXX") || phNumber.equals("XXXXXXXXXX")) {
                String callType = managedCursor.getString(type);
                String callDate = managedCursor.getString(date);
                String callDayTime = new Date(Long.valueOf(callDate)).toString();
                //long timestamp = convertDateToTimestamp(callDayTime);
                String callDuration = managedCursor.getString(duration);
                String dir = null;
                int dirCode = Integer.parseInt(callType);
                switch (dirCode) {
                    case CallLog.Calls.OUTGOING_TYPE:
                        dir = "OUTGOING";
                        break;

                    case CallLog.Calls.INCOMING_TYPE:
                        dir = "INCOMING";
                        break;

                    case CallLog.Calls.MISSED_TYPE:
                        dir = "MISSED";
                        break;
                }

                totalSeconds += Integer.parseInt(callDuration);
                //System.out.println("Number: " + phNumber + " Date: " + callDayTime + " Duration in sec: " + callDuration);
            }

        }
        managedCursor.close();

        /*float min = totalSeconds/60;
        float hours = min/60;*/

        int sec = (int) (totalSeconds % 60);
        int min = (int) ((totalSeconds / 60)%60);
        int hours = (int) ((totalSeconds/60)/60);

        durationTv.setText(hours+"h "+min+"m "+sec+"s");
        Log.d("Duration", "Total hours: " + hours+":"+min+":"+sec);
    }

    /**
     * this method request to permission asked.
     */
    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_CALL_LOG);

        if (shouldProvideRationale) {
        } else {
            Log.i("Error", "Requesting permission");
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_CALL_LOG},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    /**
     * this method check permission and return current state of permission need.
     */
    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_CALL_LOG);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i("Error", "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted. Kick off the process of building and connecting
                // GoogleApiClient.
                getCallDetails();
            } else {
            }
        }

    }
}
