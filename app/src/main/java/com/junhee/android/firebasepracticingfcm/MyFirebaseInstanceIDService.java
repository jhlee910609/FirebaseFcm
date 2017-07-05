package com.junhee.android.firebasepracticingfcm;

import android.provider.Settings;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.junhee.android.firebasepracticingfcm.domain.Uid;

import static com.google.android.gms.internal.zzs.TAG;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference uidRef = database.getReference("uid");

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }


    public void sendRegistrationToServer(String token) {

        String deviceUid = Settings.Secure.getString(
                getContentResolver(), Settings.Secure.ANDROID_ID
        );
        Log.e("UUID ----", deviceUid + "");
        Uid uid = new Uid(deviceUid, "준희", token);
        uidRef.child(deviceUid).setValue(uid);
    }

}
