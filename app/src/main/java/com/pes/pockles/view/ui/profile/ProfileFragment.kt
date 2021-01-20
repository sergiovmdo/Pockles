package com.pes.pockles.view.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ScrollView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.pes.pockles.R
import com.pes.pockles.databinding.FragmentProfileBinding
import com.pes.pockles.model.EditedUser
import com.pes.pockles.util.livedata.EventObserver
import com.pes.pockles.view.ui.base.BaseFragment
import com.pes.pockles.view.ui.editprofile.EditProfileActivity
import com.pes.pockles.view.ui.login.LaunchActivity
import java.io.Serializable

class ProfileFragment : BaseFragment<FragmentProfileBinding>() {

    private var shouldRefreshOnResume = false

    private val viewModel: ProfileViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(ProfileViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewModel = viewModel

        viewModel.user.observe(this, Observer {
            it?.let { user ->
                Glide.with(this)
                    .load(user.profileImage)
                    .into(binding.profileImage)
                viewModel.userdata = user
                binding.user = user
            }
        })

        viewModel.navigateToHistory.observe(
            this,
            EventObserver(::navigateToHistory)
        )

        viewModel.navigateToLogin.observe(this, EventObserver(::navigateToLogin))

        binding.settingsButton.setOnClickListener {
            findNavController().navigate(R.id.action_userProfileFragment_to_settingsActivity)
        }

        binding.likeButton.setOnClickListener {
            findNavController().navigate(R.id.action_userProfileFragment_to_likedPocksActivity)
        }

        binding.editProfileButton.setOnClickListener {
            val intent = Intent(it.context, EditProfileActivity::class.java).apply {
                putExtra("mail", viewModel.userdata.mail)
                putExtra("birthDate", viewModel.userdata.birthDate)
                putExtra(
                    "editableContent",
                    EditedUser(
                        viewModel.userdata.name,
                        viewModel.userdata.profileImage,
                        viewModel.userdata.radiusVisibility,
                        viewModel.userdata.accentColor
                    ) as Serializable
                )
            }
            it.context.startActivity(intent)
        }

        binding.cardView2.setOnClickListener {
            findNavController().navigate(R.id.action_userProfileFragment_to_achievementsActivity)
        }

    }

    private fun navigateToHistory(bool: Boolean) {
        if (bool) {
            findNavController().navigate(R.id.action_userProfileFragment_to_pocksHistoryActivity)
        }
    }

    private fun navigateToLogin(bool: Boolean) {
        if (bool) {
            startActivity(Intent(activity!!, LaunchActivity::class.java))
            activity!!.finish() // to prevent that back button on LaunchActivity goes to MainActivity
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_profile
    }

    override fun onResume() {
        super.onResume()
        // Check should we need to refresh the fragment
        if (shouldRefreshOnResume) {
            binding.profileScrollView.fullScroll(ScrollView.FOCUS_UP);
            viewModel.refresh()
        }
    }

    override fun onStop() {
        super.onStop()
        shouldRefreshOnResume = true
    }
}