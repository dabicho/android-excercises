package mx.org.dabicho.dabichoquiz;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;



/**
 * Activity que representa trampas en el quiz
 */
public class CheatActivity extends Activity {
    public static final String EXTRA_ANSWER_IS_TRUE = "mx.org.dabichoquiz.isTrue";
    public static final String USER_CHEATED="mx.org.dabichoquiz.userCheated";
    private boolean mAnswerIsTrue = true;
    private TextView mAnswerTextView;
    private Button mShowAnswer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheat_layout);
        userCheated(false);
        mAnswerIsTrue = getIntent().getBooleanExtra(EXTRA_ANSWER_IS_TRUE, true);

        mAnswerTextView=(TextView)findViewById(R.id.answerTextView);
        mShowAnswer=(Button)findViewById(R.id.showAnswerButton);
        mShowAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userCheated(true);
                if(mAnswerIsTrue)
                    mAnswerTextView.setText(R.string.true_button);
                else
                    mAnswerTextView.setText(R.string.false_button);
            }
        });


    }

    private void userCheated(boolean userCheated){
        Intent data=new Intent();
        data.putExtra(USER_CHEATED,userCheated);
        setResult(RESULT_OK,data);
    }
}
