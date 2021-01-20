package com.pes.pockles.data.messaging

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.pes.pockles.R
import com.pes.pockles.data.repository.RepositoryProvider
import com.pes.pockles.view.ui.MainActivity
import dagger.android.AndroidInjection
import javax.inject.Inject

class PocklesMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var repositoryProvider: RepositoryProvider

    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()
    }

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String) {
        //Send token to PAPI
        FirebaseAuth.getInstance()
            .currentUser?.let { repositoryProvider.userRepository.insertFCMToken(token) }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val notificationEnum: NotificationEnum =
            mapAPIMessageTypeToNotificationType(remoteMessage.data["category"]?.toInt())
        /*In some cases this method will do nothing, it is added because in some yes,
        * and maybe in the future we want to add functionality at receiving this notifications
        * that actually we don't want an special treatment on it*/
        val handled = notificationEnum.onMessageReceived(
            repositoryProvider,
            remoteMessage.data["body"],
            remoteMessage.data["title"],
            remoteMessage.data
        )
        if (!handled) {
            sendNotification(remoteMessage.data["body"], remoteMessage.data["title"])
        }
    }

    /*It must be overridden although it is empty*/
    override fun onDeletedMessages() {

    }

    private fun sendNotification(messageBody: String?, title: String?) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT
        )

        val channelId = "PocklesChannel"
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_pock_icon)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(
                    channelId,
                    "PocklesChannel",
                    NotificationManager.IMPORTANCE_DEFAULT
                )

            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
    }

    private fun mapAPIMessageTypeToNotificationType(messageType: Int?): NotificationEnum {
        when (messageType) {
            0 -> return NotificationEnum.CHAT
            1 -> return NotificationEnum.TRENDING
            2 -> return NotificationEnum.REPORTS
            3 -> return NotificationEnum.ACHIEVEMENT
            4 -> return NotificationEnum.BAN
        }
        return NotificationEnum.DEFAULT
    }

}