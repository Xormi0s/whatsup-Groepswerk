package be.ucll.whatsup.Chat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import be.ucll.whatsup.R;

public class ChatMessageListAdapter extends RecyclerView.Adapter<ChatMessageListAdapter.ViewHolder> {

    private List<Message> chatMessageList;
    private Context context;

    public ChatMessageListAdapter(List<Message> chatMessageList, Context context) {
        this.chatMessageList = chatMessageList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_message_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message chatMessage = chatMessageList.get(position);

        String id = holder.getmAuth().getUid();

        if (chatMessage.getMessage() != null  && chatMessage.getMessage() != "") {
            if(chatMessage.getUserID().equals(id)){
                holder.getTextViewRight().setText(chatMessageList.get(position).getMessage());
                holder.getTextViewRight().setPadding(20,20,20,20);
                holder.getTextViewRight().setBackground(ContextCompat.getDrawable(context, R.drawable.chat_right));
            } else{
                holder.getTextView().setText(chatMessageList.get(position).getMessage());
                holder.getTextView().setPadding(20,20,20,20);
                holder.getTextView().setBackground(ContextCompat.getDrawable(context, R.drawable.chat_left));
            }

        } else {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inMutable = true;
            Bitmap bmp = BitmapFactory.decodeByteArray(chatMessage.getImage().toBytes(), 0, chatMessage.getImage().toBytes().length, options);
            if(chatMessage.getUserID().equals(id)){
                holder.getImageViewRight().setImageBitmap(bmp);
                holder.getImageViewRight().setPadding(20,20,20,20);
                holder.getImageViewRight().setBackground(ContextCompat.getDrawable(context, R.drawable.chat_right));
            } else{
                holder.getImageView().setImageBitmap(bmp);
                holder.getImageView().setPadding(20,20,20,20);
                holder.getImageView().setBackground(ContextCompat.getDrawable(context, R.drawable.chat_left));
            }

        }
    }

    @Override
    public int getItemCount() {
        return chatMessageList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final TextView textViewRight;
        private final ImageView imageView;
        private final ImageView imageViewRight;
        private FirebaseAuth mAuth;

        public ViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.textView);
            imageView = view.findViewById(R.id.imageView);
            imageViewRight = view.findViewById(R.id.imageViewRight);
            textViewRight = view.findViewById(R.id.textViewRight);
            mAuth = FirebaseAuth.getInstance();
        }

        public ImageView getImageView() {
            return imageView;
        }

        public ImageView getImageViewRight() { return imageViewRight; }

        public TextView getTextView() { return textView; }

        public TextView getTextViewRight() { return textViewRight; }

        public FirebaseAuth getmAuth() { return mAuth; }
    }

}