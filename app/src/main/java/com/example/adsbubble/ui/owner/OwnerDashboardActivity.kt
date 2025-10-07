package com.example.adsbubble.ui.owner

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.adsbubble.R

class OwnerDashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_owner_dashboard)
        findViewById<Button>(R.id.createAdBtn).setOnClickListener {
            startActivity(Intent(this, OwnerCreateAdActivity::class.java))
        }
    }
}
