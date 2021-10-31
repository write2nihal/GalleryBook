package com.gallerybook.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.gallerybook.room.GalleryDatabase
import com.gallerybook.room.GalleryScope
import com.gallerybook.BaseActivity
import com.gallerybook.R
import com.gallerybook.databinding.SplashActivityBinding

import com.gallerybook.ui.dashboard.DashBoardActivity
import com.gallerybook.ui.login.LoginActivity
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

/**
 * Created by Nihal Srivastava on 30/10/21.
 */
class SplashActivity : BaseActivity(), CoroutineScope {
    private lateinit var dbSize: List<GalleryScope>
    private val splashTime = 4000
    private lateinit var splashBinding: SplashActivityBinding
    private lateinit var database: GalleryDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        splashBinding = DataBindingUtil.setContentView(this, R.layout.splash_activity)
        database = GalleryDatabase.initACDatabase(this)
        Glide.with(this).load(R.raw.butterfly).into(splashBinding.ivSplash)
        readDatabase()
        launchActivity()
    }

    private fun readDatabase() {
        async {
            dbSize = database.galleryDao().isThereAnyData()
        }
    }

    private fun launchActivity() {
        Handler(Looper.myLooper()!!).postDelayed({
            val mainIntent: Intent = if (dbSize.isNotEmpty()) {
                Intent(this@SplashActivity, DashBoardActivity::class.java)
            } else {
                Intent(this@SplashActivity, LoginActivity::class.java)
            }
            this@SplashActivity.startActivity(mainIntent)
            this@SplashActivity.finish()

        }, splashTime.toLong())
    }

    override val coroutineContext: CoroutineContext =
        Dispatchers.IO + SupervisorJob()

    override fun onDestroy() {
        super.onDestroy()
        coroutineContext[Job]!!.cancel()
    }
}
