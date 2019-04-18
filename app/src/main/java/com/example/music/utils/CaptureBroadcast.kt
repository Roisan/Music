package com.example.music.utils

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import com.example.music.R
import com.example.music.activities.MainActivity
import com.example.music.fragments.SongPlayingFragment
import java.lang.Exception

class CaptureBroadcast: BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_NEW_OUTGOING_CALL){
            try {
                MainActivity.Statified.notificationManager?.cancel(1919)
            }
            catch (e:Exception){
                e.printStackTrace()
            }
            try {
                if (SongPlayingFragment.Statified.mediaplayer?.isPlaying as Boolean){
                    SongPlayingFragment.Statified.mediaplayer?.pause()
                    SongPlayingFragment.Statified.playPauseImageButton?.setBackgroundResource(R.drawable.play1)
                }
            }
            catch (e: Exception){
                e.printStackTrace()
            }
        }else{
            val tm: TelephonyManager = context?.getSystemService(Service.TELEPHONY_SERVICE) as TelephonyManager
            when(tm?.callState){
                TelephonyManager.CALL_STATE_RINGING ->{
                    try {
                        MainActivity.Statified.notificationManager?.cancel(1919)
                    }
                    catch (e:Exception){
                        e.printStackTrace()
                    }
                    try {
                        if (SongPlayingFragment.Statified.mediaplayer?.isPlaying as Boolean){
                            SongPlayingFragment.Statified.mediaplayer?.pause()
                            SongPlayingFragment.Statified.playPauseImageButton?.setBackgroundResource(R.drawable.play1)
                        }
                    }
                    catch (e: Exception){
                        e.printStackTrace()
                    }
                }
            }
        }
    }

}