package com.example.music.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import com.example.music.R
import com.example.music.Songs
import org.w3c.dom.Text

class MainScreenAdapter(_songDetails: ArrayList<Songs>,_context: Context): RecyclerView.Adapter<MainScreenAdapter.MyViewHolder>(){


    var songDetails: ArrayList<Songs>?= null
    var mContext: Context?= null

    init {
        this.songDetails = _songDetails
        this.mContext = _context
    }

    override fun onBindViewHolder(p0: MyViewHolder, p1: Int) {
        val songObject = songDetails?.get(p1)
        p0.trackTitle?.text = songObject?.songTitle
        p0.trackArtist?.text = songObject?.artist
        p0.contentHolder?.setOnClickListener({
            Toast.makeText(mContext,"Hey " + songObject?.songTitle,Toast.LENGTH_SHORT).show()
        })

    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MyViewHolder {
        val itemView = LayoutInflater.from(p0.context)
            .inflate(R.layout.row_custom_mainscreen_adapter,p0,false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        if (songDetails == null)
            return 0;
        else{
            return (songDetails as ArrayList<Songs>).size
        }
    }

    class MyViewHolder(view: View): RecyclerView.ViewHolder(view){
        var trackTitle: TextView?= null
        var trackArtist:TextView?= null
        var contentHolder:RelativeLayout?= null

        init{
            trackTitle = view.findViewById<TextView>(R.id.trackTitle)
            trackArtist = view.findViewById<TextView>(R.id.trackArtist)
            contentHolder = view.findViewById<RelativeLayout>(R.id.contentRow)
        }
    }
}