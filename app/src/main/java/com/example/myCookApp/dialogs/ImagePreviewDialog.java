package com.example.myCookApp.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.myCookApp.R;
import com.squareup.picasso.Picasso;

public class ImagePreviewDialog extends DialogFragment {

    public ImagePreviewDialog() {
    }

    private String imageUri;

    public static ImagePreviewDialog newInstance(String imageUri) {
        Bundle args = new Bundle();
        args.putString("uri",imageUri);
        ImagePreviewDialog dialog = new ImagePreviewDialog();
        dialog.setArguments(args);
        return dialog;
    }

    /*@Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_image_preview, container, false);
        View view = inflater.inflate(R.layout.dialog_image_preview, container, false);
        ImageView imageView = view.findViewById(R.id.imagePreview);
        Picasso.get().load(imageUri).into(imageView);
        return view;
    }
*/
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                builder.setView(R.layout.dialog_image_preview);

                LayoutInflater inflater = getActivity().getLayoutInflater();
                View view = inflater.inflate(R.layout.dialog_image_preview, null);
                ImageView imageView = view.findViewById(R.id.imagePreview);
                Picasso.get().load(getArguments().getString("uri")).into(imageView);
                builder.setView(view);
        return builder.create();

    }


}
