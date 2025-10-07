package com.adx.integration.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adx.integration.data.model.Advertisement;
import com.adx.integration.databinding.ItemAdvertisementBinding;
import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.PlayerView;

import java.util.List;

/**
 * RecyclerView adapter for displaying advertisements
 * Supports both video and image content with credit cost display
 */
public class AdvertisementAdapter extends RecyclerView.Adapter<AdvertisementAdapter.AdViewHolder> {

    private final Context context;
    private final List<Advertisement> advertisements;
    private final OnAdClickListener listener;
    private ExoPlayer exoPlayer;

    public interface OnAdClickListener {
        void onAdClick(Advertisement ad);
        void onAdReceive(Advertisement ad);
        void onAdClickInteraction(Advertisement ad);
        void onStoreClick(Advertisement ad);
    }

    public AdvertisementAdapter(Context context, List<Advertisement> advertisements, OnAdClickListener listener) {
        this.context = context;
        this.advertisements = advertisements;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AdViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAdvertisementBinding binding = ItemAdvertisementBinding.inflate(
                LayoutInflater.from(context), parent, false);
        return new AdViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdViewHolder holder, int position) {
        Advertisement ad = advertisements.get(position);
        holder.bind(ad);
    }

    @Override
    public int getItemCount() {
        return advertisements.size();
    }

    @Override
    public void onViewRecycled(@NonNull AdViewHolder holder) {
        super.onViewRecycled(holder);
        holder.releasePlayer();
    }

    public class AdViewHolder extends RecyclerView.ViewHolder {
        private final ItemAdvertisementBinding binding;

        public AdViewHolder(@NonNull ItemAdvertisementBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Advertisement ad) {
            // Set basic information
            binding.adTitle.setText(ad.getTitle());
            binding.adDescription.setText(ad.getDescription());
            binding.adCategory.setText(ad.getCategory());
            binding.adCost.setText(ad.getReceiveCost() + " credits");
            
            // Set advertiser information
            if (ad.getAdvertiser() != null) {
                binding.advertiserName.setText(ad.getAdvertiser().getName());
                if (ad.getAdvertiser().getLogo() != null) {
                    Glide.with(context)
                            .load(ad.getAdvertiser().getLogo())
                            .into(binding.advertiserLogo);
                }
            }

            // Handle media content (video or images)
            setupMediaContent(ad);

            // Set up click listeners
            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAdClick(ad);
                }
            });

            binding.btnReceive.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAdReceive(ad);
                }
            });

            binding.btnClick.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAdClickInteraction(ad);
                }
            });

            binding.btnVisitStore.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onStoreClick(ad);
                }
            });

            // Show/hide store button based on availability
            binding.btnVisitStore.setVisibility(
                (ad.getAdvertiser() != null && ad.getAdvertiser().getStoreId() != null) ? 
                View.VISIBLE : View.GONE
            );
        }

        private void setupMediaContent(Advertisement ad) {
            if (ad.hasVideoContent()) {
                setupVideoContent(ad);
            } else if (ad.hasImageContent()) {
                setupImageContent(ad);
            } else {
                // Show placeholder
                binding.mediaContainer.setVisibility(View.GONE);
                binding.imageAd.setVisibility(View.VISIBLE);
                binding.videoPlayer.setVisibility(View.GONE);
                binding.imageAd.setImageResource(android.R.drawable.ic_menu_gallery);
            }
        }

        private void setupVideoContent(Advertisement ad) {
            binding.mediaContainer.setVisibility(View.VISIBLE);
            binding.videoPlayer.setVisibility(View.VISIBLE);
            binding.imageAd.setVisibility(View.GONE);

            String videoUrl = ad.getFirstMediaUrl();
            if (videoUrl != null) {
                initializeExoPlayer(videoUrl);
            }
        }

        private void setupImageContent(Advertisement ad) {
            binding.mediaContainer.setVisibility(View.VISIBLE);
            binding.imageAd.setVisibility(View.VISIBLE);
            binding.videoPlayer.setVisibility(View.GONE);

            String imageUrl = ad.getFirstMediaUrl();
            if (imageUrl != null) {
                Glide.with(context)
                        .load(imageUrl)
                        .centerCrop()
                        .into(binding.imageAd);
            }
        }

        private void initializeExoPlayer(String videoUrl) {
            if (exoPlayer == null) {
                exoPlayer = new ExoPlayer.Builder(context).build();
                binding.videoPlayer.setPlayer(exoPlayer);
            }

            MediaItem mediaItem = MediaItem.fromUri(videoUrl);
            exoPlayer.setMediaItem(mediaItem);
            exoPlayer.prepare();
            exoPlayer.setPlayWhenReady(false); // Don't auto-play
        }

        public void releasePlayer() {
            if (exoPlayer != null) {
                exoPlayer.release();
                exoPlayer = null;
            }
        }
    }
}