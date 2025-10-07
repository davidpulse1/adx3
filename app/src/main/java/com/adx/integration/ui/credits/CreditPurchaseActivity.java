package com.adx.integration.ui.credits;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.adx.integration.R;
import com.adx.integration.databinding.ActivityCreditPurchaseBinding;
import com.adx.integration.ui.adapters.CreditPackageAdapter;
import com.adx.integration.ui.main.MainViewModel;
import com.google.android.gms.wallet.PaymentsClient;
import com.google.android.gms.wallet.Wallet;
import com.google.android.gms.wallet.WalletConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

/**
 * Activity for purchasing credits using Google Pay
 * Implements secure payment processing with Stripe backend
 */
public class CreditPurchaseActivity extends AppCompatActivity implements CreditPackageAdapter.OnPackageClickListener {

    private ActivityCreditPurchaseBinding binding;
    private CreditPurchaseViewModel viewModel;
    private CreditPackageAdapter adapter;
    private PaymentsClient paymentsClient;

    // Credit packages
    private final List<CreditPackage> creditPackages = Arrays.asList(
            new CreditPackage("180 Credits", "$1.00", 180, 1.00),
            new CreditPackage("900 Credits", "$5.00", 900, 5.00),
            new CreditPackage("1,800 Credits", "$10.00", 1800, 10.00),
            new CreditPackage("3,600 Credits", "$20.00", 3600, 20.00),
            new CreditPackage("9,000 Credits", "$50.00", 9000, 50.00),
            new CreditPackage("18,000 Credits", "$100.00", 18000, 100.00)
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreditPurchaseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupUI();
        setupViewModel();
        setupGooglePay();
        setupRecyclerView();
    }

    private void setupUI() {
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Purchase Credits");
        
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(CreditPurchaseViewModel.class);
        
        viewModel.getPaymentResult().observe(this, result -> {
            if (result != null) {
                handlePaymentResult(result);
            }
        });

        viewModel.getError().observe(this, error -> {
            if (error != null) {
                showError(error);
            }
        });

        viewModel.getIsLoading().observe(this, isLoading -> {
            binding.progressBar.setVisibility(isLoading ? android.view.View.VISIBLE : android.view.View.GONE);
        });
    }

    private void setupGooglePay() {
        // Configure Google Pay
        Wallet.WalletOptions walletOptions = new Wallet.WalletOptions.Builder()
                .setEnvironment(WalletConstants.ENVIRONMENT_TEST) // Use ENVIRONMENT_PRODUCTION for release
                .build();

        paymentsClient = Wallet.getPaymentsClient(this, walletOptions);

        // Check if Google Pay is available
        viewModel.isGooglePayAvailable(paymentsClient).observe(this, isAvailable -> {
            if (!isAvailable) {
                showError("Google Pay is not available on this device");
            }
        });
    }

    private void setupRecyclerView() {
        adapter = new CreditPackageAdapter(creditPackages, this);
        binding.recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        binding.recyclerView.setAdapter(adapter);
    }

    @Override
    public void onPackageClick(CreditPackage creditPackage) {
        // Show Google Pay payment sheet
        showGooglePayPaymentSheet(creditPackage);
    }

    private void showGooglePayPaymentSheet(CreditPackage creditPackage) {
        try {
            JSONObject paymentDataRequest = createPaymentDataRequest(creditPackage);
            
            viewModel.loadPaymentData(paymentsClient, paymentDataRequest)
                    .observe(this, paymentData -> {
                        if (paymentData != null) {
                            // Process the payment
                            processPayment(paymentData, creditPackage);
                        }
                    });

        } catch (JSONException e) {
            showError("Failed to create payment request");
        }
    }

    private JSONObject createPaymentDataRequest(CreditPackage creditPackage) throws JSONException {
        JSONObject paymentDataRequest = new JSONObject();
        paymentDataRequest.put("apiVersion", 2);
        paymentDataRequest.put("apiVersionMinor", 0);

        // Merchant info
        JSONObject merchantInfo = new JSONObject();
        merchantInfo.put("merchantName", "ADX Integration");
        merchantInfo.put("merchantId", "BCR2DN6T3Z4L3L3L"); // Replace with your merchant ID
        paymentDataRequest.put("merchantInfo", merchantInfo);

        // Allowed payment methods
        JSONArray allowedPaymentMethods = new JSONArray();
        
        // Card payment method
        JSONObject cardPaymentMethod = new JSONObject();
        cardPaymentMethod.put("type", "CARD");
        
        JSONObject cardParameters = new JSONObject();
        cardParameters.put("allowedAuthMethods", new JSONArray()
                .put("PAN_ONLY")
                .put("CRYPTOGRAM_3DS"));
        
        JSONArray allowedCardNetworks = new JSONArray()
                .put("AMEX")
                .put("DISCOVER")
                .put("INTERAC")
                .put("JCB")
                .put("MASTERCARD")
                .put("VISA");
        cardParameters.put("allowedCardNetworks", allowedCardNetworks);
        cardPaymentMethod.put("parameters", cardParameters);

        // Tokenization specification
        JSONObject tokenizationSpec = new JSONObject();
        tokenizationSpec.put("type", "PAYMENT_GATEWAY");
        JSONObject gatewayParameters = new JSONObject();
        gatewayParameters.put("gateway", "stripe");
        gatewayParameters.put("stripe:publishableKey", "pk_test_1234567890"); // Replace with your key
        gatewayParameters.put("stripe:version", "2018-10-31");
        tokenizationSpec.put("parameters", gatewayParameters);
        
        cardPaymentMethod.put("tokenizationSpecification", tokenizationSpec);
        allowedPaymentMethods.put(cardPaymentMethod);
        paymentDataRequest.put("allowedPaymentMethods", allowedPaymentMethods);

        // Transaction info
        JSONObject transactionInfo = new JSONObject();
        transactionInfo.put("totalPriceStatus", "FINAL");
        transactionInfo.put("totalPrice", String.format("%.2f", creditPackage.getPrice()));
        transactionInfo.put("currencyCode", "USD");
        paymentDataRequest.put("transactionInfo", transactionInfo);

        return paymentDataRequest;
    }

    private void processPayment(String paymentData, CreditPackage creditPackage) {
        viewModel.processPayment(paymentData, creditPackage.getPrice())
                .observe(this, result -> {
                    if (result != null && result.isSuccess()) {
                        // Payment successful
                        showSuccess("Payment successful! " + creditPackage.getCredits() + " credits added.");
                        finish();
                    } else {
                        showError("Payment failed. Please try again.");
                    }
                });
    }

    private void handlePaymentResult(CreditPurchaseViewModel.PaymentResult result) {
        if (result.isSuccess()) {
            showSuccess("Successfully purchased " + result.getCreditsPurchased() + " credits!");
            setResult(RESULT_OK);
            finish();
        } else {
            showError("Payment failed: " + result.getErrorMessage());
        }
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void showSuccess(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Credit package data class
     */
    public static class CreditPackage {
        private final String name;
        private final String price;
        private final int credits;
        private final double priceValue;

        public CreditPackage(String name, String price, int credits, double priceValue) {
            this.name = name;
            this.price = price;
            this.credits = credits;
            this.priceValue = priceValue;
        }

        public String getName() {
            return name;
        }

        public String getPrice() {
            return price;
        }

        public int getCredits() {
            return credits;
        }

        public double getPriceValue() {
            return priceValue;
        }
    }
}