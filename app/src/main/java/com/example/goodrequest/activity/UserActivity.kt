package com.example.goodrequest.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.goodrequest.R
import com.example.goodrequest.databinding.ActivityUserBinding
import com.example.goodrequest.model.viewmodel.UserViewModel
import com.google.android.material.snackbar.Snackbar

class UserActivity: AppCompatActivity() {
    private lateinit var binding: ActivityUserBinding
    private val viewModel: UserViewModel by viewModels()
    companion object {
        private const val KEY_USER_ID = "user_id"
        fun start(activity: Activity, userId: Int) {
            val intent = Intent(activity, UserActivity::class.java)
            intent.putExtra(KEY_USER_ID, userId)
            activity.startActivity(intent)
            activity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        observeLiveData()

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.fetchUser(intent.getIntExtra(KEY_USER_ID,0))
        }
        viewModel.fetchUser(intent.getIntExtra(KEY_USER_ID,0), isDefault = true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = ""
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        this.finish()
        return super.onOptionsItemSelected(item)
    }
    private fun observeLiveData() {
        viewModel.userLiveData.observe(this, { user ->
            binding.avatar
            Glide.with(this).load(user.avatar).error(R.drawable.ic_person).into(binding.avatar)
            binding.name.text = user.fullname
            binding.email.text = user.email
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