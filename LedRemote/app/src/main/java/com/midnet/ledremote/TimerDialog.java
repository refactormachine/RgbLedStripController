package com.midnet.ledremote;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.Toast;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class TimerDialog extends DialogFragment {
    private Duration mHours;
    private Duration mMinutes;
    private Duration mSeconds;
    private TimerType mTimerType;

    int getTotalMiliseconds() {
        Duration mili = mHours.plus(mMinutes).plus(mSeconds);
        return (int)mili.toMillis();
    }

    public enum TimerType {
        TURN_ON,
        TURN_OFF
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View dialogView = inflater.inflate(R.layout.timer_dialog, null);
        final NumberPicker npHours = dialogView.findViewById(R.id.hoursNumberPicker);
        final NumberPicker npMinutes = dialogView.findViewById(R.id.minutesNumberPicker);
        final NumberPicker npSeconds = dialogView.findViewById(R.id.secondsNumberPicker);

        setMinMax(npHours, 0,99);
        setMinMax(npMinutes, 0,59);
        setMinMax(npSeconds, 0,59);
        builder.setView(dialogView)
                // Add action buttons
                .setPositiveButton(R.string.set, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //Do nothing here because we override this button later to change the close behaviour.
                        //However, we still need this because on older versions of Android unless we
                        //pass a handler the button doesn't get instantiated
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        TimerDialog.this.getDialog().cancel();
                    }
                });

        return builder.create();
    }

    private void setMinMax(NumberPicker numberPicker, int min, int max) {
        numberPicker.setMaxValue(max);
        numberPicker.setMinValue(min);
    }

    @Override
    public void onStart()
    {
        super.onStart();    //super.onStart() is where dialog.show() is actually called on the underlying dialog, so we have to do it after this point
        AlertDialog d = (AlertDialog)getDialog();
        if(d == null)
            return;

        final NumberPicker npHours = d.findViewById(R.id.hoursNumberPicker);
        final NumberPicker npMinutes = d.findViewById(R.id.minutesNumberPicker);
        final NumberPicker npSeconds = d.findViewById(R.id.secondsNumberPicker);
        final RadioButton radioTurnOn = d.findViewById(R.id.turnOnRadio);
        final RadioButton radioTurnOff = d.findViewById(R.id.turnOffRadio);

        Button positiveButton = d.getButton(Dialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mHours = Duration.of(npHours.getValue(), ChronoUnit.HOURS);
                mMinutes = Duration.of(npMinutes.getValue(), ChronoUnit.MINUTES);
                mSeconds = Duration.of(npSeconds.getValue(), ChronoUnit.SECONDS);
                mTimerType = radioTurnOn.isChecked() ? TimerType.TURN_ON : TimerType.TURN_OFF;

                if (canCloseDialog()) {
                    dismiss();
                    mListener.onTimerDialogPositiveClick(TimerDialog.this);
                }
                //else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.
            }

            @NonNull
            private Boolean canCloseDialog() {
                Boolean canCloseDialog = false;
                if (radioTurnOn.isChecked() || radioTurnOff.isChecked()) {
                    canCloseDialog = true;
                } else {
                    Toast.makeText(getActivity(), "You must select timer on or off", Toast.LENGTH_SHORT).show();
                }
                return canCloseDialog;
            }
        });

    }


    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface TimerDialogListener {
        public void onTimerDialogPositiveClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    TimerDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the TimerDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the TimerDialogListener so we can send events to the host
            mListener = (TimerDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement TimerDialogListener");
        }
    }

    public Duration getHours() {
        return mHours;
    }

    public Duration getMinutes() {
        return mMinutes;
    }

    public Duration getSeconds() {
        return mSeconds;
    }

    public TimerType getTimerType() {
        return mTimerType;
    }
}
