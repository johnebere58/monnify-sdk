package com.monnify.monnify_payment_sdk.util;

import androidx.annotation.NonNull;

import java.io.IOException;

public class NoConnectivityException extends IOException {

    @NonNull
    @Override
    public String getMessage() {
        return "There is an issue with your internet connection. Please ensure your terminal is connected to the internet";
    }
}