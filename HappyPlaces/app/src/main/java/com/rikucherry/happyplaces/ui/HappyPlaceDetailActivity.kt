package com.rikucherry.happyplaces.ui

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rikucherry.happyplaces.R
import com.rikucherry.happyplaces.model.HappyPlaceModel
import com.rikucherry.happyplaces.ui.MainActivity.Companion.EXTRA_PLACE_DETAILS
import kotlinx.android.synthetic.main.activity_happy_place_detail.*

class HappyPlaceDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_happy_place_detail)
        setSupportActionBar(toolbar_happy_place_detail)

        var model : HappyPlaceModel? = null
        if (intent.hasExtra(EXTRA_PLACE_DETAILS)) {
            model = intent.getSerializableExtra(EXTRA_PLACE_DETAILS) as HappyPlaceModel
        }

        if (model != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.title = model.title

            toolbar_happy_place_detail.setNavigationOnClickListener {
                setResult(RESULT_CANCELED)
                onBackPressed()
            }

            iv_place_image_detail.setImageURI(Uri.parse(model.image))
            tv_description.text = model.description
            tv_location.text = model.location

        } else {

        }



    }
}