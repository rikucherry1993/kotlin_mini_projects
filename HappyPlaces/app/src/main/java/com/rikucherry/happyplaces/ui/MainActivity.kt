package com.rikucherry.happyplaces.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.rikucherry.happyplaces.R
import com.rikucherry.happyplaces.data.Databasehandler
import com.rikucherry.happyplaces.model.HappyPlaceModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        const val REQUEST_FROM_MAIN_TO_ADD = 999
        const val REQUEST_FROM_MAIN_TO_DETAIL = 998
        const val EXTRA_PLACE_DETAILS = "EXTRA_PLACE_DETAILS"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fabAddHappyPlace.setOnClickListener {
            startActivityForResult(Intent(this, AddHappyPlaceActivity::class.java), REQUEST_FROM_MAIN_TO_ADD)
        }

        getHappyPlaces()
        TODO("Add swipe features")
    }

    private fun getHappyPlaces(){
        val dbHandler = Databasehandler(this)
        val mList = dbHandler.getHappyPlacesList()

        if (mList.size > 0) {
            rv_happy_list.visibility = View.VISIBLE
            tv_empty.visibility = View.GONE
            rv_happy_list.layoutManager = LinearLayoutManager(this)
            rv_happy_list.setHasFixedSize(true)
            val adapter = HappyPlacesAdapter(this, mList, itemClickCallback)
            rv_happy_list.adapter = adapter
        } else {
            rv_happy_list.visibility = View.GONE
            tv_empty.visibility = View.VISIBLE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_FROM_MAIN_TO_ADD && resultCode == RESULT_OK) {
            getHappyPlaces()
        } else if (requestCode == REQUEST_FROM_MAIN_TO_DETAIL && resultCode == RESULT_OK) {

        }

    }

    private val itemClickCallback = object: HappyPlacesAdapter.OnItemClickListener{
        override fun onClickItem(position: Int, model: HappyPlaceModel) {
            val intent = Intent(this@MainActivity, HappyPlaceDetailActivity::class.java)
            intent.putExtra(EXTRA_PLACE_DETAILS, model)
            startActivityForResult(intent, REQUEST_FROM_MAIN_TO_DETAIL)
        }

    }

}