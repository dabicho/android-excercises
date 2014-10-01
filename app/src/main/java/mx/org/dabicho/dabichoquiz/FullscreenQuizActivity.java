package mx.org.dabicho.dabichoquiz;

import mx.org.dabicho.dabichoquiz.model.TrueFalse;
import mx.org.dabicho.dabichoquiz.util.SystemUiHider;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.*;
import android.support.v4.BuildConfig;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class FullscreenQuizActivity extends Activity {
    private final String TAG = "FullscreenQuizActivity";
    private static final String CURRENT_INDEX_KEY = "currentIndex";
    private static final String USER_CHEATED_KEY ="userCheated";
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * If set, will toggle the system UI visibility upon interaction. Otherwise,
     * will show the system UI visibility upon interaction.
     */
    private static final boolean TOGGLE_ON_CLICK = true;

    /**
     * The flags to pass to {@link SystemUiHider#getInstance}.
     */
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

    /**
     * The instance of the {@link SystemUiHider} for this activity.
     */
    private SystemUiHider mSystemUiHider;
    private Button mTrueButton;
    private Button mFalseButton;
    private Button mCheatButtom;
    private ImageButton mNextButton;
    private ImageButton mPrevButton;
    private TextView mQuestionTextView;

    private boolean mUserCheated;

    private TrueFalse[] mQuestionBank = new TrueFalse[]{
            new TrueFalse(R.string.question_oceans, true),
            new TrueFalse(R.string.question_mideast, false),
            new TrueFalse(R.string.question_africa, false),
            new TrueFalse(R.string.question_americas, true),
            new TrueFalse(R.string.question_asia, true),
            new TrueFalse(R.string.question_superman, false),
            new TrueFalse(R.string.question_gonzalo, false),
            new TrueFalse(R.string.question_dalia, true)
    };

    private int mCurrentIndex = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, " onCreate() called");
        restoreState(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_quiz);

        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);
        mQuestionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(FullscreenQuizActivity.this, "Siguiente pregunta:\n" + getResources().getString(mQuestionBank[(mCurrentIndex + 1) % mQuestionBank.length].getQuestion()), Toast.LENGTH_SHORT).show();
            }
        });
        updateQuestion();

        mTrueButton = (Button) findViewById(R.id.true_button);
        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(true);
            }
        });

        mFalseButton = (Button) findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(false);
            }
        });

        mCheatButtom = (Button) findViewById(R.id.cheat_button);
        mCheatButtom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cheatIntent = new Intent(FullscreenQuizActivity.this, CheatActivity.class);
                cheatIntent.putExtra(CheatActivity.EXTRA_ANSWER_IS_TRUE,
                        mQuestionBank[mCurrentIndex].isTrueQuestion());
                startActivityForResult(cheatIntent, 0);
            }
        });

        mPrevButton = (ImageButton) findViewById(R.id.prev_button);
        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex + -1 + mQuestionBank.length) % mQuestionBank.length;
                updateQuestion();
            }
        });

        mNextButton = (ImageButton) findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                updateQuestion();
            }
        });
    }

    private void updateQuestion() {
        int question = mQuestionBank[mCurrentIndex].getQuestion();
        mQuestionTextView.setText(question);
    }

    private void checkAnswer(boolean userPressedTrue) {
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isTrueQuestion();

        int messageResId = 0;

        if (userPressedTrue == answerIsTrue) {
            if(mUserCheated)
                messageResId=R.string.judgment_toast;
            else
                messageResId = R.string.respuesta_correcta;
        } else
            messageResId = R.string.respuesta_incorrecta;
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Log.d(TAG, " onPostCreate() called");
        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, " onPause() called");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, " onStart() called");

        Log.d(TAG, "BuildVersion: "+ Build.VERSION.SDK_INT);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, " onResume() called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, " onStop() called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, " onDestroy() called");
    }

    /*
    Este método es llamado por onPause, onStop y onDestroy, y sobre-escribirlo es una forma de guardar
    estados
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CURRENT_INDEX_KEY, mCurrentIndex);
        outState.putBoolean(USER_CHEATED_KEY, mUserCheated);
        Log.d(TAG, "onSaveInstanceState called");
    }

    private void restoreState(Bundle inState) {
        if (inState != null) {
            mCurrentIndex = inState.getInt(CURRENT_INDEX_KEY, 0);
            mUserCheated=inState.getBoolean(USER_CHEATED_KEY, false);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            mUserCheated = data.getBooleanExtra(CheatActivity.USER_CHEATED, false);
        }
    }
}
