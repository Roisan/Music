package com.example.music.fragments


import android.app.Activity
import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import com.example.music.CurrentSongHelper
import com.example.music.R
import com.example.music.Songs
import java.lang.Exception
import kotlin.random.Random


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class SongPlayingFragment : Fragment() {

    var mediaplayer: MediaPlayer?= null
    var myActivity: Activity?= null
    var startTimeText: TextView?= null
    var endTimeText: TextView?= null
    var playPauseImageButton: ImageButton?= null
    var previousImageButton: ImageButton?= null
    var nextImageButton: ImageButton?= null
    var loopImageButton: ImageButton?= null
    var seekbar: SeekBar?= null
    var songArtistView: TextView?= null
    var songTitleView: TextView?= null
    var shuffleImageButton: ImageButton?= null
    var currentPosition: Int = 0
    var fetchSongs: ArrayList<Songs>?= null
    var currentSongHelper: CurrentSongHelper?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_song_playing, container, false)
        seekbar = view?.findViewById(R.id.seekBar)
        startTimeText = view?.findViewById(R.id.startTime)
        endTimeText = view?.findViewById(R.id.endTime)
        playPauseImageButton = view?.findViewById(R.id.playPauseButton)
        nextImageButton = view?.findViewById(R.id.nextButton)
        loopImageButton = view?.findViewById(R.id.loopButton)
        shuffleImageButton = view?.findViewById(R.id.shuffleButton)
        songArtistView = view?.findViewById(R.id.songArtist)
        songTitleView = view?.findViewById(R.id.songTitle)


        return view
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        myActivity = context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        myActivity = activity
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        currentSongHelper = CurrentSongHelper()
        currentSongHelper?.isPlaying = true
        currentSongHelper?.isLoop = false
        currentSongHelper?.isShuffle = false

        var path: String? = null
        var _songTitle: String? = null
        var _songArtist: String? = null
        var songId: Long = 0
        try {
            path = arguments!!.getString("path")
            _songArtist =  arguments!!.getString("songTitle")
            _songTitle = arguments!!.getString("songArtist")
            songId = arguments!!.getInt("SongId")!!.toLong()
            currentPosition = arguments!!.getInt("songPosition")
            fetchSongs = arguments!!.getParcelableArrayList("songData")

            currentSongHelper?.songPath = path
            currentSongHelper?.songTitle = _songTitle
            currentSongHelper?.songArtist = _songArtist
            currentSongHelper?.songId = songId
            currentSongHelper?.currentPosition = currentPosition

        }catch (e: Exception){
            e.printStackTrace()
        }
        mediaplayer = MediaPlayer()
        mediaplayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
        try {
            mediaplayer?.setDataSource(myActivity, Uri.parse(path))
            mediaplayer?.prepare()
        }catch (e:Exception){
            e.printStackTrace()
        }
        mediaplayer?.start()
        if (currentSongHelper?.isPlaying as Boolean){
            playPauseImageButton?.setBackgroundResource(R.drawable.pause2)
        }
        else{
            playPauseImageButton?.setBackgroundResource(R.drawable.play2)
        }
    }

    fun clickHandler(){
        shuffleImageButton?.setOnClickListener({

        })
        nextImageButton?.setOnClickListener({

        })
        previousImageButton?.setOnClickListener({

        })
        loopImageButton?.setOnClickListener({

        })
        playPauseImageButton?.setOnClickListener({
            if (mediaplayer?.isPlaying as Boolean){
                mediaplayer?.pause()
                currentSongHelper?.isPlaying = false
                playPauseImageButton?.setBackgroundResource(R.drawable.play2)
            }else{
                mediaplayer?.start()
                currentSongHelper?.isPlaying = true
                playPauseImageButton?.setBackgroundResource(R.drawable.pause2)
            }
        })
    }

    fun playNext(check: String){
        if (check.equals("PlayNextNormal",true)){
            currentPosition = currentPosition+1
        }else if (check.equals("PlayNextLikeNormalShuffle",true)){
            var randomObject = java.util.Random()
            var randomPosition = randomObject.nextInt(fetchSongs?.size?.plus(1) as Int)
            currentPosition = randomPosition

        }
        if (currentPosition == fetchSongs?.size)
            currentPosition = 0

        var nextSong = fetchSongs?.get(currentPosition)
        currentSongHelper?.songTitle = nextSong?.songTitle
        currentSongHelper?.songPath = nextSong?.songData
        currentSongHelper?.currentPosition = currentPosition
        currentSongHelper?.songId = nextSong?.songID as Long
        mediaplayer?.reset()
        try {
            mediaplayer?.setDataSource(myActivity, Uri.parse(currentSongHelper?.songPath))
            mediaplayer?.prepare()
            mediaplayer?.start()
        }catch (e: Exception){
            e.printStackTrace()
        }
    }
}
