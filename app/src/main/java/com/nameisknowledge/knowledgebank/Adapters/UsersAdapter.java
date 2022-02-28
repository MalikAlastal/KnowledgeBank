package com.nameisknowledge.knowledgebank.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.auth.User;
import com.nameisknowledge.knowledgebank.ModelClasses.UserMD;
import com.nameisknowledge.knowledgebank.R;
import com.nameisknowledge.knowledgebank.ReqListener;
import com.nameisknowledge.knowledgebank.databinding.CustomItemUserBinding;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.Holder>{
    private List<UserMD> users;
    private ReqListener reqListener;

    public UsersAdapter(List<UserMD> users, ReqListener reqListener) {
        this.users = users;
        this.reqListener = reqListener;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_item_user,parent,false));
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
        private CustomItemUserBinding binding;
        private UserMD userMD;
        public Holder(@NonNull View itemView) {
            super(itemView);
            binding = CustomItemUserBinding.bind(itemView);
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    reqListener.onClick(userMD);
                }
            });
        }
        private void Bind(UserMD userMD){
            this.userMD = userMD;
            binding.nameTxt.setText(userMD.getUsername());
            binding.emailTxt.setText(userMD.getEmail());
        }
    }
}
