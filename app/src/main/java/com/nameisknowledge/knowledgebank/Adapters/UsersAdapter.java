package com.nameisknowledge.knowledgebank.Adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nameisknowledge.knowledgebank.Listeners.GenericListener;
import com.nameisknowledge.knowledgebank.ModelClasses.UserMD;
import com.nameisknowledge.knowledgebank.R;
import com.nameisknowledge.knowledgebank.databinding.CustomItemUserBinding;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.Holder>{
    private List<UserMD> users;
    private GenericListener<String> listener;

    public UsersAdapter(List<UserMD> users, GenericListener<String> listener) {
        this.users = users;
        this.listener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setUsers(List<UserMD> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_item_user,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.bind(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        private CustomItemUserBinding binding;
        private String Uid;

        public Holder(@NonNull View itemView) {
            super(itemView);
            binding = CustomItemUserBinding.bind(itemView);
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.getData(Uid);
                }
            });
        }

        private void bind(UserMD userMD){
            this.Uid = userMD.getNotificationToken();
            binding.tvEmail.setText(userMD.getEmail());
            binding.tvUid.setText(userMD.getUid());
        }
    }
}
