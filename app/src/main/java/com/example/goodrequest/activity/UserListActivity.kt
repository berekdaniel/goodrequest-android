package com.example.goodrequest.activity

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.goodrequest.R
import com.example.goodrequest.adapters.UserListAdapter
import com.example.goodrequest.databinding.ActivityUserListBinding
import com.example.goodrequest.model.viewmodel.UserListViewModel
import com.google.android.material.snackbar.Snackbar


class UserListActivity : AppCompatActivity(){
    private lateinit var binding: ActivityUserListBinding
    private lateinit var adapter: UserListAdapter
    private val viewModel: UserListViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        observeLiveData()
        adapter = UserListAdapter(this, resources.configuration.orientation) { userId ->
            UserActivity.start(this, userId)
        }

        val lm  = LinearLayoutManager(this, if( resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) RecyclerView.HORIZONTAL else RecyclerView.VERTICAL, false)
        binding.recyclerView.layoutManager = lm
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val lastVisible =lm.findLastVisibleItemPosition()
                if(lastVisible == adapter.items.size-1)
                    viewModel.fetchUsers()
            }
        })
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.fetchUsers(isRefresing = true)
        }
        viewModel.fetchUsers(isDefault = true)
    }

    private fun observeLiveData() {
        viewModel.usersLiveData.observe(this, {
            it?.let {
                adapter.items = it.toMutableList()
            }
        })

        viewModel.loadingLiveData.observe(this, {
            if (!it) {
                binding.swipeRefresh.isRefreshing = false
            }
            binding.progressBar.visibility =
                if (it && !binding.swipeRefresh.isRefreshing) View.VISIBLE else View.GONE

        })

        viewModel.throwableLiveData.observe(this, {
            it?.let {
                val snackbar = Snackbar
                    .make(binding.root, resources.getText(R.string.error_occured_text), Snackbar.LENGTH_INDEFINITE)
                    .setAction(resources.getText(R.string.retry_button)) {
                        viewModel.retrySubject.onNext(null)
                        viewModel.resetThrowable()
                    }

                snackbar.show()
            }

        })
    }
}