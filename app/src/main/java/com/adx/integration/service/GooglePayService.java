package com.adx.integration.service;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.FragmentActivity;

import com.adx.integration.R;
import com.google.android.gms.pay.Pay;
import com.google.android.gms.pay.PayApiAvailabilityStatus;
import com.google.android.gms.pay.PayClient;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.gms.wallet.PaymentDataRequest;
import com.google.android.gms.wallet.PaymentsClient;
import com.google.android.gms.wallet.Wallet;
import com.google.android.gms.wallet.WalletConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Optional;

/**
 * Service for handling Google Pay integration
 * Manages payment requests and processing
 */
public class GooglePayService {

    private static final String GATEWAY_TOKENIZATION_NAME = "stripe";
    private static final String STRIPE_PUBLISHABLE_KEY = "pk_test_your_stripe_key";
    private static final String GATEWAY_MERCHANT_ID = "your_gateway_merchant_id";

    private final Context context;
    private final PaymentsClient paymentsClient;
    private final PayClient payClient;
    private ActivityResultLauncher<Intent> paymentResultLauncher;

    public GooglePayService(Context context) {
        this.context = context;
        this.paymentsClient = createPaymentsClient();
        this.payClient = Pay.getClient(context);
    }

    private PaymentsClient createPaymentsClient() {
        Wallet.WalletOptions walletOptions = new Wallet.WalletOptions.Builder()
                .setEnvironment(WalletConstants.ENVIRONMENT_TEST) // Change to PRODUCTION for release
                .build();
        
        return Wallet.getPaymentsClient(context, walletOptions);
    }

    /**
     * Initialize Google Pay and check availability
     */
    public void initializeGooglePay(FragmentActivity activity, GooglePayCallback callback) {
        setupPaymentLauncher(activity);
        
        Optional.ofNullable(paymentsClient).ifPresent(client -> {
            IsReadyToPayRequest request = createIsReadyToPayRequest();
            Task<Boolean> task = client.isReadyToPay(request);
            
            task.addOnCompleteListener(task1 -> {
                if (task1.isSuccessful()) {
                    callback.onGooglePayAvailable(task1.getResult());
                } else {
                    callback.onGooglePayError("Google Pay not available");
                }
            });
        });
    }

    private void setupPaymentLauncher(FragmentActivity activity) {
        paymentResultLauncher = activity.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        PaymentData paymentData = PaymentData.getFromIntent(result.getData());
                        if (paymentData != null) {
                            handlePaymentSuccess(paymentData);
                        }
                    } else {
                        handlePaymentCancelled();
                    }
                }
        );
    }

    private IsReadyToPayRequest createIsReadyToPayRequest() {
        try {
            JSONObject request = new JSONObject();
            request.put("apiVersion", 2);
            request.put("apiVersionMinor", 0);
            
            JSONArray allowedPaymentMethods = new JSONArray();
            allowedPaymentMethods.put(getBaseCardPaymentMethod());
            request.put("allowedPaymentMethods", allowedPaymentMethods);
            
            return IsReadyToPayRequest.fromJson(request.toString());
        } catch (JSONException e) {
            throw new RuntimeException("Error creating IsReadyToPayRequest", e);
        }
    }

    /**
     * Create payment data request for credit purchase
     */
    public void createPaymentDataRequest(double amount, String currency) {
        try {
            JSONObject request = new JSONObject();
            request.put("apiVersion", 2);
            request.put("apiVersionMinor", 0);
            
            // Merchant info
            JSONObject merchantInfo = new JSONObject();
            merchantInfo.put("merchantName", "ADX Integration");
            merchantInfo.put("merchantId", "your_merchant_id");
            request.put("merchantInfo", merchantInfo);
            
            // Allowed payment methods
            JSONArray allowedPaymentMethods = new JSONArray();
            allowedPaymentMethods.put(getGatewayCardPaymentMethod());
            request.put("allowedPaymentMethods", allowedPaymentMethods);
            
            // Transaction info
            JSONObject transactionInfo = new JSONObject();
            transactionInfo.put("totalPriceStatus", "FINAL");
            transactionInfo.put("totalPrice", String.format("%.2f", amount));
            transactionInfo.put("currencyCode", currency);
            
            JSONArray displayItems = new JSONArray();
            JSONObject displayItem = new JSONObject();
            displayItem.put("label", "ADX Credits");
            displayItem.put("type", "LINE_ITEM");
            displayItem.put("price", String.format("%.2f", amount));
            displayItems.put(displayItem);
            transactionInfo.put("displayItems", displayItems);
            
            request.put("transactionInfo", transactionInfo);
            
            PaymentDataRequest paymentDataRequest = PaymentDataRequest.fromJson(request.toString());
            launchPayment(paymentDataRequest);
            
        } catch (JSONException e) {
            throw new RuntimeException("Error creating payment request", e);
        }
    }

    private JSONObject getBaseCardPaymentMethod() throws JSONException {
        JSONObject cardPaymentMethod = new JSONObject();
        cardPaymentMethod.put("type", "CARD");
        
        JSONObject parameters = new JSONObject();
        parameters.put("allowedAuthMethods", getAllowedAuthMethods());
        parameters.put("allowedCardNetworks", getAllowedCardNetworks());
        cardPaymentMethod.put("parameters", parameters);
        
        return cardPaymentMethod;
    }

    private JSONObject getGatewayCardPaymentMethod() throws JSONException {
        JSONObject cardPaymentMethod = getBaseCardPaymentMethod();
        
        JSONObject tokenizationSpecification = new JSONObject();
        tokenizationSpecification.put("type", "PAYMENT_GATEWAY");
        
        JSONObject parameters = new JSONObject();
        parameters.put("gateway", GATEWAY_TOKENIZATION_NAME);
        parameters.put("gatewayMerchantId", GATEWAY_MERCHANT_ID);
        parameters.put("stripe:publishableKey", STRIPE_PUBLISHABLE_KEY);
        parameters.put("stripe:version", "2018-10-31");
        tokenizationSpecification.put("parameters", parameters);
        
        cardPaymentMethod.put("tokenizationSpecification", tokenizationSpecification);
        
        return cardPaymentMethod;
    }

    private JSONArray getAllowedAuthMethods() throws JSONException {
        return new JSONArray()
                .put("PAN_ONLY")
                .put("CRYPTOGRAM_3DS");
    }

    private JSONArray getAllowedCardNetworks() throws JSONException {
        return new JSONArray()
                .put("AMEX")
                .put("DISCOVER")
                .put("INTERAC")
                .put("JCB")
                .put("MASTERCARD")
                .put("VISA");
    }

    private void launchPayment(PaymentDataRequest paymentDataRequest) {
        if (paymentsClient != null && paymentResultLauncher != null) {
            Task<PaymentData> futurePaymentData = paymentsClient.loadPaymentData(paymentDataRequest);
            
            futurePaymentData.addOnCompleteListener(task -> {
                try {
                    PaymentData paymentData = task.getResult();
                    if (paymentData != null) {
                        Intent intent = new Intent();
                        PaymentData.putIntoIntent(paymentData, intent);
                        paymentResultLauncher.launch(intent);
                    }
                } catch (Exception e) {
                    handlePaymentError(e);
                }
            });
        }
    }

    private void handlePaymentSuccess(PaymentData paymentData) {
        // Extract payment token and send to backend
        try {
            JSONObject paymentDataJson = new JSONObject(paymentData.toJson());
            JSONObject paymentMethodData = paymentDataJson.getJSONObject("paymentMethodData");
            JSONObject tokenizationData = paymentMethodData.getJSONObject("tokenizationData");
            String token = tokenizationData.getString("token");
            
            // Send token to backend for processing
            processPaymentToken(token);
            
        } catch (JSONException e) {
            handlePaymentError(e);
        }
    }

    private void handlePaymentCancelled() {
        // Handle payment cancellation
        android.util.Log.d("GooglePayService", "Payment cancelled by user");
    }

    private void handlePaymentError(Exception error) {
        android.util.Log.e("GooglePayService", "Payment error", error);
        // Handle payment error
    }

    private void processPaymentToken(String token) {
        // This would call your backend API to process the payment
        // For now, just log it
        android.util.Log.d("GooglePayService", "Processing payment token: " + token);
        
        // You would typically send this to your backend and then:
        // 1. Create Stripe payment intent
        // 2. Confirm payment with token
        // 3. Update user credits
        // 4. Notify UI of success
    }

    /**
     * Interface for Google Pay callbacks
     */
    public interface GooglePayCallback {
        void onGooglePayAvailable(boolean isAvailable);
        void onGooglePayError(String error);
        void onPaymentSuccess(String token);
        void onPaymentCancelled();
        void onPaymentError(String error);
    }
}