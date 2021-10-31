package com.gallerybook.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingData
import androidx.recyclerview.widget.GridLayoutManager
import com.gallerybook.BaseActivity
import com.gallerybook.R
import com.gallerybook.broadcast.NetworkStateReceiverListener
import com.gallerybook.databinding.ActivityDashBoardBinding
import com.gallerybook.interfaces.OnImageClickListener
import com.gallerybook.room.GalleryDatabase
import com.gallerybook.room.GalleryScope
import com.gallerybook.ui.adapter.GalleryAdapter
import com.gallerybook.ui.details.ProductDetailsActivity
import com.gallerybook.utils.Constants
import com.gallerybook.utils.GalleryUtils
import com.gallerybook.viewmodel.GalleryViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.custom_toolbar.view.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect

/**
 * Created by Nihal Srivastava on 30/10/21.
 */
@AndroidEntryPoint
class DashBoardActivity : BaseActivity(), OnImageClickListener, NetworkStateReceiverListener {

    private lateinit var binding: ActivityDashBoardBinding
    private lateinit var galleryAdapter: GalleryAdapter
    private lateinit var database: GalleryDatabase
    private val viewModel: GalleryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        iv_toolbar_menu.visibility = View.GONE
        iv_popup_menu.visibility = View.GONE
        tv_title_toolbar.text = getString(R.string.app_name)
        database = GalleryDatabase.initACDatabase(this)
        setupRecyclerView()
    }


    private fun handleNetworkSwitch() {
        if (GalleryUtils.hasInternet(this)) {
            GalleryUtils.showLoader(this)
            loadData()
        } else {
            database.galleryDao().fetchStoredACDetails().observe(this, { dbList ->
                CoroutineScope(Dispatchers.Main).async {
                    val dataStore = mutableListOf<GalleryScope>()
                    dataStore.clear()
                    for (i in dbList.indices) {
                        if (dbList[i].image_path != "") {
                            dataStore.add(dbList[i])
                        }
                    }
                    galleryAdapter.submitData(PagingData.from(dataStore))
                }
            })
        }
    }

    private fun setupRecyclerView() {
        galleryAdapter = GalleryAdapter(this, this)
        binding.recyclerview.apply {
            adapter = galleryAdapter
            layoutManager = GridLayoutManager(this@DashBoardActivity, 2)
            setHasFixedSize(true)
        }
    }

    private fun loadData() {
        lifecycleScope.launch {
            viewModel.listData.collect {
                galleryAdapter.submitData(it)
                hideProgress()
            }
        }
        GalleryUtils.hideLoader()
    }

    private fun hideProgress() {
        GalleryUtils.hideLoader()
    }

    override fun onItemClick(item: GalleryScope) {
        val intent = Intent(this, ProductDetailsActivity::class.java)
        val gson = Gson()
        val json = gson.toJson(item)
        intent.putExtra(Constants.MODEL_VALUE, json)
        startActivity(intent)
    }

    override fun networkAvailable() {
        CoroutineScope(Dispatchers.Main).async {
            delay(1000)
            handleNetworkSwitch()
        }
        GalleryUtils.showSnackBar(binding.dashboard, "You became online !!")
    }

    override fun networkUnavailable() {
        CoroutineScope(Dispatchers.Main).async {
            delay(1000)
            handleNetworkSwitch()
        }
        GalleryUtils.showSnackBar(binding.dashboard, "You went offline !!")
    }
}
