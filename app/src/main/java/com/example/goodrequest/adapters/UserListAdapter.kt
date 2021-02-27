package com.example.goodrequest.adapters

import android.content.Context
import android.content.res.Configuration
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.goodrequest.databinding.ViewUserListBinding
import com.example.goodrequest.model.response.UserData

class UserListAdapter(val context: Context, val orientation: Int, val userClicked:(Int) -> Unit): RecyclerView.Adapter<UserListAdapter.UserListViewHolder>(){

    class UserListViewHolder(val binding: ViewUserListBinding) : RecyclerView.ViewHolder(binding.root)

    var items: MutableList<UserData> = mutableListOf()
    set(value) {
        synchronized(items) {
            items.clear()
            items.addAll(value)
            notifyDataSetChanged()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserListViewHolder {
        return UserListViewHolder(ViewUserListBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: UserListViewHolder, position: Int) {
        val user = items[position]
        Glide.with(context).load(user.avatar).into(holder.binding.avatar)
        if(orientation == Configuration.ORIENTATION_PORTRAIT)
            holder.binding.name.text = user.fullname
        else {
            holder.binding.name.text = user.firstName
            holder.binding.lastName?.text = user.lastName
        }
        holder.binding.root.setOnClickListener {
            user.id?.let { userId ->
                userClicked.invoke(userId)
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}

