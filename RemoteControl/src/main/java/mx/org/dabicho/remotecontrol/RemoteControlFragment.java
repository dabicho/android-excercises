package mx.org.dabicho.remotecontrol;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Fragmento para mostrar la interfaz del control remoto
 */
public class RemoteControlFragment extends Fragment {
    private TextView mSelectedTextView;
    private TextView mWorkingTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_remote_control, container, false);
        mSelectedTextView = (TextView) v.findViewById(R.id.fragment_remote_control_selectedTextView);
        mWorkingTextView = (TextView) v.findViewById(R.id.fragment_remote_control_workingTextView);

        View.OnClickListener numberButtonListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                TextView lTextView = (TextView) v;
                String working = mWorkingTextView.getText().toString();
                String text = lTextView.getText().toString();

                if (working.equals("0")) {

                    mWorkingTextView.setText(text);
                } else {
                    mWorkingTextView.setText(working + text);
                }

            }
        };

        TableLayout lTableLayout=(TableLayout)v.findViewById(R.id.fragment_remote_control_tableLayout);
        // Representa al número del botón
        Integer number=1;
        int i;
        for (i=2; i<lTableLayout.getChildCount()-1; i++){
            TableRow r = (TableRow)lTableLayout.getChildAt(i);
            for(int j=0; j<r.getChildCount(); j++){
                Button lButton=(Button)r.getChildAt(j);
                lButton.setText(number.toString());
                lButton.setOnClickListener(numberButtonListener);
                number++;
            }
        }
        TableRow r = (TableRow)lTableLayout.getChildAt(i);

        Button lButton=(Button)r.getChildAt(0);
        lButton.setText("Delete");
        lButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWorkingTextView.setText("0");
            }
        });

        lButton=(Button)r.getChildAt(2);
        lButton.setText("Enter");
        lButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence working=mWorkingTextView.getText();
                if(working.length()>0)
                    mSelectedTextView.setText(working);
                mWorkingTextView.setText("0");
            }
        });

        lButton=(Button)r.getChildAt(1);
        lButton.setText("0");
        lButton.setOnClickListener(numberButtonListener);
        return v;

    }


}
