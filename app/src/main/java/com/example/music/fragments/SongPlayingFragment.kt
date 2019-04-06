package com.example.music.fragments


import android.app.Activity
import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
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
import java.sql.Time
import java.util.concurrent.TimeUnit
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

    var updateSongTime = object : Runnable{
        override fun run() {
           val getCurrent = mediaplayer?.currentPosition
            startTimeText?.setText(String.format("%d:%d",
                TimeUnit.MILLISECONDS.toMinutes(getCurrent?.toLong() as Long),
                TimeUnit.MILLISECONDS.toSeconds(getCurrent?.toLong() as Long) -
                TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MILLISECONDS.toMinutes(getCurrent.toLong()))))

            seekbar?.setProgress(getCurrent?.toInt() as Int)
            Handler().postDelayed(this,1000)
        }


    }

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
            songId = arguments!!.getInt("SongId").toLong()
            currentPosition = arguments!!.getInt("songPosition")
            fetchSongs = arguments!!.getParcelableArrayList("songData")

            currentSongHelper?.songPath = path
            currentSongHelper?.songTitle = _songTitle
            currentSongHelper?.songArtist = _songArtist
            currentSongHelper?.songId = songId
            currentSongHelper?.currentPosition = currentPosition

            updateTextView(currentSongHelper?.songTitle as String, currentSongHelper?.songArtist as String)

        }catch (e: Exception){
            e.printStackTrace()
        }
        mediaplayer = MediaPlayer()
        mediaplayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
        try {
            mediaplayer?.setDataSource(myActivity as Context, Uri.parse(path))
            mediaplayer?.prepare()
        }catch (e:Exception){
            e.printStackTrace()
        }
        mediaplayer?.start()

        processInformation(mediaplayer as MediaPlayer)

        if (currentSongHelper?.isPlaying as Boolean){
            playPauseImageButton?.setBackgroundResource(R.drawable.pause1)
        }
        else{
            playPauseImageButton?.setBackgroundResource(R.drawable.play1)
        }

        mediaplayer?.setOnCompletionListener {
            onSongComplete()
        }
        clickHandler()
    }

    fun clickHandler(){
        shuffleImageButton?.setOnClickListener({
            if (currentSongHelper?.isShuffle as Boolean){
                shuffleImageButton?.setBackgroundResource(R.drawable.shuffle)
                currentSongHelper?.isShuffle = false
            }else{
                currentSongHelper?.isShuffle = true
                currentSongHelper?.isLoop = false
                shuffleImageButton?.setBackgroundResource((R.drawable.shuffle))
                loopImageButton?.setBackgroundResource(R.drawable.loop)
            }
        })

        nextImageButton?.setOnClickListener({
            currentSongHelper?.isPlaying = true
            if(currentSongHelper?.isShuffle as Boolean){
                playNext("PlayNextLikeNormalShuffle")
            }else{
                playNext("PlayNextNormal")
            }
        })

        previousImageButton?.setOnClickListener({
            currentSongHelper?.isPlaying = true
            if(currentSongHelper?.isLoop as Boolean){
                loopImageButton?.setBackgroundResource(R.drawable.loop)
            }
            playPrevious()
        })

        loopImageButton?.setOnClickListener({
            if(currentSongHelper?.isLoop as Boolean){
                currentSongHelper?.isShuffle = false
                loopImageButton?.setBackgroundResource(R.drawable.loop)
            }else{
                currentSongHelper?.isLoop = true
                currentSongHelper?.isShuffle = false
                loopImageButton?.setBackgroundResource(R.drawable.loop)
                shuffleImageButton?.setBackgroundResource(R.drawable.loop)
            }
        })

        playPauseImageButton?.setOnClickListener({
            if (mediaplayer?.isPlaying as Boolean){
                mediaplayer?.pause()
                currentSongHelper?.isPlaying = false
                playPauseImageButton?.setBackgroundResource(R.drawable.play1)
            }else{
                mediaplayer?.start()
                currentSongHelper?.isPlaying = true
                playPauseImageButton?.setBackgroundResource(R.drawable.pause1)
            }
        })
    }

    fun playNext(check: String){
        if (check.equals("PlayNextNormal",true)){
            currentPosition = currentPosition + 1
        }else if (check.equals("PlayNextLikeNormalShuffle",true)){
            var randomObject = Random
            var randomPosition = randomObject.nextInt(fetchSongs?.size?.plus(1) as Int)
            currentPosition = randomPosition

        }
        if (currentPosition == fetchSongs?.size)
            currentPosition = 0

        currentSongHelper?.isLoop = false
        val nextSong = fetchSongs?.get(currentPosition)
        currentSongHelper?.songTitle = nextSong?.songTitle
        currentSongHelper?.songPath = nextSong?.songData
        currentSongHelper?.currentPosition = currentPosition
        currentSongHelper?.songId = nextSong?.songID as Long

        updateTextView(currentSongHelper?.songTitle as String, currentSongHelper?.songArtist as String)
        mediaplayer?.reset()
        try {
            mediaplayer?.setDataSource(myActivity as Context, Uri.parse(currentSongHelper?.songPath))
            mediaplayer?.prepare()
            mediaplayer?.start()
            processInformation(mediaplayer as MediaPlayer)
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    fun playPrevious(){
        currentPosition = currentPosition - 1
        if(currentPosition == -1){
            currentPosition = 0
        }
        if(currentSongHelper?.isPlaying as Boolean){
            playPauseImageButton?.setBackgroundResource(R.drawable.pause1)
        }else{
            playPauseImageButton?.setBackgroundResource((R.drawable.play1))
        }

        currentSongHelper?.isLoop = false
        val nextSong = fetchSongs?.get(currentPosition)
        currentSongHelper?.songTitle = nextSong?.songTitle
        currentSongHelper?.songPath = nextSong?.songData
        currentSongHelper?.currentPosition = currentPosition
        currentSongHelper?.songId = nextSong?.songID as Long

        updateTextView(currentSongHelper?.songTitle as String, currentSongHelper?.songArtist as String)

        mediaplayer?.reset()
        try {
            mediaplayer?.setDataSource(activity as Context, Uri.parse(currentSongHelper?.songPath))
            mediaplayer?.prepare()
            mediaplayer?.start()
            processInformation(mediaplayer as MediaPlayer)
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    fun onSongComplete(){
        if(currentSongHelper?.isShuffle as Boolean){
            playNext("PlayNextLikeNormalShuffle")
            currentSongHelper?.isPlaying = true
        }else{
            if(currentSongHelper?.isLoop as Boolean){
                currentSongHelper?.isPlaying = true
                val nextSong = fetchSongs?.get(currentPosition)
                currentSongHelper?.songTitle = nextSong?.songTitle
                currentSongHelper?.songPath = nextSong?.songData
                currentSongHelper?.currentPosition = currentPosition
                currentSongHelper?.songId = nextSong?.songID as Long

                updateTextView(currentSongHelper?.songTitle as String, currentSongHelper?.songArtist as String)

                mediaplayer?.reset()
                try {
                    mediaplayer?.setDataSource(myActivity as Context, Uri.parse(currentSongHelper?.songPath))
                    mediaplayer?.prepare()
                    mediaplayer?.start()
                    processInformation(mediaplayer as MediaPlayer)
                }catch (e: Exception){
                    e.printStackTrace()
                }
            }else{
                playNext("PlayNextNormal")
                currentSongHelper?.isPlaying = true
            }
        }
    }

    fun updateTextView(songTitle: String, songArtist: String){
        songTitleView?.setText(songTitle)
        songArtistView?.setText(songArtist)
    }

    fun processInformation(mediaPlayer: MediaPlayer){
        val finalTime = mediaplayer!!.duration
        val startTime = mediaplayer!!.currentPosition
        seekbar?.max = finalTime
        startTimeText?.setText(String.format("%d: %d",
            TimeUnit.MILLISECONDS.toMinutes(startTime.toLong()),
            TimeUnit.MILLISECONDS.toSeconds(startTime.toLong())- TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MILLISECONDS.toMinutes(startTime.toLong()))))
        endTimeText?.setText(String.format("%d: %d",
            TimeUnit.MILLISECONDS.toMinutes(finalTime.toLong()),
            TimeUnit.MILLISECONDS.toSeconds(finalTime.toLong())- TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MILLISECONDS.toMinutes(finalTime.toLong()))))
        seekbar?.setProgress(startTime)
        Handler().postDelayed(updateSongTime, 1000)
    }
}
