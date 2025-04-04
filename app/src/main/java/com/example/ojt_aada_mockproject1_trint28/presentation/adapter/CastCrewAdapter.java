package com.example.ojt_aada_mockproject1_trint28.presentation.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.paging.PagingDataAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ojt_aada_mockproject1_trint28.R;
import com.example.ojt_aada_mockproject1_trint28.databinding.ItemCastCrewBinding;
import com.example.ojt_aada_mockproject1_trint28.domain.model.CastCrew;
import com.squareup.picasso.Picasso;

public class CastCrewAdapter extends PagingDataAdapter<CastCrew, CastCrewAdapter.CastCrewViewHolder> {

    public CastCrewAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public CastCrewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCastCrewBinding binding = ItemCastCrewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CastCrewViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CastCrewViewHolder holder, int position) {
        CastCrew castCrew = getItem(position);
        if (castCrew != null) {
            holder.bind(castCrew);
        }
    }

    static class CastCrewViewHolder extends RecyclerView.ViewHolder {
        private final ItemCastCrewBinding binding;

        CastCrewViewHolder(ItemCastCrewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(CastCrew castCrew) {
            binding.tvName.setText(castCrew.getName());
            if (castCrew.getProfilePath() != null && !castCrew.getProfilePath().isEmpty()) {
                Picasso.get()
                        .load(castCrew.getProfilePath())
                        .resize(50, 70)
                        .centerCrop()
                        .placeholder(R.drawable.ic_launcher_background)
                        .error(R.drawable.ic_close)
                        .into(binding.ivProfile);
            } else {
                binding.ivProfile.setImageResource(R.drawable.ic_launcher_background);
            }
        }
    }

    private static final DiffUtil.ItemCallback<CastCrew> DIFF_CALLBACK = new DiffUtil.ItemCallback<CastCrew>() {
        @Override
        public boolean areItemsTheSame(@NonNull CastCrew oldItem, @NonNull CastCrew newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull CastCrew oldItem, @NonNull CastCrew newItem) {
            return oldItem.equals(newItem);
        }
    };
}