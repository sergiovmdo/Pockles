package com.pes.pockles.view.ui.notifications


import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.pes.pockles.R
import com.pes.pockles.data.Resource
import com.pes.pockles.databinding.FragmentNotificationsBinding
import com.pes.pockles.model.Notification
import com.pes.pockles.view.ui.base.BaseFragment

/**
 * A simple [Fragment] subclass.
 */
class NotificationsFragment : BaseFragment<FragmentNotificationsBinding>() {

    private val viewModel: NotificationsViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(NotificationsViewModel::class.java)
    }

    private val itemAdapter = ItemAdapter<BindingNotificationItem>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        val fastAdapter = FastAdapter.with(itemAdapter)
        binding.recycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = fastAdapter
        }

        viewModel.notifications.observe(this, Observer {
            when (it) {
                is Resource.Success<List<Notification>> -> populateData(it.data!!)
            }
        })

        viewModel.refreshNotifications()
    }

    private fun populateData(data: List<Notification>) {
        val items = data.map { BindingNotificationItem(it) }
        itemAdapter.setNewList(items)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 15) {
            viewModel.refreshNotifications()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun getLayout(): Int {
       return R.layout.fragment_notifications
    }


}
