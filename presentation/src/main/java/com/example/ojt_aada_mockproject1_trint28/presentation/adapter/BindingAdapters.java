package com.example.ojt_aada_mockproject1_trint28.presentation.adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;
import androidx.databinding.InverseBindingListener;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.ojt_aada_mockproject1_trint28.R;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class BindingAdapters {

    @BindingAdapter("imageUrl")
    public static void loadImage(ImageView imageView, String url) {
        if (url != null && !url.isEmpty()) {
            Picasso.get()
                    .load(url)
                    .resize(200, 300)
                    .centerCrop()
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(imageView);
        } else {
            imageView.setImageResource(R.drawable.ic_launcher_foreground);
        }
    }

    @BindingAdapter("circleImageUrl")
    public static void loadCircleImage(CircleImageView imageView, String url) {
        if (url != null && !url.isEmpty()) {
            Picasso.get()
                    .load(url)
                    .resize(120, 120)
                    .centerCrop()
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(imageView);
        } else {
            imageView.setImageResource(R.drawable.ic_launcher_foreground);
        }
    }

    @BindingAdapter("onCheckedChanged")
    public static void setOnCheckedChangeListener(RadioGroup radioGroup, RadioGroup.OnCheckedChangeListener listener) {
        radioGroup.setOnCheckedChangeListener(listener);
    }

    @BindingAdapter("checkedButton")
    public static void setCheckedButton(RadioGroup radioGroup, int buttonId) {
        radioGroup.check(buttonId);
    }

    @BindingAdapter("voteAverageFormatted")
    public static void setVoteAverageFormatted(TextView textView, Double voteAverage) {
        if (voteAverage != null) {
            String formatted = String.format("%.1f/10", voteAverage);
            textView.setText(formatted);
        } else {
            textView.setText("N/A");
        }
    }

    // Binding adapter to set the text of EditText from LiveData
    @BindingAdapter(value = {"android:text", "android:textAttrChanged"}, requireAll = false)
    public static void setText(EditText editText, LiveData<String> liveData, final InverseBindingListener textAttrChanged) {
        String newValue = liveData != null ? liveData.getValue() : null;
        String currentValue = editText.getText() != null ? editText.getText().toString() : null;

        if (newValue != null && !newValue.equals(currentValue)) {
            editText.setText(newValue);
        }

        if (liveData instanceof MutableLiveData) {
            MutableLiveData<String> mutableLiveData = (MutableLiveData<String>) liveData;
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    String updatedValue = s.toString();
                    String currentLiveDataValue = mutableLiveData.getValue();
                    if (!updatedValue.equals(currentLiveDataValue)) {
                        mutableLiveData.setValue(updatedValue);
                    }
                    if (textAttrChanged != null) {
                        textAttrChanged.onChange();
                    }
                }
            });
        }
    }

    @InverseBindingAdapter(attribute = "android:text", event = "android:textAttrChanged")
    public static String getText(EditText editText) {
        return editText.getText() != null ? editText.getText().toString() : null;
    }

    // Two-way binding for RadioGroup
    @BindingAdapter("android:checkedButton")
    public static void setCheckedButton(RadioGroup radioGroup, String gender) {
        if (gender == null) return;
        if (gender.equals("Male")) {
            radioGroup.check(R.id.rb_male);
        } else if (gender.equals("Female")) {
            radioGroup.check(R.id.rb_female);
        } else {
            radioGroup.clearCheck();
        }
    }

    @InverseBindingAdapter(attribute = "android:checkedButton", event = "checkedButtonAttrChanged")
    public static String getCheckedButton(RadioGroup radioGroup) {
        int checkedId = radioGroup.getCheckedRadioButtonId();
        if (checkedId == R.id.rb_male) {
            return "Male";
        } else if (checkedId == R.id.rb_female) {
            return "Female";
        }
        return null;
    }

    @BindingAdapter("checkedButtonAttrChanged")
    public static void setCheckedButtonListener(RadioGroup radioGroup, final InverseBindingListener listener) {
        if (listener != null) {
            radioGroup.setOnCheckedChangeListener((group, checkedId) -> listener.onChange());
        }
    }
}