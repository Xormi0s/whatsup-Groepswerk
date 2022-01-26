package be.ucll.whatsup.Chat;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import be.ucll.whatsup.Contacts.ContactListAdapter;
import be.ucll.whatsup.Contacts.ContactModel;
import be.ucll.whatsup.Contacts.contactListModel;
import be.ucll.whatsup.R;

public class MakeGroupAdaptor extends RecyclerView.Adapter<MakeGroupAdaptor.ViewHolder>{

    private Activity activity;
    private ArrayList<contactListModel> arrayList;
    private ArrayList<contactListModel> checkboxlist = new ArrayList<>();

    public MakeGroupAdaptor(Activity activity, ArrayList<contactListModel> arrayList) {
        this.activity = activity;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.groupcontact_list, parent, false);
        return new MakeGroupAdaptor.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MakeGroupAdaptor.ViewHolder holder, int position) {
        contactListModel model = arrayList.get(position);

        holder.getCheckBox().setText(model.getName());

        holder.getCheckBox().setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //if(checkboxlist == null || checkboxlist.size() >= 1){
                    if(holder.getCheckBox().isChecked()){
                        checkboxlist.add(model);
                    } else {
                        checkboxlist.remove(model);
                    }
                //}
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final CheckBox checkBox;

        public ViewHolder(@NonNull View view) {
            super(view);
            checkBox = view.findViewById(R.id.checkBoxContact);
        }

        public CheckBox getCheckBox() {
            return checkBox;
        }
    }

    public ArrayList<contactListModel> getCheckboxlist() {
        return checkboxlist;
    }
}
