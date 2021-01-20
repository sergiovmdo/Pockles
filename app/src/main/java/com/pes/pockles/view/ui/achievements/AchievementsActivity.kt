package com.pes.pockles.view.ui.achievements

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.pes.pockles.R
import com.pes.pockles.data.Resource
import com.pes.pockles.databinding.ActivityAchievementsBinding
import com.pes.pockles.model.Achievement
import com.pes.pockles.view.ui.achievements.item.BindingAchievementItem
import com.pes.pockles.view.ui.base.BaseActivity


class AchievementsActivity : BaseActivity() {

    private lateinit var binding: ActivityAchievementsBinding

    private val viewModel: AchievementsViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(AchievementsViewModel::class.java)
    }

    private val itemAdapter = ItemAdapter<BindingAchievementItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_achievements)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        val fastAdapter = FastAdapter.with(itemAdapter)
        binding.recycler.apply {
            layoutManager = LinearLayoutManager(this@AchievementsActivity)
            adapter = fastAdapter
        }

        viewModel.achievement.observe(this, Observer {
            when (it) {
                is Resource.Success<List<Achievement>> -> populateData(it.data!!)
            }
        })

        viewModel.refreshAchievements()
    }

    private fun populateData(data: List<Achievement>) {
        itemAdapter.setNewList(data.map { BindingAchievementItem(it) })
    }


}