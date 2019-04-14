package com.example.music.fragments


import android.app.Activity
import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import com.cleveroad.audiovisualization.AudioVisualization
import com.cleveroad.audiovisualization.DbmHandler
import com.cleveroad.audiovisualization.GLAudioVisualizationView
import com.example.music.CurrentSongHelper
import com.example.music.R
import com.example.music.Songs
import com.example.music.databases.MusicDatabase
import kotlinx.android.synthetic.*
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

    object Statified{
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

        var audioVisualization: AudioVisualization?= null
        var glView: GLAudioVisualizationView?= null

        var fab: ImageButton?= null

        var favouriteContent: MusicDatabase?= null

        var updateSongTime = object : Runnable{
            override fun run() {
                val getCurrent = Statified.mediaplayer?.currentPosition
                Statified.startTimeText?.setText(String.format("%d:%d",
                    TimeUnit.MILLISECONDS.toMinutes(getCurrent?.toLong() as Long),
                    (TimeUnit.MILLISECONDS.toSeconds(getCurrent?.toLong()) -
                            TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MILLISECONDS.toMinutes(getCurrent.toLong())))))

                Statified.seekbar?.setProgress(getCurrent?.toInt() as Int)
                Handler().postDelayed(this,1000)
            }
        }

    }



    object Staticated{
        var MY_PREFS_SHUFFLE = "Shuffle feature"
        var MY_PREFS_LOOP = "Loop feature"

        fun onSongComplete(){
            if(Statified.currentSongHelper?.isShuffle as Boolean){
                playNext("PlayNextLikeNormalShuffle")
                Statified.currentSongHelper?.isPlaying = true
            }else{
                if(Statified.currentSongHelper?.isLoop as Boolean){
                    Statified. currentSongHelper?.isPlaying = true
                    val nextSong = Statified.fetchSongs?.get(Statified.currentPosition)
                    Statified. currentSongHelper?.songTitle = nextSong?.songTitle
                    Statified.currentSongHelper?.songArtist = nextSong?.artist
                    Statified.currentSongHelper?.songPath = nextSong?.songData
                    Statified.currentSongHelper?.currentPosition = Statified.currentPosition
                    Statified.currentSongHelper?.songId = nextSong?.songID as Long

                    updateTextView(Statified.currentSongHelper?.songTitle as String, Statified.currentSongHelper?.songArtist as String)

                    Statified. mediaplayer?.reset()
                    try {
                        Statified. mediaplayer?.setDataSource(Statified.myActivity as Context, Uri.parse(Statified.currentSongHelper?.songPath))
                        Statified. mediaplayer?.prepare()
                        Statified.mediaplayer?.start()
                        processInformation(Statified.mediaplayer as MediaPlayer)
                    }catch (e: Exception){
                        e.printStackTrace()
                    }
                }else{
                    playNext("PlayNextNormal")
                    Statified.currentSongHelper?.isPlaying = true
                }
            }
        }

        fun updateTextView(songTitle: String, songArtist: String){
            Statified.songTitleView?.setText(songTitle)
            Statified.songArtistView?.setText(songArtist)
        }

        fun processInformation(mediaPlayer: MediaPlayer){
            val finalTime = Statified.mediaplayer!!.duration
            val startTime = Statified.mediaplayer!!.currentPosition
            Statified.seekbar?.max = finalTime
            Statified.startTimeText?.setText(String.format("%d: %d",
                TimeUnit.MILLISECONDS.toMinutes(startTime.toLong()),
                (TimeUnit.MILLISECONDS.toSeconds(startTime.toLong()) - TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MILLISECONDS.toMinutes(startTime.toLong())))))
            Statified.endTimeText?.setText(String.format("%d: %d",
                TimeUnit.MILLISECONDS.toMinutes(finalTime.toLong()),
                (TimeUnit.MILLISECONDS.toSeconds(finalTime.toLong()) - TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MILLISECONDS.toMinutes(finalTime.toLong())))))
            Statified. seekbar?.setProgress(startTime)
            Handler().postDelayed(Statified.updateSongTime, 1000)
        }

        fun playNext(check: String){
            if (check.equals("PlayNextNormal",true)){
                Statified.currentPosition = Statified.currentPosition + 1
            }else if (check.equals("PlayNextLikeNormalShuffle",true)){
                var randomObject = Random
                var randomPosition = randomObject.nextInt(Statified.fetchSongs?.size?.plus(1) as Int)
                Statified. currentPosition = randomPosition

            }
            if (Statified.currentPosition == Statified.fetchSongs?.size)
                Statified.currentPosition = 0

            Statified. currentSongHelper?.isLoop = false
            val nextSong = Statified.fetchSongs?.get(Statified.currentPosition)
            Statified. currentSongHelper?.songTitle = nextSong?.songTitle
            Statified.currentSongHelper?.songArtist = nextSong?.artist
            Statified. currentSongHelper?.songPath = nextSong?.songData
            Statified.currentSongHelper?.currentPosition = Statified.currentPosition
            Statified.currentSongHelper?.songId = nextSong?.songID as Long

            updateTextView(Statified.currentSongHelper?.songTitle as String,Statified. currentSongHelper?.songArtist as String)
            Statified. mediaplayer?.reset()
            try {
                Statified. mediaplayer?.setDataSource(Statified.myActivity as Context, Uri.parse(Statified.currentSongHelper?.songPath))
                Statified.mediaplayer?.prepare()
                Statified.mediaplayer?.start()
                processInformation(Statified.mediaplayer as MediaPlayer)
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_song_playing, container, false)
        Statified. seekbar = view?.findViewById(R.id.seekBar)
        Statified. startTimeText = view?.findViewById(R.id.startTime)
        Statified.endTimeText = view?.findViewById(R.id.endTime)
        Statified. playPauseImageButton = view?.findViewById(R.id.playPauseButton)
        Statified. nextImageButton = view?.findViewById(R.id.nextButton)
        Statified. previousImageButton = view?.findViewById(R.id.previousButton)
        Statified.loopImageButton = view?.findViewById(R.id.loopButton)
        Statified.shuffleImageButton = view?.findViewById(R.id.shuffleButton)
        Statified. songArtistView = view?.findViewById(R.id.songArtist)
        Statified.songTitleView = view?.findViewById(R.id.songTitle)
        Statified.glView = view?.findViewById(R.id.visualizer_view)
        Statified. fab = view?.findViewById(R.id.favouriteIcon)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Statified.audioVisualization = Statified.glView as AudioVisualization
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        Statified. myActivity = context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        Statified.myActivity = activity
    }

    override fun onResume() {
        super.onResume()
        Statified.audioVisualization?.onResume()
    }

    override fun onPause() {
        super.onPause()
        Statified.audioVisualization?.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Statified.audioVisualization?.release()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        Statified.favouriteContent = MusicDatabase(Statified.myActivity)
        Statified.currentSongHelper = CurrentSongHelper()
        Statified.mediaplayer?.pause()
        Statified.currentSongHelper?.isPlaying = true
        Statified.currentSongHelper?.isLoop = false
        Statified.currentSongHelper?.isShuffle = false

        var path: String? = null
        var _songTitle: String? = null
        var _songArtist: String? = null
        var songId: Long = 0
        try {
            path = arguments!!.getString("path")
            _songArtist =  arguments!!.getString("songArtist")
            _songTitle = arguments!!.getString("songTitle")
            songId = arguments!!.getInt("SongId").toLong()
            Statified.currentPosition = arguments!!.getInt("songPosition")
            Statified.fetchSongs = arguments!!.getParcelableArrayList("songData")

            Statified. currentSongHelper?.songPath = path
            Statified.currentSongHelper?.songTitle = _songTitle
            Statified.currentSongHelper?.songArtist = _songArtist
            Statified.currentSongHelper?.songId = songId
            Statified.currentSongHelper?.currentPosition = Statified.currentPosition

            Staticated.updateTextView(Statified.currentSongHelper?.songTitle as String, Statified.currentSongHelper?.songArtist as String)

        }catch (e: Exception){
            e.printStackTrace()
        }

        var fromFavBottomBar = arguments?.get("FavBottomBar") as? String
        if (fromFavBottomBar != null){
            Statified.mediaplayer = FavouriteFragment.Statified.mediaPlayer
        }else {

            Statified.mediaplayer = MediaPlayer()
            Statified.mediaplayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
            try {
                Statified.mediaplayer?.setDataSource(Statified.myActivity as Context, Uri.parse(path))
                Statified.mediaplayer?.prepare()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            Statified.mediaplayer?.start()
        }
        Staticated.processInformation(Statified.mediaplayer as MediaPlayer)

        if (Statified.currentSongHelper?.isPlaying as Boolean){
            Statified.playPauseImageButton?.setBackgroundResource(R.drawable.pause1)
        }
        else{
            Statified.playPauseImageButton?.setBackgroundResource(R.drawable.play1)
        }

        Statified.mediaplayer?.setOnCompletionListener {
            Staticated.onSongComplete()
        }
        clickHandler()

        var visualizationHandler = DbmHandler.Factory.newVisualizerHandler(Statified.myActivity as Context, 0)
        Statified.audioVisualization?.linkTo(visualizationHandler)

        var prefsForShuffle = Statified.myActivity?.getSharedPreferences(Staticated.MY_PREFS_SHUFFLE, Context.MODE_PRIVATE)
        var isShuffleAllowed = prefsForShuffle?.getBoolean("feature",false)
        if (isShuffleAllowed as Boolean){
            Statified.currentSongHelper?.isShuffle = true
            Statified.currentSongHelper?.isLoop = false
            Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shufflegrey)
            Statified.loopImageButton?.setBackgroundResource(R.drawable.loop)
        }else{
            Statified.currentSongHelper?.isShuffle = false
            Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle)
        }

        var prefsForLoop = Statified.myActivity?.getSharedPreferences(Staticated.MY_PREFS_LOOP, Context.MODE_PRIVATE)
        var isLoopAllowed = prefsForLoop?.getBoolean("feature",false)
        if (isLoopAllowed as Boolean){
            Statified.currentSongHelper?.isLoop = true
            Statified.currentSongHelper?.isShuffle = false
            Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle)
            Statified.loopImageButton?.setBackgroundResource(R.drawable.loopgrey)
        }else{
            Statified.currentSongHelper?.isLoop = false
            Statified.loopImageButton?.setBackgroundResource(R.drawable.loop)
        }


        if (Statified.favouriteContent?.checkifIDExists(Statified.currentSongHelper?.songId?.toInt() as Int) as Boolean){
            Statified.fab?.setImageDrawable(ContextCompat.getDrawable(Statified.myActivity?: return,R.drawable.heart))
            Statified.favouriteContent?.deleteFavourite(Statified.currentSongHelper?.songId?.toInt() as Int)
            Toast.makeText(Statified.myActivity,"Removed from favourites",Toast.LENGTH_SHORT)
        }else {
            Statified.fab?.setImageDrawable(ContextCompat.getDrawable(Statified.myActivity?: return, R.drawable.blackheart))
            Statified.favouriteContent?.storesAsFavourite(
                Statified.currentSongHelper?.songId?.toInt() as Int, Statified.currentSongHelper?.songArtist,
                Statified.currentSongHelper?.songTitle, Statified.currentSongHelper?.songPath
            )
            Toast.makeText(Statified.myActivity, "Added to Favourites", Toast.LENGTH_SHORT)
        }

    }

    fun clickHandler(){
        Statified.shuffleImageButton?.setOnClickListener({
            var editorShuffle = Statified.myActivity?.getSharedPreferences(Staticated.MY_PREFS_SHUFFLE, Context.MODE_PRIVATE)?.edit()
            var editorLoop = Statified.myActivity?.getSharedPreferences(Staticated.MY_PREFS_LOOP, Context.MODE_PRIVATE)?.edit()
            if (Statified.currentSongHelper?.isShuffle as Boolean){
                Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shufflegrey)
                Statified.currentSongHelper?.isShuffle = false
                editorShuffle?.putBoolean("feature",false)
                editorShuffle?.apply()
            }else{
                Statified. currentSongHelper?.isShuffle = true
                Statified.currentSongHelper?.isLoop = false
                Statified.shuffleImageButton?.setBackgroundResource((R.drawable.shuffle))
                Statified. loopImageButton?.setBackgroundResource(R.drawable.loopgrey)
                editorShuffle?.putBoolean("feature",true)
                editorShuffle?.apply()
                editorLoop?.putBoolean("feature",false)
                editorLoop?.apply()
            }
        })

        Statified.nextImageButton?.setOnClickListener({
            Statified. currentSongHelper?.isPlaying = true
            if(Statified.currentSongHelper?.isShuffle as Boolean){
                Staticated.playNext("PlayNextLikeNormalShuffle")
            }else{
                Staticated.playNext("PlayNextNormal")
            }
        })

        Statified. previousImageButton?.setOnClickListener({
            Statified. currentSongHelper?.isPlaying = true
            if(Statified.currentSongHelper?.isLoop as Boolean){
                Statified.loopImageButton?.setBackgroundResource(R.drawable.loopgrey)
            }
            playPrevious()
        })

        Statified.loopImageButton?.setOnClickListener({
            var editorShuffle = Statified.myActivity?.getSharedPreferences(Staticated.MY_PREFS_SHUFFLE, Context.MODE_PRIVATE)?.edit()
            var editorLoop =Statified. myActivity?.getSharedPreferences(Staticated.MY_PREFS_LOOP, Context.MODE_PRIVATE)?.edit()

            if(Statified.currentSongHelper?.isLoop as Boolean){
                Statified. currentSongHelper?.isLoop = false
                Statified.loopImageButton?.setBackgroundResource(R.drawable.loopgrey)
                editorLoop?.putBoolean("feature",false)
                editorLoop?.apply()
            }else{
                Statified.currentSongHelper?.isLoop = true
                Statified.currentSongHelper?.isShuffle = false
                Statified.loopImageButton?.setBackgroundResource(R.drawable.loop)
                Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shufflegrey)
                editorShuffle?.putBoolean("feature",false)
                editorShuffle?.apply()
                editorLoop?.putBoolean("feature",true)
                editorLoop?.apply()
            }
        })

        Statified. playPauseImageButton?.setOnClickListener({
            if (Statified.mediaplayer?.isPlaying as Boolean){
                Statified.mediaplayer?.pause()
                Statified.currentSongHelper?.isPlaying = false
                Statified. playPauseImageButton?.setBackgroundResource(R.drawable.play1)
            }else{
                Statified. mediaplayer?.start()
                Statified. currentSongHelper?.isPlaying = true
                Statified. playPauseImageButton?.setBackgroundResource(R.drawable.pause1)
            }
        })
    }



    fun playPrevious(){
        Statified.currentPosition = Statified.currentPosition - 1
        if(Statified.currentPosition == -1){
            Statified.currentPosition = 0
        }
        if(Statified.currentSongHelper?.isPlaying as Boolean){
            Statified.playPauseImageButton?.setBackgroundResource(R.drawable.pause1)
        }else{
            Statified.playPauseImageButton?.setBackgroundResource((R.drawable.play1))
        }

        Statified.currentSongHelper?.isLoop = false
        val nextSong = Statified.fetchSongs?.get(Statified.currentPosition)
        Statified.currentSongHelper?.songTitle = nextSong?.songTitle
        Statified.currentSongHelper?.songArtist = nextSong?.artist
        Statified.currentSongHelper?.songPath = nextSong?.songData
        Statified.currentSongHelper?.currentPosition = Statified.currentPosition
        Statified.currentSongHelper?.songId = nextSong?.songID as Long

        Staticated.updateTextView(Statified.currentSongHelper?.songTitle as String, Statified.currentSongHelper?.songArtist as String)

        Statified.mediaplayer?.reset()
        try {
            Statified.mediaplayer?.setDataSource(activity as Context, Uri.parse(Statified.currentSongHelper?.songPath))
            Statified.mediaplayer?.prepare()
            Statified.mediaplayer?.start()
            Staticated.processInformation(Statified.mediaplayer as MediaPlayer)
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

}
