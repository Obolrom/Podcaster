package io.obolonsky.chat

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import by.kirich1409.viewbindingdelegate.viewBinding
import io.obolonsky.chat.databinding.ActivityChatBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttp
import okhttp3.WebSocket
import timber.log.Timber
import ua.naiksoftware.stomp.Stomp
import kotlin.random.Random

class ChatActivity : AppCompatActivity() {

    private val stompClient by lazy {
        Stomp.over(Stomp.ConnectionProvider.OKHTTP, WEB_SOCKET_ENDPOINT)
    }

    private val binding: ActivityChatBinding by viewBinding()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        stompClient.connect()

        binding.sendMessage.setOnClickListener {
            sendMessage(binding.messageInput.text.toString())
        }
    }

    override fun onStart() {
        super.onStart()

        stompClient.topic("/topic/greetings")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    binding.messages.text = binding.messages.text.toString() + "\n" + it.payload
                    Timber.tag(ChatActivity.javaClass.simpleName).d("message: ${it.payload}")
                },
                Timber::e,
                { Timber.tag(ChatActivity.javaClass.simpleName).d("topic complete") }
            )
    }

    private fun sendMessage(text: String) {
        if (text.isBlank()) return

        stompClient.send(
            "/app/hello",
            "{\"message\": \"$text\"}")
            .subscribe(
                {
                    Timber.tag(ChatActivity.javaClass.simpleName).d("sent message")
                },
                Timber::e
            )
    }

    override fun onDestroy() {
        super.onDestroy()
        stompClient.disconnect()
    }

    companion object {
        private const val WEB_SOCKET_ENDPOINT = "ws://192.168.0.106:8081/gs-guide-websocket"
    }
}