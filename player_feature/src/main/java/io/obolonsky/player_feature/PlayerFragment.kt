package io.obolonsky.player_feature

import android.content.Context
import android.os.Bundle
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import by.kirich1409.viewbindingdelegate.viewBinding
import io.obolonsky.core.di.depsproviders.App
import io.obolonsky.coreui.BaseFragment
import io.obolonsky.player_feature.databinding.FragmentPlayerBinding
import io.obolonsky.player_feature.databinding.FragmentPlayerNavigationBinding
import io.obolonsky.player_feature.di.DaggerPlayerComponent
import javax.inject.Inject

class PlayerFragment : BaseFragment(R.layout.fragment_player) {

    @Inject
    lateinit var player: ExoPlayer

    private val binding by viewBinding<FragmentPlayerBinding>()
    private val playerNavBinding: FragmentPlayerNavigationBinding by
        viewBinding(viewBindingRootId = R.id.player_navigation)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        DaggerPlayerComponent.factory()
            .create((context.applicationContext as App).getAppComponent())
            .inject(this)
    }

    override fun initViewModels() { }

    override fun initViews(savedInstanceState: Bundle?) {
        binding.playerView.player = player

        player.prepare()
        player.setMediaItem(
            MediaItem.fromUri("https://github.com/Obolrom/MusicLibrary/blob/master/rhcp_californication/red-hot-chili-peppers-californication.mp3?raw=true".toUri())
        )
        player.play()
    }
}