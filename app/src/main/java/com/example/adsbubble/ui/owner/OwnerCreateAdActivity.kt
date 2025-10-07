package com.example.adsbubble.ui.owner

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.adsbubble.R
import com.example.adsbubble.data.api.ApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class OwnerCreateAdActivity : AppCompatActivity() {
    private var mediaUri: Uri? = null
    private val REQ_PHOTO = 1001
    private val REQ_VIDEO = 1002

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_ad)
        val title = findViewById<EditText>(R.id.adTitle)
        findViewById<Button>(R.id.takePhotoBtn).setOnClickListener {
            val i = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(i, REQ_PHOTO)
        }
        findViewById<Button>(R.id.recordVideoBtn).setOnClickListener {
            val i = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
            startActivityForResult(i, REQ_VIDEO)
        }
        findViewById<Button>(R.id.uploadBtn).setOnClickListener {
            val jwt = getSharedPreferences("prefs", MODE_PRIVATE).getString("jwt_token", "") ?: ""
            val storeId = "store-default"
            val tpl = "simple_promo"
            val t = title.text.toString()
            if (mediaUri == null) return@setOnClickListener
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val file = FileUtils.getFileFromUri(this@OwnerCreateAdActivity, mediaUri!!)
                    val reqFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                    val part = MultipartBody.Part.createFormData("media", file.name, reqFile)
                    ApiClient.retrofit.uploadAd("Bearer $jwt", okhttp3.RequestBody.create(null, storeId), okhttp3.RequestBody.create(null, t), okhttp3.RequestBody.create(null, tpl), part)
                    finish()
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQ_PHOTO || requestCode == REQ_VIDEO) {
                mediaUri = data?.data
            }
        }
    }
}
