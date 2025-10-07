package com.example.adsbubble.ui.owner

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.adsbubble.R
import com.example.adsbubble.data.api.ApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OwnerLoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_owner_login)
        val email = findViewById<EditText>(R.id.email)
        val pass = findViewById<EditText>(R.id.password)
        findViewById<Button>(R.id.loginBtn).setOnClickListener {
            val em = email.text.toString(); val pw = pass.text.toString()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val res = ApiClient.retrofit.login(mapOf("email" to em, "password" to pw))
                    val token = res["token"] ?: ""
                    val prefs = getSharedPreferences("prefs", MODE_PRIVATE)
                    prefs.edit().putString("jwt_token", token).apply()
                    startActivity(Intent(this@OwnerLoginActivity, OwnerDashboardActivity::class.java))
                    finish()
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
        }
    }
}
