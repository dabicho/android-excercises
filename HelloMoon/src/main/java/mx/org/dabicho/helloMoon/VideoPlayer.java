package mx.org.dabicho.helloMoon;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.IOException;

/**
 * Created by dabicho on 10/3/14.
 */
public class VideoPlayer {
    private static final String TAG="VideoPlayer";
    private MediaPlayer mMediaPlayer;
    private MediaPlayer.OnCompletionListener mOnCompletionListener;
    private SurfaceHolder mSurfaceHolder;
    private MediaPlayer.OnVideoSizeChangedListener mOnVideoSizeChangedListener;


    public void setOnCompletionListener(MediaPlayer.OnCompletionListener onCompletionListener){
        mOnCompletionListener=onCompletionListener;
    }

    public void setOnVideoSizeChangedListener(MediaPlayer.OnVideoSizeChangedListener onVideoSizeChangedListener){
        mOnVideoSizeChangedListener=onVideoSizeChangedListener;
    }

    public void setSurfaceHolder(SurfaceHolder sh) {
        mSurfaceHolder = sh;



    }

    public void stop(){
        if(mMediaPlayer!=null){
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer=null;
        }
    }

    public boolean play(Context c ){

        if(mMediaPlayer==null) {
            mMediaPlayer = MediaPlayer.create(c, R.raw.apollo_17_stroll2);
            mMediaPlayer.setOnVideoSizeChangedListener(mOnVideoSizeChangedListener);
            mMediaPlayer.setDisplay(mSurfaceHolder);

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


    public boolean isPlaying() {
        if (mMediaPlayer==null)
            return false;
        return mMediaPlayer.isPlaying();
    }
}
