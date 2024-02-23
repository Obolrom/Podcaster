package io.obolonsky.podcaster.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import by.kirich1409.viewbindingdelegate.viewBinding
import io.obolonsky.chat.ChatActivity
import io.obolonsky.core.di.actions.*
import io.obolonsky.github.ui.GitHubActivity
import io.obolonsky.podcaster.MyPrefs
import io.obolonsky.podcaster.PodcasterApp
import io.obolonsky.podcaster.R
import io.obolonsky.podcaster.databinding.ActivityMainFakeBinding
import io.obolonsky.podcaster.misc.MyPrefsSerializer
import io.obolonsky.quizzy.ui.QuizActivity
import io.obolonsky.utils.get
import javax.inject.Inject
import javax.inject.Provider

class MainActivity : AppCompatActivity() {

    @Inject
    internal lateinit var goToShazamAction: Provider<GoToShazamAction>

    @Inject
    internal lateinit var goToSpaceXAction: Provider<GoToSpaceXAction>

    @Inject
    internal lateinit var goToNasaAction: Provider<GoToNasaAction>

    @Inject
    internal lateinit var goToDownloadsAction: Provider<NavigateToDownloadsAction>

    @Inject
    internal lateinit var goToCryptoAction: Provider<GoToCryptoAction>

    private val preferences: DataStore<MyPrefs> by dataStore(
        fileName = "PodcasterPreferences",
        serializer = MyPrefsSerializer(),
    )

    private val binding: ActivityMainFakeBinding by viewBinding()

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as PodcasterApp).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_fake)

//        startActivity(Intent(this, QuizActivity::class.java))

        binding.goToGithub.setOnClickListener {
            startActivity(Intent(this, GitHubActivity::class.java))
        }

        binding.goToShazam.setOnClickListener {
            goToShazamAction.get {
                navigate(this@MainActivity)
            }
        }

        binding.goToQuizzy.setOnClickListener {
            startActivity(Intent(this, QuizActivity::class.java))
        }

        binding.goToSpaceX.setOnClickListener {
            goToSpaceXAction.get {
                navigate(this@MainActivity)
            }
        }

        binding.goToNasa.setOnClickListener {
            goToNasaAction.get {
                navigate(this@MainActivity)
            }
        }

        binding.goToDownloads.setOnClickListener {
            goToDownloadsAction.get {
                navigate(this@MainActivity)
            }
        }

        binding.goToCrypto.setOnClickListener {
            goToCryptoAction.get {
                navigate(this@MainActivity)
            }
        }

        binding.goToChat.setOnClickListener {
            startActivity(Intent(this, ChatActivity::class.java))
        }
    }
}