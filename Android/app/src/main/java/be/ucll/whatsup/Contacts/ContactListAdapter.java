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
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import be.ucll.whatsup.Chat.ChatScreen;
import be.ucll.whatsup.Login.User;
import be.ucll.whatsup.R;


public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.ViewHolder>{

    private Activity activity;
    private ArrayList<ContactModel> arrayList;
    private String uid;



    public ContactListAdapter(Activity activity, ArrayList<ContactModel> arrayList, String uid) {
        this.activity = activity;
        this.arrayList = arrayList;
        this.uid = uid;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_list, parent, false);
        return new ContactListAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactListAdapter.ViewHolder holder, int position) {
        ContactModel model = arrayList.get(position);

        holder.tvName.setText(model.getName());

        holder.tvNumber.setText(model.getNumber());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity.getApplicationContext(), ChatScreen.class);
                intent.putExtra("number", model.getNumber());
                intent.putExtra("name", model.getName());
                activity.finish();
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        int count = arrayList.size();
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
