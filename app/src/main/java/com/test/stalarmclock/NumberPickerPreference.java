package com.test.stalarmclock;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.preference.PreferenceFragment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.NumberPicker;

/**
 * Created by a_ghelfi on 11/04/2016.
 */
public class NumberPickerPreference extends DialogPreference {

    private static final int DEFAULT_MIN_VALUE = 10;
    private static final int DEFAULT_MAX_VALUE = 80;
    private static final int DEFAULT_VALUE = 10;

    private int mMinValue;
    private int mMaxValue;
    private int mValue;
    private NumberPicker mNumberPicker;

    public NumberPickerPreference(Context context) {
        this(context, null);
    }

    public NumberPickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        setMinValue(DEFAULT_MIN_VALUE);
        setMaxValue(DEFAULT_MAX_VALUE);

        // set layout
        setDialogLayoutResource(R.layout.numberpicker_dialog);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);
        setDialogIcon(null);
    }

    @Override
    protected void onSetInitialValue(boolean restore, Object defaultValue) {
        setValue(restore ? getPersistedInt(DEFAULT_VALUE) : (Integer) defaultValue);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, DEFAULT_VALUE);
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        mNumberPicker = (NumberPicker) view.findViewById(R.id.number_picker);
        mNumberPicker.setMinValue(mMinValue);
        mNumberPicker.setMaxValue(mMaxValue);
        mNumberPicker.setValue(mValue);
    }

    public int getMinValue() {
        return mMinValue;
    }

    public void setMinValue(int minValue) {
        mMinValue = minValue;
        setValue(Math.max(mValue, mMinValue));
    }

    public int getMaxValue() {
        return mMaxValue;
    }

    public void setMaxValue(int maxValue) {
        mMaxValue = maxValue;
        setValue(Math.min(mValue, mMaxValue));
    }

    public int getValue() {
        return mValue;
    }

    public void setValue(int value) {
        value = Math.max(Math.min(value, mMaxValue), mMinValue);

        if (value != mValue) {
            mValue = value;
            persistInt(value);
            notifyChanged();
        }
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        // when the user selects "OK", persist the new value
        if (positiveResult) {
            int numberPickerValue = mNumberPicker.getValue();
            if (callChangeListener(numberPickerValue)) {
                setValue(numberPickerValue);
            }
        }
    }
}
