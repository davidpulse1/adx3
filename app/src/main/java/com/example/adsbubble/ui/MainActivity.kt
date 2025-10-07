package com.example.adsbubble.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.adsbubble.R
import com.example.adsbubble.service.BubbleService

class MainActivity : AppCompatActivity() {
    private val requestOverlay = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { }
    private val requestPermissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { map ->
        // no-op, user handles
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.startBubbleBtn).setOnClickListener {
            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
                requestOverlay.launch(intent)
            } else {
                startService(Intent(this, BubbleService::class.java))
            }
        }

        findViewById<Button>(R.id.openAdsBtn).setOnClickListener {
            startActivity(Intent(this, AdsActivity::class.java))
        }

        // Request perms
        val perms = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
        ActivityCompat.requestPermissions(this, perms, 101)
    }
}
