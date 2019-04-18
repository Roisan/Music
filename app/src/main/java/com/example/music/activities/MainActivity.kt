package com.example.music.activities

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import com.example.music.R
import com.example.music.adapters.NavigationDrawerAdapter
import com.example.music.fragments.MainScreenFragment
import com.example.music.fragments.SongPlayingFragment
import java.lang.Exception

class MainActivity : AppCompatActivity(){

    var navigationDrawerIconsList: ArrayList<String> = arrayListOf()
    var images_for_navdrawer = intArrayOf(R.drawable.music,R.drawable.favourites,R.drawable.settings,
        R.drawable.about)
    var trackNotificationBuilder: Notification?= null

    object Statified{
        var drawerLayout: DrawerLayout?=null
        var notificationManager: NotificationManager?= null

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        MainActivity.Statified.drawerLayout = findViewById(R.id.drawer_layout)

        navigationDrawerIconsList.add("All Songs")
        navigationDrawerIconsList.add("Favourites")
        navigationDrawerIconsList.add("Settings")
        navigationDrawerIconsList.add("About Us")

        val toggle = ActionBarDrawerToggle(this@MainActivity,  MainActivity.Statified.drawerLayout,toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        MainActivity.Statified.drawerLayout?.setDrawerListener(toggle)
        toggle.syncState()

        val mainScreenFragment = MainScreenFragment()
        this.supportFragmentManager
            .beginTransaction()
            .add(R.id.details_fragment, mainScreenFragment, "MainScreenFragment")
            .commit()

        var _navigationAdapter = NavigationDrawerAdapter(navigationDrawerIconsList, images_for_navdrawer,
            this)
        _navigationAdapter.notifyDataSetChanged()

        var navigation_recycler_view = findViewById<RecyclerView>(R.id.navigation_recycler_view)
        navigation_recycler_view.layoutManager = LinearLayoutManager(this)
        navigation_recycler_view.itemAnimator = DefaultItemAnimator()
        navigation_recycler_view.adapter = _navigationAdapter
        navigation_recycler_view.setHasFixedSize(true)

        val intent = Intent(this@MainActivity, MainActivity::class.java)
        val pIntent = PendingIntent.getActivity(this@MainActivity, System.currentTimeMillis().toInt(),
            intent,0)
        trackNotificationBuilder = Notification.Builder(this)
            .setContentTitle("Track Playing in Background")
            .setSmallIcon(R.drawable.music1)
            .setContentIntent(pIntent)
            .setOngoing(true)
            .setAutoCancel(true)
            .build()

        Statified.notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


    }

    override fun onStart() {
        super.onStart()
        try {
            Statified.notificationManager?.cancel(1919)
        }
        catch (e:Exception){
            e.printStackTrace()
        }
    }

    override fun onStop() {
        super.onStop()
        try {
            if(SongPlayingFragment.Statified.mediaplayer?.isPlaying as Boolean){
                Statified.notificationManager?.notify(1919,trackNotificationBuilder)
            }
        }
        catch (e:Exception){
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            Statified.notificationManager?.cancel(1919)
        }
        catch (e:Exception){
            e.printStackTrace()
        }
    }
    }
