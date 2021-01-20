package com.pes.pockles.view.ui.chat.item

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.pes.pockles.R
import com.pes.pockles.model.Message
import com.pes.pockles.util.TimeUtils
import kotlinx.android.synthetic.main.my_message.view.*
import kotlinx.android.synthetic.main.other_message.view.*


private const val VIEW_TYPE_MY_MESSAGE = 1
private const val VIEW_TYPE_OTHER_MESSAGE = 2

class MessageAdapter(val context: Context) : RecyclerView.Adapter<MessageViewHolder>() {

    private var messages: List<Message> = ArrayList()

    fun setMessages(messages: List<Message>) {
        this.messages = messages
        this.notifyDataSetChanged()
    }

    inner class MyMessageViewHolder(view: View) : MessageViewHolder(view) {
        private var messageText: TextView = view.txtMyMessage
        private var timeText: TextView = view.txtMyMessageTime
//        txtMsgFrom.setText(Html.fromHtml(convertToHtml(txtMsgFrom.getText().toString()) + " &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;"))

        override fun bind(message: Message) {
            messageText.text = message.text
            timeText.text = TimeUtils.getMessageTime(message)
        }
    }

    inner class OtherMessageViewHolder(view: View) : MessageViewHolder(view) {
        private var messageText: TextView = view.txtOtherMessage
        private var timeText: TextView = view.txtOtherMessageTime

        override fun bind(message: Message) {
            messageText.text = message.text
            timeText.text = TimeUtils.getMessageTime(message)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val message = messages[position]

        return if (message.senderId == FirebaseAuth.getInstance().currentUser?.uid) {
            VIEW_TYPE_MY_MESSAGE
        } else {
            VIEW_TYPE_OTHER_MESSAGE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return if (viewType == VIEW_TYPE_MY_MESSAGE) {
            MyMessageViewHolder(
                LayoutInflater.from(context).inflate(R.layout.my_message, parent, false)
            )
        } else {
            OtherMessageViewHolder(
                LayoutInflater.from(context).inflate(R.layout.other_message, parent, false)
            )
        }
    }

    override fun getItemCount(): Int {
        return messages.count()
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        holder?.bind(message)
    }

}

open class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    open fun bind(message: Message) {}
}