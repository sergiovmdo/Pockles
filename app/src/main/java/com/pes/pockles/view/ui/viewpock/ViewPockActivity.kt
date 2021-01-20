package com.pes.pockles.view.ui.viewpock

import android.content.Intent
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.auth.FirebaseAuth
import com.pes.pockles.R
import com.pes.pockles.data.Resource
import com.pes.pockles.databinding.ViewPockBinding
import com.pes.pockles.model.ChatData
import com.pes.pockles.model.Pock
import com.pes.pockles.util.TimeUtils
import com.pes.pockles.view.ui.base.BaseActivity
import com.pes.pockles.view.ui.chat.ChatActivity
import com.pes.pockles.view.ui.viewuser.ViewUserActivity


class ViewPockActivity : BaseActivity() {

    private lateinit var binding: ViewPockBinding
    private val viewModel: ViewPockViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(ViewPockViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val pockId: String? = intent.extras?.getString("markerId")

        if (pockId == null) {
            finish()
        }

        setUpWindow()

        binding = DataBindingUtil.setContentView(this, R.layout.view_pock)

        binding.pockViewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.loadPock(pockId!!)

        binding.back.setOnClickListener {
            onBackPressed()
        }

        binding.chat.setOnClickListener {
            goChat(pockId)
        }

        binding.share.setOnClickListener {
            sharePock()
        }

        binding.report.setOnClickListener {
            goReport()
        }

        binding.username.setOnClickListener {
            val intent = Intent(it.context, ViewUserActivity::class.java).apply {
                putExtra("userId", viewModel.pock.value?.data?.user)
            }
            it.context.startActivity(intent)
        }

        initializeObservers()
    }

    private fun initializeObservers() {
        viewModel.pock.observe(this, Observer {
            it?.let {
                when (it) {
                    is Resource.Success<Pock> -> hideUnnecessaryButtons()
                }
            }
        }
        )
    }

    private fun hideUnnecessaryButtons() {
        val user = FirebaseAuth.getInstance().currentUser
        if (!viewModel.getPock()!!.chatAccess || (user != null && viewModel.getPock()!!.user == user.uid)) {
            binding.chat.visibility = View.GONE
        }

        if (user != null && viewModel.getPock()!!.user == user.uid) {
            binding.report.visibility = View.GONE
        }
    }

    private fun sharePock() {
        val shareText =
            "${viewModel.getPock()?.username} ${resources.getString(R.string.has_published_share_text)} ${TimeUtils.getPockTime(
                viewModel.getPock()!!
            )}:\n" +
                    "${viewModel.getPock()?.message}\n" +
                    "[${resources.getString(R.string.category_hint)}: ${viewModel.getPock()?.category}]\n" +
                    resources.getString(R.string.shared_from_pockles)
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.setType("text/plain").putExtra(Intent.EXTRA_TEXT, shareText)
        startActivity(shareIntent)
    }

    private fun goReport() {
        basicAlert()
    }

    private fun goChat(pockId: String) {
        val intent = Intent(this, ChatActivity::class.java).apply {
            putExtra(
                "chatData",
                ChatData(
                    null,
                    pockId,
                    viewModel.pock.value?.data!!.username,
                    viewModel.pock.value?.data!!.userProfileImage
                )
            )
            putExtra("userId", viewModel.pock.value?.data?.user)
        }
        startActivity(intent)

    }


    private fun setUpWindow() {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_DIM_BEHIND,
            WindowManager.LayoutParams.FLAG_DIM_BEHIND
        )
        val params = window.attributes
        params.alpha = 1.0f // lower than one makes it more transparent
        params.dimAmount = .6f
        window.attributes = params
        window.setBackgroundDrawable(
            ColorDrawable(
                ContextCompat.getColor(
                    this,
                    android.R.color.transparent
                )
            )
        )
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val width: Int = size.x
        val height: Int = size.y
        if (height > width) {
            window.setLayout((width * .9).toInt(), (height * .9).toInt())
        } else {
            window.setLayout((width * .7).toInt(), (height * .8).toInt())
        }
    }

    //Alert for displaying the user agreement to report the pock
    private fun basicAlert() {
        let {
            val builder = AlertDialog.Builder(it)
            builder.setMessage(R.string.alertMessageReport)?.setTitle(R.string.alertTitleReport)
            builder.apply {
                setPositiveButton(
                    R.string.alertOK
                ) { _, _ ->
                    choiceAlert()
                    // User clicked OK button
                }
                setNegativeButton(
                    R.string.alertNO
                ) { dialog, _ ->
                    // User cancelled the dialog, it simply closes it
                    dialog.dismiss()
                }

            }
            builder.create()
        }.show()
    }

    private fun choiceAlert() {
        // setup the alert builder
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.alertTitleMotivo)
        // add a radio button list
        val options = R.array.motivos
        var checkedItem = 1 // default
        builder.setSingleChoiceItems(
            options,
            checkedItem
        ) { _, which ->
            checkedItem = which
        }
        // add OK and Cancel buttons
        builder.setPositiveButton(
            R.string.alertOK
        ) { _, _ ->
            okReport(checkedItem)
        }
        builder.setNegativeButton(R.string.alertNO, null)
        // create and show the alert dialog
        val dialog = builder.create()
        dialog.show()
    }

    private fun okReport(which: Int) {
        val bigArray = resources.getStringArray(R.array.motivos)
        val motive = bigArray[which]
        viewModel.report(motive)
    }

}