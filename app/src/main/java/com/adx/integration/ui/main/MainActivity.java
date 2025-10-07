package com.adx.integration.ui.main;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.adx.integration.R;
import com.adx.integration.databinding.ActivityMainBinding;
import com.adx.integration.ui.auth.AuthActivity;
import com.adx.integration.ui.credits.CreditPurchaseActivity;
import com.adx.integration.utils.LocationPermissionHelper;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Main activity hosting the primary user interface
 * Manages navigation, bubble reminder, and location permissions
 */
@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MainViewModel viewModel;
    private NavController navController;
    private BadgeDrawable adBadge;
    
    private final ActivityResultLauncher<String> locationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    viewModel.startLocationTracking();
                } else {
                    showLocationPermissionDenied();
                }
            });

    private final ActivityResultLauncher<String[]> multiplePermissionsLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), permissions -> {
                boolean allGranted = true;
                for (Boolean granted : permissions.values()) {
                    if (!granted) {
                        allGranted = false;
                        break;
                    }
                }
                
                if (allGranted) {
                    viewModel.startLocationTracking();
                } else {
                    showLocationPermissionDenied();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        
        setupNavigation();
        setupBubbleReminder();
        setupObservers();
        checkAuthentication();
        requestLocationPermissions();
    }

    private void setupNavigation() {
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_dashboard, R.id.navigation_ads, R.id.navigation_stores, 
                R.id.navigation_credits, R.id.navigation_profile)
                .build();
        
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController);
    }

    private void setupBubbleReminder() {
        // Setup floating action button as bubble reminder
        binding.fabBubble.setOnClickListener(v -> showAdPanel());
        
        // Add badge for ad count
        adBadge = binding.fabBubble.getOrCreateBadge();
        adBadge.setVisible(false);
    }

    private void setupObservers() {
        // Observe available ad count
        viewModel.getAvailableAdCount().observe(this, count -> {
            if (count > 0) {
                adBadge.setNumber(count);
                adBadge.setVisible(true);
                animateBubbleReminder();
            } else {
                adBadge.setVisible(false);
            }
        });

        // Observe user authentication status
        viewModel.getUser().observe(this, user -> {
            if (user == null) {
                navigateToAuth();
            } else {
                updateUI(user);
            }
        });

        // Observe credit balance
        viewModel.getCreditBalance().observe(this, balance -> {
            if (balance != null) {
                updateCreditDisplay(balance.getCredits());
            }
        });

        // Observe errors
        viewModel.getError().observe(this, error -> {
            if (error != null) {
                showError(error);
            }
        });
    }

    private void checkAuthentication() {
        viewModel.checkAuthentication();
    }

    private void requestLocationPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ requires multiple permissions
            String[] permissions = {
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.POST_NOTIFICATIONS
            };
            multiplePermissionsLauncher.launch(permissions);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10+ requires background location permission
            String[] permissions = {
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
            };
            multiplePermissionsLauncher.launch(permissions);
        } else {
            // Older Android versions
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private void showAdPanel() {
        AdPanelFragment adPanel = new AdPanelFragment();
        adPanel.show(getSupportFragmentManager(), "AdPanel");
    }

    private void animateBubbleReminder() {
        binding.fabBubble.animate()
                .scaleX(1.2f)
                .scaleY(1.2f)
                .setDuration(300)
                .withEndAction(() -> binding.fabBubble.animate()
                        .scaleX(1.0f)
                        .scaleY(1.0f)
                        .setDuration(300)
                        .start())
                .start();
    }

    private void updateUI(User user) {
        // Update user avatar and username in toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Welcome, " + user.getUsername());
        }
    }

    private void updateCreditDisplay(int credits) {
        // Update credit display in UI
        // This would be implemented based on your specific UI design
    }

    private void navigateToAuth() {
        Intent intent = new Intent(this, AuthActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showLocationPermissionDenied() {
        Toast.makeText(this, "Location permission is required for optimal app experience", 
                      Toast.LENGTH_LONG).show();
    }

    private void showError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.startLocationTracking();
        viewModel.startAdSync();
    }

    @Override
    protected void onPause() {
        super.onPause();
        viewModel.stopLocationTracking();
        viewModel.stopAdSync();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}