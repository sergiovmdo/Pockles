package com.pes.pockles.view.ui.pockshistory

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.binding.BindingViewHolder
import com.mikepenz.fastadapter.listeners.ClickEventHook
import com.pes.pockles.R
import com.pes.pockles.data.Resource
import com.pes.pockles.databinding.ActivityPocksHistoryBinding
import com.pes.pockles.databinding.PockHistoryItemBinding
import com.pes.pockles.model.EditedPock
import com.pes.pockles.model.Pock
import com.pes.pockles.view.ui.base.BaseActivity
import com.pes.pockles.view.ui.editpock.EditPockActivity
import com.pes.pockles.view.ui.pockshistory.item.BindingPockItem
import java.io.Serializable

class PocksHistoryActivity : BaseActivity() {

    private lateinit var binding: ActivityPocksHistoryBinding
    private val viewModel: PocksHistoryViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(PocksHistoryViewModel::class.java)
    }

    // Create the ItemAdapter holding your Items
    private val itemAdapter = ItemAdapter<BindingPockItem>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_pocks_history)
        binding.lifecycleOwner = this
        binding.pocksHistoryViewModel = viewModel

        // Add back-button to toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        val fastAdapter = FastAdapter.with(itemAdapter)
        binding.rvPocksHistory.let {
            it.layoutManager = LinearLayoutManager(this)
            it.adapter = fastAdapter
        }

        fastAdapter.addEventHook(object: ClickEventHook<BindingPockItem>() {
            override fun onBind(viewHolder: RecyclerView.ViewHolder): View? {
                return viewHolder.asBinding<PockHistoryItemBinding> {
                    it.editButton
                }
            }

            override fun onClick(
                v: View,
                position: Int,
                fastAdapter: FastAdapter<BindingPockItem>,
                item: BindingPockItem
            ) {
                val intent = Intent(this@PocksHistoryActivity, EditPockActivity::class.java).apply {
                    putExtra("pockId", item.pock?.id)
                    putExtra("editableContent", EditedPock(item.pock!!.message, item.pock!!.category, item.pock!!.chatAccess, item.pock?.media) as Serializable)
                }
                startActivityForResult(intent, 15)
            }
        })

        initializeObservers()
        viewModel.refreshInformation()
    }

    private fun initializeObservers() {
        viewModel.pocksHistory.observe(
            this,
            Observer { value: Resource<List<Pock>>? ->
                value?.let {
                    when (value) {
                        is Resource.Success<List<Pock>> -> setDataRecyclerView(value.data!!)
                        is Resource.Error -> binding.swipePocksHistory.isRefreshing = false
                    }
                }
            }
        )

        // Action for back-button on toolbar
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        // Add refresh action
        binding.swipePocksHistory.setOnRefreshListener {
            viewModel.refreshInformation()
        }

    }

    private fun setDataRecyclerView(data: List<Pock>) {
        binding.swipePocksHistory.isRefreshing = false

        val pockListBinding: List<BindingPockItem> = data.map { pock ->
            val binding =
                BindingPockItem()
            binding.pock = pock
            binding.showEdit = pock.editable
            binding
        }
        //Fill and set the items to the ItemAdapter
        itemAdapter.setNewList(pockListBinding)

        //Hide progress bar when pocks history showed
        binding.newPockProgressBar.visibility = View.GONE
    }

    inline fun <reified T : ViewBinding> RecyclerView.ViewHolder.asBinding(block: (T) -> View): View? {
        return if (this is BindingViewHolder<*> && this.binding is T) {
            block(this.binding as T)
        } else {
            null
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 15) {
            viewModel.refreshInformation()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

}