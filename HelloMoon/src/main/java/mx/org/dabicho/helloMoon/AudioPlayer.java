package mx.org.dabicho.helloMoon;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

/**
 * Created by dabicho on 10/3/14.
 */
public class AudioPlayer {
    private static final String TAG="AudioPlayer";
    private MediaPlayer mMediaPlayer;
    private MediaPlayer.OnCompletionListener mOnCompletionListener;

    public void setOnCompletionListener(MediaPlayer.OnCompletionListener onCompletionListener){
        mOnCompletionListener=onCompletionListener;
    }

    public void stop(){
        if(mMediaPlayer!=null){
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer=null;
        }
    }

    /**
     *
     * @param c contexto de la actividad
     * @return true si está reproduciendo
     */
    public boolean play(Context c ){

        if(mMediaPlayer==null) {
            mMediaPlayer = MediaPlayer.create(c, R.raw.one_small_step);

            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stop();
                    if(mOnCompletionListener!=null)
                        mOnCompletionListener.onCompletion(mMediaPlayer);
                }

            });

        }

        if (mMediaPlayer.isPlaying()){
            mMediaPlayer.pause();
            Log.d(TAG, "play pause: " + mMediaPlayer.isPlaying());
            return mMediaPlayer.isPlaying();
        }

            mMediaPlayer.start();
            Log.d(TAG, "play play: "+mMediaPlayer.isPlaying());
        return mMediaPlayer.isPlaying();
    }

    /**
     *
     * @return true if it is currently playing music
     */
    public boolean isPlaying(){
        if(mMediaPlayer==null)
            return false;
        return mMediaPlayer.isPlaying();
    }


}
