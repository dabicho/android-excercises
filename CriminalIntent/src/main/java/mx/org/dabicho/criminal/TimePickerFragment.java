package mx.org.dabicho.criminal;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by dabicho on 10/2/14.
 */
public class TimePickerFragment extends DialogFragment {

    public static final String EXTRA_TIME = "mx.org.dabicho.criminalIntent.time";
    private Date mDate;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mDate = (Date) getArguments().getSerializable(EXTRA_TIME);

        Calendar lCalendar = Calendar.getInstance();
        lCalendar.setTime(mDate);
        int hour = lCalendar.get(Calendar.HOUR_OF_DAY);
        int minutes = lCalendar.get(Calendar.MINUTE);

        View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_time, null);

        TimePicker lTimePicker = (TimePicker) v.findViewById(R.id.dialog_time_timePicker);
        lTimePicker.setIs24HourView(true);
        lTimePicker.setCurrentHour(hour);
        lTimePicker.setCurrentMinute(minutes);
        lTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                Calendar c = Calendar.getInstance();
                c.setTime(mDate);
                mDate = new GregorianCalendar(c.get(Calendar.YEAR), c.get(Calendar.MONTH),
                        c.get(Calendar.DAY_OF_MONTH), hourOfDay, minute).getTime();

                getArguments().putSerializable(EXTRA_TIME,mDate);
            }
        });

        return new AlertDialog.Builder(getActivity()).setView(v)
                .setTitle(R.string.time_picker_title)
                .setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendResult(Activity.RESULT_OK);
                    }
                }).create();
    }

    public static TimePickerFragment newInstance(Date date) {
        Bundle args=new Bundle();
        args.putSerializable(EXTRA_TIME,date);
        TimePickerFragment fragment=new TimePickerFragment();
        fragment.setArguments(args);
        return fragment;

    }

    private void sendResult(int resultCode) {
        if(getTargetFragment()==null)
            return;
        Intent i=new Intent();
        i.putExtra(EXTRA_TIME,mDate);
        getTargetFragment().onActivityResult(getTargetRequestCode(),resultCode,i);
    }
}
