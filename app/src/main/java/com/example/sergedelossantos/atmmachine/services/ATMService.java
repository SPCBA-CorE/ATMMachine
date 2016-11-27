package com.example.sergedelossantos.atmmachine.services;

/**
 * Created by serge.delossantos on 11/26/2016.
 */
import android.app.*;
import android.content.*;
import android.media.*;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.*;

import com.android.volley.*;
import com.android.volley.toolbox.*;

import org.json.*;

public class ATMService extends Service {
    public static final String
            ACTION_ATM_BROADCAST = ATMService.class.getName() + "ATMBroadcast";
    private static String TAG = ATMService.class.getSimpleName();
    private ATMThread mythread;
    public boolean isRunning = false;
    private String MachineNo = "ATM1";

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        mythread  = new ATMThread();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        if(isRunning){
            Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
            mythread.interrupt();
        }
        super.onDestroy();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.d(TAG, "onStart");
        if(!isRunning){
            Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
            mythread.start();
            isRunning = true;
        }
    }

    private void Response(JSONObject value){
        try {
            // Parsing json object response
            // response will be a json object
            boolean isconfirmed = value.getBoolean("ResultBoolean");
            String cardid = value.getString("CardId");

            //display it
            Intent intent = new Intent(ACTION_ATM_BROADCAST);
            intent.putExtra("isconfirmed",isconfirmed);
            intent.putExtra("cardid",cardid);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);


        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),
                    "Error: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    public void checkCardArrived(String machineno){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://unionbankphils.gear.host/api/Bank/CheckMachineNo?machineno="+ machineno;

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    public void onResponse(JSONObject response) {
                        Response(response);
                    }
                }, new Response.ErrorListener() {

                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                    }
                });

        queue.add(jsObjRequest);
    }

    class ATMThread extends Thread{
        static final long DELAY = 2000;
        @Override
        public void run(){
            while(isRunning){
                Log.d(TAG,"Running");
                try {
                    checkCardArrived(MachineNo);
                    Thread.sleep(DELAY);
                } catch (InterruptedException e) {
                    isRunning = false;
                    e.printStackTrace();
                }
            }
        }
    }
}
