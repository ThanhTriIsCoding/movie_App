package com.example.ojt_aada_mockproject1_trint28.presentation.adapter;

import android.widget.ImageView;
import android.widget.RadioGroup;

import androidx.databinding.BindingAdapter;

import com.example.ojt_aada_mockproject1_trint28.R;
import com.squareup.picasso.Picasso;

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

    @BindingAdapter("onCheckedChanged")
    public static void setOnCheckedChangeListener(RadioGroup radioGroup, RadioGroup.OnCheckedChangeListener listener) {
        radioGroup.setOnCheckedChangeListener(listener);
    }

    @BindingAdapter("checkedButton")
    public static void setCheckedButton(RadioGroup radioGroup, int buttonId) {
        radioGroup.check(buttonId);
    }
}