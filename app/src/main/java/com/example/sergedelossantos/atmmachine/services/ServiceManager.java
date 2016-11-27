package com.example.sergedelossantos.atmmachine.services;

import android.content.Context;
import android.content.Intent;
import android.view.View;

/**
 * Created by serge.delossantos on 11/26/2016.
 */


public class ServiceManager {

    private Context m_intent;

    public ServiceManager(Context intent){
        setM_intent(intent);
    }

    // Method to start the service
    public void startService(View view) {
        getM_intent().startService(new Intent(getM_intent(),ATMService.class));
    }

    // Method to stop the service
    public void stopService(View view) {
        getM_intent().stopService(new Intent(getM_intent(),ATMService.class));
    }

    private Context getM_intent() {
        return m_intent;
    }

    private void setM_intent(Context m_intent) {
        this.m_intent = m_intent;
    }
}
