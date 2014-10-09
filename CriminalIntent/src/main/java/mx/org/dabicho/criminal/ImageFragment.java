package mx.org.dabicho.criminal;


import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import mx.org.dabicho.criminal.api.PictureUtils;


/**
 * Fragmento de diálogo para mostrar una imágen
 */
public class ImageFragment extends DialogFragment {

    private ImageView mImageView;

    public static final String EXTRA_IMAGE_PATH="mx.org.dabicho.criminal.image_path";

    public static ImageFragment newInstance(String path) {
        Bundle args=new Bundle();
        args.putSerializable(EXTRA_IMAGE_PATH,path);
        ImageFragment lFragment=new ImageFragment();
        lFragment.setArguments(args);
        lFragment.setStyle(STYLE_NO_TITLE,0);

        return lFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        mImageView=new ImageView(getActivity());
        String path=(String)getArguments().getSerializable(EXTRA_IMAGE_PATH);
        BitmapDrawable image= PictureUtils.getScaledDrawable(getActivity(),path);
        mImageView.setImageDrawable(image);

        return mImageView;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        PictureUtils.cleanImageView(mImageView);
    }


}

