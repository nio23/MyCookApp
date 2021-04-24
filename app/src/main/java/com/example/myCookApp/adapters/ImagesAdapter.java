package com.example.myCookApp.adapters;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myCookApp.models.Image;
import com.example.myCookApp.R;
import com.example.myCookApp.StepClickInterface;
import com.squareup.picasso.Picasso;

public class ImagesAdapter extends ListAdapter<Image, ImagesAdapter.ViewHolder> {
    private StepClickInterface listener;
    private boolean contextMenu;

    public ImagesAdapter(StepClickInterface listener, boolean contextMenu){
        super(DIFF_CALLBACK);
        this.listener = listener;
        this.contextMenu = contextMenu;
    }

    public ImagesAdapter(boolean contextMenu){
        super(DIFF_CALLBACK);
        this.contextMenu = contextMenu;
    }


    private static final DiffUtil.ItemCallback<Image> DIFF_CALLBACK = new DiffUtil.ItemCallback<Image>() {
        @Override
        public boolean areItemsTheSame(@NonNull Image oldItem, @NonNull Image newItem) {
            return oldItem.getImageId() == newItem.getImageId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Image oldItem, @NonNull Image newItem) {
            return oldItem.getUri().equals(newItem.getUri());
        }


    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Image currentImage = getItem(position);
        Picasso.get().load(currentImage.getUri()).error(R.drawable.ic_photo_camera_30dp).fit().into(holder.image);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);

            if (contextMenu)
            image.setOnCreateContextMenuListener(this);


        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            MenuItem delete = menu.add(Menu.NONE, 1, 1, "Διαγραφή");
            delete.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onDeleteImage(getItem(position));
                        //notifyDataSetChanged();
                    }
                    return true;
                }
            });
        }
    }
}
