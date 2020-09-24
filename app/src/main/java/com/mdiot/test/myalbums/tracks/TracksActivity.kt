package com.mdiot.test.myalbums.tracks

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.mdiot.test.myalbums.R
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main activity for the application.
 */
@AndroidEntryPoint
class TracksActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tracks_activity)
        setSupportActionBar(findViewById(R.id.toolbar))

        val navController: NavController = findNavController(R.id.nav_host_fragment)
        appBarConfiguration = AppBarConfiguration.Builder(R.id.tracks_fragment_dest).build()
        setupActionBarWithNavController(navController, appBarConfiguration)
    }
}