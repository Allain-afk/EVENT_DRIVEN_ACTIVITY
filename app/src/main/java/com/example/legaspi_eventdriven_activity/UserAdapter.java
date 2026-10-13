package com.example.legaspi_eventdriven_activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.legaspi_eventdriven_activity.network.User;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> userList = new ArrayList<>();
    private List<String> sourceList = new ArrayList<>();

    public void setUsers(List<User> users, List<String> sources) {
        this.userList = users;
        this.sourceList = sources;
        notifyDataSetChanged();
    }
    
    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        String source = sourceList.get(position);
        
        String fullName = user.getFirstName() + " " + user.getLastName();
        holder.tvUserName.setText(fullName.trim());
        holder.tvUserEmail.setText(user.getEmail());
        holder.tvUserSource.setText(source);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName, tvUserEmail, tvUserSource;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvUserEmail = itemView.findViewById(R.id.tvUserEmail);
            tvUserSource = itemView.findViewById(R.id.tvUserSource);
        }
    }
}
