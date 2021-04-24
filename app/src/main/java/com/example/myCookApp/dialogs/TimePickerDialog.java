package com.example.myCookApp.dialogs;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.myCookApp.StepClickInterface;
import com.example.myCookApp.R;
import com.example.myCookApp.models.Step;


public class TimePickerDialog extends DialogFragment {

    private NumberPicker hoursPicker, minutesPicker;
    private StepClickInterface listener;
    private int hour, minutes;
    private Button buttonOk, buttonCancel;
    private Step step;

    public TimePickerDialog(StepClickInterface listener) {
        this.listener = listener;
    }

    public TimePickerDialog(StepClickInterface listener, Step step) {
        this.listener = listener;
        this.step = step;
    }


    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        listener.getTime(0,0, step);
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_step_time_picker, container, false);
        hoursPicker = view.findViewById(R.id.hoursPicker);
        minutesPicker = view.findViewById(R.id.minutesPicker);
        buttonOk = view.findViewById(R.id.buttonOk);
        buttonCancel = view.findViewById(R.id.buttonCancel);


        hoursPicker.setMaxValue(12);
        hoursPicker.setMinValue(0);
        minutesPicker.setMaxValue(60);
        minutesPicker.setMinValue(0);

        hoursPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                hour = newVal;
            }
        });

        minutesPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                minutes = newVal;
            }
        });

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.getTime(hour, minutes, step);
                    dismiss();
                }
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.getTime(0,0, step);
                dismiss();
            }
        });

        return view;
    }
}
