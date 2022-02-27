package com.nameisknowledge.knowledgebank;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nameisknowledge.knowledgebank.databinding.CustomItemUsersBinding;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.Holder>{
    private List<MD_User> MDUsers;
    private RequestListener requestListener;

    public UsersAdapter(List<MD_User> MDUsers,RequestListener requestListener) {
        this.MDUsers = MDUsers;
        this.requestListener = requestListener;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_item_users,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.Bind(MDUsers.get(position));
    }

    @Override
    public int getItemCount() {
        return MDUsers.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        private CustomItemUsersBinding binding;
        private String id,name;
        public Holder(@NonNull View itemView) {
            super(itemView);
            binding = CustomItemUsersBinding.bind(itemView);
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    requestListener.request(id,name);
                }
            });
        }
        private void Bind(MD_User MDUser){
            this.name = MDUser.getFullName();
            this.id = MDUser.getId();
            binding.txvEmail.setText(MDUser.getEmail());
            binding.txvFullName.setText(MDUser.getFullName());
        }
    }
}
