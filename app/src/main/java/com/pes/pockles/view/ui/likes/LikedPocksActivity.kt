package com.pes.pockles.view.ui.likes

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.binding.BindingViewHolder
import com.mikepenz.fastadapter.diff.FastAdapterDiffUtil
import com.mikepenz.fastadapter.listeners.ClickEventHook
import com.pes.pockles.R
import com.pes.pockles.data.Resource
import com.pes.pockles.databinding.ActivityLikedPocksBinding
import com.pes.pockles.databinding.LikeItemBinding
import com.pes.pockles.model.Pock
import com.pes.pockles.view.ui.base.BaseActivity
import com.pes.pockles.view.ui.likes.item.BindingLikeItem
import com.pes.pockles.view.ui.viewpock.ViewPockActivity

class LikedPocksActivity : BaseActivity() {

    private lateinit var binding: ActivityLikedPocksBinding
    private val viewModel: LikedPocksViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(LikedPocksViewModel::class.java)
    }

    private val itemAdapter = ItemAdapter<BindingLikeItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_liked_pocks)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        val fastAdapter = FastAdapter.with(itemAdapter)
        binding.recycler.apply {
            layoutManager = LinearLayoutManager(this@LikedPocksActivity)
            adapter = fastAdapter
        }

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        fastAdapter.addEventHook(object : ClickEventHook<BindingLikeItem>() {
            override fun onBind(viewHolder: RecyclerView.ViewHolder): View? {
                return viewHolder.asBinding<LikeItemBinding> {
                    it.like
                }
            }

            override fun onClick(
                v: View,
                position: Int,
                fastAdapter: FastAdapter<BindingLikeItem>,
                item: BindingLikeItem
            ) {
                viewModel.removeLike(item.pock.id)
            }
        })

        fastAdapter.addEventHook(object : ClickEventHook<BindingLikeItem>() {
            override fun onBind(viewHolder: RecyclerView.ViewHolder): View? {
                return viewHolder.asBinding<LikeItemBinding> {
                    it.likeItemContainer
                }
            }

            override fun onClick(
                v: View,
                position: Int,
                fastAdapter: FastAdapter<BindingLikeItem>,
                item: BindingLikeItem
            ) {
                val intent = Intent(this@LikedPocksActivity, ViewPockActivity::class.java)
                intent.putExtra("markerId", item.pock.id)
                startActivityForResult(intent, 15)
            }
        })

        viewModel.likedPocks.observe(this, Observer {
            when (it) {
                is Resource.Success<List<Pock>> -> populateData(it.data!!)
            }
        })

        viewModel.refreshInformation()
    }

    inline fun <reified T : ViewBinding> RecyclerView.ViewHolder.asBinding(block: (T) -> View): View? {
        return if (this is BindingViewHolder<*> && this.binding is T) {
            block(this.binding as T)
        } else {
            null
        }
    }

    private fun populateData(data: List<Pock>) {
        val items = data.map { BindingLikeItem(it) }
        val diffs: DiffUtil.DiffResult = FastAdapterDiffUtil.calculateDiff(itemAdapter, items)
        FastAdapterDiffUtil[itemAdapter] = diffs
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 15) {
            viewModel.refreshInformation()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}