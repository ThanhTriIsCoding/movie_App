package com.example.data.custom;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceViewHolder;

public class CustomPreferenceCategory extends PreferenceCategory {

    public CustomPreferenceCategory(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);

        TextView titleView = (TextView) holder.findViewById(android.R.id.title);
        if (titleView != null) {
            titleView.setTextColor(Color.parseColor("#CCCCCC")); // Màu xám nhạt
            titleView.setTextSize(18); // Cỡ chữ 18sp
            titleView.setTypeface(Typeface.DEFAULT_BOLD); // Chữ đậm
            titleView.setAllCaps(true); // Chữ viết hoa như "FILTER" và "SORT"

            // Thêm gạch chân giả lập bằng cách sử dụng đường kẻ
            View underline = new View(titleView.getContext());
            underline.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    4 // Độ dày của gạch chân
            ));
            underline.setBackgroundColor(Color.parseColor("#CCCCCC"));

            // Đặt vị trí dưới tiêu đề
            ((ViewGroup) titleView.getParent()).addView(underline);
        }
    }
}