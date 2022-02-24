package com.nameisknowledge.knowledgebank;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nameisknowledge.knowledgebank.databinding.CustomItemUsersBinding;

import java.util.ArrayList;
import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.Holder>{
    private List<User> users;

    public UsersAdapter(List<User> users) {
        this.users = users;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_item_users,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.Bind(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        CustomItemUsersBinding binding;
        public Holder(@NonNull View itemView) {
            super(itemView);
            binding = CustomItemUsersBinding.bind(itemView);
        }
        private void Bind(User user){
            binding.txvEmail.setText(user.getEmail());
            binding.txvFullName.setText(user.getFullName());
        }
    }
}
