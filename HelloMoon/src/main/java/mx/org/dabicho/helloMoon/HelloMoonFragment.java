package mx.org.dabicho.helloMoon;

import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * HelloMoon Fragment hosting the SurfaceView for videoPlayback and play and stop buttons
 */
public class HelloMoonFragment extends Fragment {
    private static final String TAG = "HelloMoonFragment";
    private Button mPlayButton;

    private Button mStopButton;
    private SurfaceHolder mSurfaceHolder;
    private SurfaceView mSurfaceView;
    private VideoPlayer mPlayer = new VideoPlayer();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Debido a la rotación, se destruye la instancia de el reproductor
        // No es posible usando onSaveInstance por que el reproductor es destruido de igual forma
        // aunque conserve en una variable la posición
        // setRetainInstance permite que android conserve el fragmento sin destruirlo (junto con
        // sus miembros) por lo que no se detiene la reproducición
        // El fragment manager se encarga de devolver este mismo fragmento a su actividad
        Log.d(TAG,"onCreate");
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_hello_moon, container, false);
        //Este fragmento de código es para VideoPlayer

        mSurfaceView=((SurfaceView)
                fragmentView.findViewById(R.id.hellomoon_videoSurfaceView));
        mSurfaceHolder=((SurfaceView)
                fragmentView.findViewById(R.id.hellomoon_videoSurfaceView)).getHolder();

        mPlayer.setSurfaceHolder(mSurfaceHolder);
        mPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
            @Override
            public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                ViewGroup.LayoutParams lLayoutParams=mSurfaceView.getLayoutParams();
                lLayoutParams.height=height;
                lLayoutParams.width=width;

                mSurfaceView.setLayoutParams(lLayoutParams);
            }
        });
        mSurfaceView.setVisibility(View.VISIBLE);
        //Fin para video




        Log.d(TAG,"onCreateView Activity: "+getActivity());
        mPlayButton = (Button) fragmentView.findViewById(R.id.hellomoon_playButton);
        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"onCreateView Activity: "+getActivity());
                //AudioPlayer requiere un contexto como parámetro
                //mPlayer.play(getActivity) para configurar la superficie (Surface)
                if (mPlayer.play(getActivity())) {
                    mPlayButton.setText(R.string.hellomoon_pause);

                }
                else {
                    mPlayButton.setText(R.string.hellomoon_play);

                }
            }
        });


        if(mPlayer.isPlaying())
            mPlayButton.setText(R.string.hellomoon_pause);
        else
            mPlayButton.setText(R.string.hellomoon_play);

        mStopButton = (Button) fragmentView.findViewById(R.id.hellomoon_stopButton);
        mStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayer.stop();

                mPlayButton.setText(R.string.hellomoon_play);

            }
        });
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mPlayButton.setText(R.string.hellomoon_play);
            }
        });
        return fragmentView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPlayer.stop();
    }


}
