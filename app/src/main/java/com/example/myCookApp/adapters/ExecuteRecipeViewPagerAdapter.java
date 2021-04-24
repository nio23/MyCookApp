package com.example.myCookApp.adapters;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.myCookApp.R;
import com.example.myCookApp.models.Step;
import com.example.myCookApp.models.StepWithImages;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ExecuteRecipeViewPagerAdapter extends ListAdapter<StepWithImages, ExecuteRecipeViewPagerAdapter.ViewHolder> {
    private Context context;
    private OnItemClickListener listener;


    public ExecuteRecipeViewPagerAdapter() {
        super(diffCallback);
    }

    private static final DiffUtil.ItemCallback<StepWithImages> diffCallback = new DiffUtil.ItemCallback<StepWithImages>() {
        @Override
        public boolean areItemsTheSame(@NonNull StepWithImages oldItem, @NonNull StepWithImages newItem) {
            return oldItem.getStep().getStepId() == newItem.getStep().getStepId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull StepWithImages oldItem, @NonNull StepWithImages newItem) {
            return oldItem.getStep().getTimeLeft() == newItem.getStep().getTimeLeft() &&
                    oldItem.getStep().isRunning() == newItem.getStep().isRunning();
        }

        @Nullable
        @Override
        public Object getChangePayload(@NonNull StepWithImages oldItem, @NonNull StepWithImages newItem) {
            if (oldItem.getStep().isRunning() != newItem.getStep().isRunning())
                return "startOrStopTimer";
            return null;
        }
    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.execute_recipe_item, parent, false);
        context = parent.getContext();
        return new ViewHolder(itemView, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StepWithImages currentStep = getItem(position);

        holder.instructionsTv.setText(currentStep.getStep().getInstructions());

        if (currentStep.getStep().getStepTime() > 0) {
            holder.timerLayout.setVisibility(View.VISIBLE);
        } else {
            holder.timerLayout.setVisibility(View.GONE);
        }

        if (currentStep.getStep().isRunning())
            holder.timerIB.setBackground(context.getResources().getDrawable(R.drawable.ic_baseline_timer_off_90dp));
        else
            holder.timerIB.setBackground(context.getResources().getDrawable(R.drawable.ic_baseline_timer_90dp));

        ExecuteRecipeImagesViewPagerAdapter imagesAdapter = new ExecuteRecipeImagesViewPagerAdapter();
        holder.imagesVP.setAdapter(imagesAdapter);
        imagesAdapter.setImages((ArrayList) currentStep.getImages());

        new TabLayoutMediator(holder.tabLayout, holder.imagesVP, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {

            }
        }).attach();

        if (currentStep.getImages().size() > 0){
            holder.imagesLayout.setVisibility(View.VISIBLE);
            holder.instructionsTv.setGravity(Gravity.CENTER_HORIZONTAL);
        }else{
            holder.imagesLayout.setVisibility(View.GONE);
            holder.instructionsTv.setGravity(Gravity.CENTER);
        }


    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
        if (payloads.isEmpty())
            onBindViewHolder(holder, position);
        else {
            StepWithImages currentStep = getItem(position);
            for (Object data : payloads) {
                if (data.equals("startOrStopTimer")) {
                    if (currentStep.getStep().isRunning())
                        holder.timerIB.setBackground(context.getResources().getDrawable(R.drawable.ic_baseline_timer_off_90dp));
                    else
                        holder.timerIB.setBackground(context.getResources().getDrawable(R.drawable.ic_baseline_timer_90dp));

                }
            }
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageButton timerIB;
        TextView instructionsTv, notifyMeTV;
        CheckBox notifyCB;
        ViewPager2 imagesVP;
        ConstraintLayout timerLayout;
        TabLayout tabLayout;
        FrameLayout imagesLayout;

        public ViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            timerIB = itemView.findViewById(R.id.timerIB);
            instructionsTv = itemView.findViewById(R.id.instructionsTV);
            notifyCB = itemView.findViewById(R.id.notifyMeCheckBox);
            imagesVP = itemView.findViewById(R.id.imagesNestedViewpager);
            notifyMeTV = itemView.findViewById(R.id.notifyMeTV);
            timerLayout = itemView.findViewById(R.id.timerLayout);
            tabLayout = itemView.findViewById(R.id.tabLayout);
            imagesLayout = itemView.findViewById(R.id.imagesLayout);

            notifyCB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION){
                        listener.checkBoxState(notifyCB.isChecked(), position);
                    }
                }
            });

            timerIB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        //long currentStepId = getItem(position).getStep().getStepId();
                        listener.startOrStopTimer(getItem(position).getStep(), position);
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void startOrStopTimer(Step step, int position);
        void checkBoxState(Boolean state, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


}
