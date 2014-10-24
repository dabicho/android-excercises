package mx.org.dabicho.photogallery;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.File;

/**
 * Fragmento para mostrar la página de una foto
 */
public class PhotoPageFragment extends VisibleSupportFragment {
    private static final String TAG = "PhotoFragment";

    private String mUrl;
    private WebView mWebView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        mUrl=getActivity().getIntent().getDataString();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View lView=inflater.inflate(R.layout.fragment_photo_page,container,false);

        final ProgressBar lProgressBar=(ProgressBar)lView.findViewById(R.id.progressBar);
        lProgressBar.setMax(100); //webCrhome reporta porcentaje de avance de 0 a 100
        final TextView titleTextView = (TextView) lView.findViewById(R.id.titleTextView);

        mWebView=(WebView) lView.findViewById(R.id.webView);
        mWebView.getSettings().setJavaScriptEnabled(true);
        File cacheDir = getActivity().getExternalCacheDir();
        if(cacheDir==null)
            cacheDir=getActivity().getCacheDir();
        mWebView.getSettings().setAppCachePath(cacheDir.toString());
        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if(newProgress==100)
                    lProgressBar.setVisibility(View.INVISIBLE);
                else {
                    lProgressBar.setVisibility(View.VISIBLE);
                    lProgressBar.setProgress(newProgress);

                }

            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                titleTextView.setText(title);
            }
        });

        mWebView.loadUrl(mUrl);
        return lView;
    }
}
