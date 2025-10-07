package com.adx.integration.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.adx.integration.data.model.Advertisement;
import com.adx.integration.databinding.FragmentAdPanelBinding;
import com.adx.integration.ui.adapters.AdvertisementAdapter;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Dialog fragment displaying available advertisements in a panel
 * Features:
 * - Shows newest ads at the bottom
 * - Video and image support for each ad
 * - Credit cost display
 * - One-click ad interaction
 */
@AndroidEntryPoint
public class AdPanelFragment extends DialogFragment implements AdvertisementAdapter.OnAdClickListener {

    private FragmentAdPanelBinding binding;
    private MainViewModel viewModel;
    private AdvertisementAdapter adapter;
    private List<Advertisement> advertisements = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Light_NoTitleBar_Fullscreen);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAdPanelBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        
        setupUI();
        setupObservers();
        loadAdvertisements();
    }

    private void setupUI() {
        // Setup toolbar
        binding.toolbar.setNavigationOnClickListener(v -> dismiss());
        binding.toolbar.setTitle("Available Advertisements");
        
        // Setup RecyclerView with newest ads at bottom
        adapter = new AdvertisementAdapter(requireContext(), advertisements, this);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(adapter);
        
        // Setup refresh
        binding.swipeRefresh.setOnRefreshListener(this::loadAdvertisements);
        
        // Setup empty state
        binding.emptyState.setVisibility(View.GONE);
    }

    private void setupObservers() {
        viewModel.getAvailableAdCount().observe(getViewLifecycleOwner(), count -> {
            // Update UI based on ad count
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                showError(error);
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.swipeRefresh.setRefreshing(isLoading);
        });
    }

    private void loadAdvertisements() {
        binding.swipeRefresh.setRefreshing(true);
        
        viewModel.loadAvailableAds().observe(getViewLifecycleOwner(), ads -> {
            binding.swipeRefresh.setRefreshing(false);
            
            if (ads != null && !ads.isEmpty()) {
                advertisements.clear();
                advertisements.addAll(ads);
                adapter.notifyDataSetChanged();
                
                binding.recyclerView.setVisibility(View.VISIBLE);
                binding.emptyState.setVisibility(View.GONE);
                
                // Scroll to bottom to show newest ads
                binding.recyclerView.scrollToPosition(advertisements.size() - 1);
            } else {
                binding.recyclerView.setVisibility(View.GONE);
                binding.emptyState.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onAdClick(Advertisement ad) {
        // Show ad details dialog
        AdDetailDialogFragment dialog = AdDetailDialogFragment.newInstance(ad);
        dialog.show(getChildFragmentManager(), "AdDetail");
    }

    @Override
    public void onAdReceive(Advertisement ad) {
        viewModel.interactWithAd(ad, "receive");
        showMessage("Ad received! " + ad.getReceiveCost() + " credits deducted");
    }

    @Override
    public void onAdClickInteraction(Advertisement ad) {
        viewModel.interactWithAd(ad, "click");
        showMessage("Ad clicked! " + ad.getClickCost() + " credits deducted");
        
        // Open advertiser website if available
        if (ad.getAdvertiser() != null && ad.getAdvertiser().getWebsite() != null) {
            openWebsite(ad.getAdvertiser().getWebsite());
        }
    }

    @Override
    public void onStoreClick(Advertisement ad) {
        if (ad.getAdvertiser() != null && ad.getAdvertiser().getStoreId() != null) {
            // Navigate to store panel
            StorePanelFragment storePanel = new StorePanelFragment();
            Bundle args = new Bundle();
            args.putString("storeId", ad.getAdvertiser().getStoreId());
            storePanel.setArguments(args);
            storePanel.show(getChildFragmentManager(), "StorePanel");
        }
    }

    private void openWebsite(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, android.net.Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            showError("Unable to open website");
        }
    }

    private void showMessage(String message) {
        com.google.android.material.snackbar.Snackbar.make(binding.getRoot(), message, 
                com.google.android.material.snackbar.Snackbar.LENGTH_SHORT).show();
    }

    private void showError(String error) {
        com.google.android.material.snackbar.Snackbar.make(binding.getRoot(), error, 
                com.google.android.material.snackbar.Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}