package nl.camilstaps.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

import nl.camilstaps.rbn.R;

public class RangePreference extends DialogPreference {
	private EditText minEditText, maxEditText;
	private final float defaultMin;
	private final float defaultMax;

	public RangePreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		setDialogLayoutResource(R.layout.preference_range);

		TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.RangePreference);

		defaultMin = attributes.getFloat(R.styleable.RangePreference_defaultMinValue, 0);
		defaultMax = attributes.getFloat(R.styleable.RangePreference_defaultMaxValue, 100);

		attributes.recycle();
	}

	public float getMinValue() {
		return getSharedPreferences().getFloat(getMinKey(), defaultMin);
	}

	public float getMaxValue() {
		return getSharedPreferences().getFloat(getMaxKey(), defaultMax);
	}

	public void setSummaryLikeValue() {
		setSummary(Util.formatFloat(getMinValue()) + " - " + Util.formatFloat(getMaxValue()));
	}

	@Override
	protected void onBindDialogView(View view) {
		super.onBindDialogView(view);

		minEditText = (EditText) view.findViewById(R.id.min);
		maxEditText = (EditText) view.findViewById(R.id.max);

		SharedPreferences prefs = getSharedPreferences();
		minEditText.setText(Float.toString(prefs.getFloat(getMinKey(), defaultMin)));
		maxEditText.setText(Float.toString(prefs.getFloat(getMaxKey(), defaultMax)));
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);

		if (positiveResult) {
			getEditor()
					.putFloat(getMinKey(), Float.parseFloat(minEditText.getText().toString()))
					.putFloat(getMaxKey(), Float.parseFloat(maxEditText.getText().toString()))
					.commit();
		}
	}

	protected String getMinKey() {
		return getKey() + "_min";
	}

	protected String getMaxKey() {
		return getKey() + "_max";
	}
}
