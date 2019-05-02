package com.tddevelopment.loitr.ui

import android.support.v7.app.AppCompatActivity
import com.tddevelopment.loitr.service.GeofenceApp

abstract class BaseActivity : AppCompatActivity() {
    fun getRepo() = (application as GeofenceApp).getRepo()
}