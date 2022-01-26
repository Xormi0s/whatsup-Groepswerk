package be.ucll.whatsup.Contacts;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import be.ucll.whatsup.Chat.ChatScreen;
import be.ucll.whatsup.Chat.GroupScreen;
import be.ucll.whatsup.R;


public class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.ViewHolder>{

    private Activity activity;
    private ArrayList<GroupContact> groupContacts;
    private String uid;

    private CollectionReference group;
    private FirebaseFirestore db;

    public GroupListAdapter(Activity activity, ArrayList<GroupContact> arrayList, String uid) {
        this.activity = activity;
        this.groupContacts = arrayList;
        this.uid = uid;

        db = FirebaseFirestore.getInstance();
        group = db.collection("GroupschatList");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_list, parent, false);
        return new GroupListAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupListAdapter.ViewHolder holder, int position) {
        GroupContact groupContact = groupContacts.get(position);

        holder.tvName.setText(groupContact.getName());

        if(groupContact.getName4() != null){
            holder.tvNumber.setText(groupContact.getName1() + ", " + groupContact.getName2() + ", " + groupContact.getName3() + ", " + groupContact.getName4());
        } else {
            holder.tvNumber.setText(groupContact.getName1() + ", " + groupContact.getName2() + ", " + groupContact.getName3());
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity.getApplicationContext(), GroupScreen.class);
                intent.putExtra("id", groupContact.getId());
                intent.putExtra("name", groupContact.getName());
                activity.finish();
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        int count = groupContacts.size();
        return count;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvName;
        private final TextView tvNumber;

        public ViewHolder(@NonNull View view) {
            super(view);
            tvName = view.findViewById(R.id.tv_name);
            tvNumber = view.findViewById(R.id.tv_number);
        }
    }
}
