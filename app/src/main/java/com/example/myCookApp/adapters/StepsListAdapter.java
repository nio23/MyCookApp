package com.example.myCookApp.adapters;

import android.content.Context;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.SuperscriptSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myCookApp.R;
import com.example.myCookApp.StepClickInterface;
import com.example.myCookApp.models.Step;
import com.example.myCookApp.models.StepWithImages;
import com.jakewharton.rxbinding3.view.RxView;
import com.jakewharton.rxbinding3.widget.RxTextView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class StepsListAdapter extends ListAdapter<StepWithImages, StepsListAdapter.ViewHolder> {

    private StepClickInterface listener;
    private Context context;



    public StepsListAdapter(StepClickInterface listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }


    private static final DiffUtil.ItemCallback<StepWithImages> DIFF_CALLBACK = new DiffUtil.ItemCallback<StepWithImages>() {
        @Override
        public boolean areItemsTheSame(StepWithImages oldItem, StepWithImages newItem) {
            return oldItem.getStep().getStepId() == newItem.getStep().getStepId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull StepWithImages oldItem, @NonNull StepWithImages newItem) {
            return oldItem.getStep().getInstructions().equals(newItem.getStep().getInstructions()) &&
                    oldItem.getImages().size() == newItem.getImages().size() &&
                    oldItem.getStep().getStepTime() == newItem.getStep().getStepTime();
        }

        @Nullable
        @Override
        public Object getChangePayload(@NonNull StepWithImages oldItem, @NonNull StepWithImages newItem) {
            if (oldItem.getImages().size() < newItem.getImages().size()){
               return  "imageInserted";
            }else if (oldItem.getImages().size() > newItem.getImages().size()){
                return "imageRemoved";
            }else if (oldItem.getStep().getStepTime() != newItem.getStep().getStepTime()) {
                return "stepTimeChanged";
            }else {
                return null;
            }
        }
    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.stepview_item2, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final StepWithImages currentStep = getItem(position);

        holder.stepnumber.setText(superScriptSpan((position + 1) + "ο βήμα"));

        if (currentStep.getStep().getInstructions() != null)
        holder.instructions.setText(currentStep.getStep().getInstructions());


        if (currentStep.getStep().getStepTime() > 0) {
            setTime(holder.time, currentStep.getStep().getStepTime());
        } else {
            holder.toggler.setChecked(false);
        }

        if (currentStep.getImages() != null && currentStep.getImages().size() > 0) {
            holder.imagesRV.setVisibility(View.VISIBLE);
            ImagesAdapter imagesAdapter = new ImagesAdapter(listener, true);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false);
            holder.imagesRV.setAdapter(imagesAdapter);
            holder.imagesRV.setLayoutManager(linearLayoutManager);
            imagesAdapter.submitList(currentStep.getImages());
        }

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()){
            onBindViewHolder(holder,position);
        }else {
            StepWithImages currentStep = getItem(position);
            holder.stepnumber.setText(superScriptSpan((position + 1) + "ο βήμα"));
            for (Object data: payloads){
                if (data.equals("imageInserted")){
                    if (((ImagesAdapter) holder.imagesRV.getAdapter()) != null) {
                        ((ImagesAdapter) holder.imagesRV.getAdapter()).submitList(currentStep.getImages());
                    }else {
                        holder.imagesRV.setVisibility(View.VISIBLE);
                        ImagesAdapter imagesAdapter = new ImagesAdapter(listener, true);
                        holder.imagesRV.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
                        holder.imagesRV.setAdapter(imagesAdapter);
                        imagesAdapter.submitList(currentStep.getImages());
                    }
                }
                if (data.equals("imageRemoved")){
                    if (((ImagesAdapter) holder.imagesRV.getAdapter()) != null) {
                        ((ImagesAdapter) holder.imagesRV.getAdapter()).submitList(currentStep.getImages());
                        if (currentStep.getImages().size() == 0)
                            holder.imagesRV.setVisibility(View.GONE);
                    }
                }
                if (data.equals("stepTimeChanged")){
                    if (currentStep.getStep().getStepTime() > 0) {
                        setTime(holder.time, currentStep.getStep().getStepTime());
                        holder.time.setVisibility(View.VISIBLE);
                    } else {
                        holder.toggler.setChecked(false);
                    }
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    public StepWithImages getStepWithImagesAt(int position){
        return getItem(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        EditText instructions;
        TextView stepnumber, time;
        RecyclerView imagesRV;
        Button expandViewBtn;
        CardView cardView;
        SwitchCompat toggler;
        Group group;
        ImageButton addNewImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            stepnumber = itemView.findViewById(R.id.stepNumberTV);
            instructions = itemView.findViewById(R.id.instructionsET);
            imagesRV = itemView.findViewById(R.id.imagesRV);
            expandViewBtn = itemView.findViewById(R.id.expandViewBtn);
            cardView = itemView.findViewById(R.id.cardview);
            toggler = itemView.findViewById(R.id.togglerSW);
            time = itemView.findViewById(R.id.timeTV);
            group = itemView.findViewById(R.id.group);
            addNewImage = itemView.findViewById(R.id.addNewImage);


            addNewImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.addNewImageClick(getItem(position).getStep().getStepId());
                    }
                }
            });


            toggler.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null)
                        if (isChecked) {
                            listener.onTogglerSwitch(getItem(position).getStep());
                        } else {
                            time.setVisibility(View.GONE);
                        }
                }
            });


            expandViewBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (group.getVisibility() == View.GONE) {
                        group.setVisibility(View.VISIBLE);
                        if (getItem(getAdapterPosition()).getStep().getStepTime() > 0)
                            time.setVisibility(View.VISIBLE);
                        if (imagesRV.getAdapter() == null)
                            imagesRV.setVisibility(View.GONE);
                        expandViewBtn.setBackgroundResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                    } else {
                        group.setVisibility(View.GONE);
                        time.setVisibility(View.GONE);
                        expandViewBtn.setBackgroundResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                    }
                }
            });




            instructions.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION && instructions.hasFocus())
                        listener.onTextChange(getItem(getAdapterPosition()).getStep(), s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });


        }


    }

    private SpannableString superScriptSpan(String string) {
        SpannableString spannableString = new SpannableString(string);
        spannableString.setSpan(new SuperscriptSpan(), string.indexOf("ο"), string.indexOf("ο") + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    private int getHours(int timeInSeconds) {
        return timeInSeconds / 3600;

    }

    private int getMinutes(int timeInSeconds) {
        return (timeInSeconds % 3600) / 60;
    }

    private void setTime(TextView textView, int time) {
        if (time > 0) {
            textView.setText(context.getResources().getQuantityString(R.plurals.time, getHours(time), getHours(time), getMinutes(time)));
            if (getHours(time) == 0) {
                textView.setText(context.getResources().getQuantityString(R.plurals.timeZeroHours, getMinutes(time), getMinutes(time)));
            }
            if (getMinutes(time) == 0) {
                textView.setText(context.getResources().getQuantityString(R.plurals.timeZeroMinutes, getHours(time), getHours(time)));
            }
        }
    }
}
