package com.example.adsbubble.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.adsbubble.R
import com.example.adsbubble.data.db.Ad
import com.example.adsbubble.viewmodel.AdsViewModel

class AdsActivity : AppCompatActivity() {
    private val vm: AdsViewModel by viewModels()
    private lateinit var adapter: AdsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ads)
        adapter = AdsAdapter { ad ->
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://adx3.me/${ad.token}"))
            startActivity(intent)
        }
        val rv = findViewById<RecyclerView>(R.id.adsRecycler)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter

        vm.ads.observe(this) { list ->
            adapter.submitList(list)
        }

        val itemTouch = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder) = false
            override fun onSwiped(vh: RecyclerView.ViewHolder, dir: Int) {
                val pos = vh.adapterPosition
                val ad = adapter.currentList[pos]
                if (dir == ItemTouchHelper.LEFT) {
                    // bookmark toggle
                    ad.copy(bookmarked = true).also { vm.deleteToken(it.token); } // simplistic: remove for demo
                } else {
                    vm.deleteToken(ad.token)
                }
                adapter.notifyItemRemoved(pos)
            }
        }
        ItemTouchHelper(itemTouch).attachToRecyclerView(rv)
    }
}
