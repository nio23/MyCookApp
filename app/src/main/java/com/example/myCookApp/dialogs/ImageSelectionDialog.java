package com.example.myCookApp.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.myCookApp.StepClickInterface;
import com.example.myCookApp.R;


public class ImageSelectionDialog extends DialogFragment {
    //public static final String TAG = ImageSelectionDialog.class.getSimpleName();
    private TextView closeBtn;
    private RelativeLayout cameraLayout, imageLayout;
    private StepClickInterface listener;
    private int requestCode;

    public ImageSelectionDialog(StepClickInterface listener, int requestCode) {
        this.listener = listener;
        this.requestCode = requestCode;
    }


  /*  public static ImageSelectionDialog getInstance() {
        ImageSelectionDialog fragment = new ImageSelectionDialog();
        return fragment;
    }*/



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.select_image_dialog, container, false);
        closeBtn = view.findViewById(R.id.closeBtn);
        cameraLayout = view.findViewById(R.id.camera);
        imageLayout = view.findViewById(R.id.image);

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        cameraLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.dialogSelection(cameraLayout.getId(), requestCode);
                    dismiss();
                }
            }
        });

        imageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.dialogSelection(imageLayout.getId(), requestCode);
                    dismiss();
                }
            }
        });

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (StepClickInterface) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    +" must implement ImageSelectionDialogListener");
        }
    }

}
