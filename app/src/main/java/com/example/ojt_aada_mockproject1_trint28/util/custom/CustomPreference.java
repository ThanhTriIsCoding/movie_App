package com.example.ojt_aada_mockproject1_trint28.util.custom;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

public class CustomPreference extends Preference {

    public CustomPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayoutResource(androidx.preference.R.layout.preference); // Đảm bảo sử dụng layout mặc định
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);

        // Lấy TextView của tiêu đề (Title)
        TextView titleView = (TextView) holder.findViewById(android.R.id.title);
        if (titleView != null) {
            titleView.setTextColor(Color.BLACK); // Màu đen
            titleView.setTypeface(Typeface.DEFAULT_BOLD); // Chữ đậm
            titleView.setTextSize(30); // Kích thước chữ 30sp
        }

        // Lấy TextView của mô tả (Summary)
        TextView summaryView = (TextView) holder.findViewById(android.R.id.summary);
        if (summaryView != null) {
            summaryView.setTextColor(Color.parseColor("#CCCCCC")); // Màu xám nhạt
            summaryView.setTextSize(20); // Kích thước chữ 20sp
        }
    }
}