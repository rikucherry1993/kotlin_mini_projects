package com.rikucherry.happyplaces.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rikucherry.happyplaces.R
import kotlinx.android.synthetic.main.activity_map.*

class MapActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        setSupportActionBar(toolbar_map)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar_map.setNavigationOnClickListener {
            setResult(RESULT_CANCELED)
            onBackPressed()
        }

    }
}