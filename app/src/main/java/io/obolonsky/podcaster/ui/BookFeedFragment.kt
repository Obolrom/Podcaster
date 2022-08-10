package io.obolonsky.podcaster.ui

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import io.obolonsky.core.di.lazyViewModel
import io.obolonsky.podcaster.PodcasterApp
import io.obolonsky.podcaster.R
import io.obolonsky.podcaster.background.AnotherOneWorker
import io.obolonsky.podcaster.background.TestDiWorker
import io.obolonsky.podcaster.data.room.entities.Book
import io.obolonsky.podcaster.databinding.FragmentBookFeedBinding
import io.obolonsky.podcaster.misc.appComponent
import io.obolonsky.podcaster.misc.launchWhenStarted
import io.obolonsky.podcaster.ui.adapters.BookFeedPagingAdapter
import io.obolonsky.podcaster.ui.adapters.OffsetItemDecorator
import io.obolonsky.podcaster.viewmodels.SongsViewModel
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class BookFeedFragment : AbsFragment(R.layout.fragment_book_feed) {

    private val songsViewModel: SongsViewModel by lazyViewModel {
        appComponent.songsViewModel().create(it)
    }

    @Inject
    lateinit var player: SimpleExoPlayer

    private val binding: FragmentBookFeedBinding by viewBinding()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as PodcasterApp).appComponent.inject(this)
    }

    override fun initViewModels() {
        songsViewModel.books
            .onEach {
                (binding.recyclerFeed.adapter as? BookFeedPagingAdapter)
                    ?.apply { submitData(lifecycle, it) }
            }
            .launchWhenStarted(lifecycleScope)

        songsViewModel.shazamDetect
            .onEach {
                player.prepare()
                player.playWhenReady = true
                it.track?.audioUri?.let { audioUri ->
                    player.setMediaItem(MediaItem.fromUri(audioUri))
                }
            }
            .launchWhenStarted(lifecycleScope)
    }

    override fun initViews(savedInstanceState: Bundle?) {
        binding.recyclerFeed.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = BookFeedPagingAdapter(onClick = ::onBookClicked)

            addItemDecoration(
                OffsetItemDecorator(resources.getDimensionPixelOffset(R.dimen.big_margin))
            )
        }
//        songsViewModel.loadBooks()

        WorkManager.getInstance(requireActivity().applicationContext)
            .beginWith(
                OneTimeWorkRequestBuilder<TestDiWorker>()
                    .build()
            )
            .then(OneTimeWorkRequestBuilder<AnotherOneWorker>().build())
            .enqueue()

        binding.stopRecording.setOnClickListener {
            val recordIntent = Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION)
            startActivityForResult(recordIntent, 0)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0) {
            data?.data?.let { uri ->
                player.prepare()
                player.playWhenReady = true
                player.setMediaItem(MediaItem.fromUri(uri))
                viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                    val bytes = requireContext().contentResolver
                        ?.openInputStream(uri)
                        ?.readBytes()

                    bytes?.let(File(requireContext().filesDir, "fileToDetect.mp3")::writeBytes)
                    val file = File(requireContext().filesDir, "fileToDetect.mp3")
                    songsViewModel.audioDetect(file)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            1234 -> {
                if (grantResults.isNotEmpty() && grantResults.first() == PackageManager.PERMISSION_GRANTED)
                    0
                else
                    1
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun onBookClicked(book: Book) {
        Timber.d("onBookClicked book: ${book.title}")
        val action = BookFeedFragmentDirections
            .actionBookFeedFragmentToBookDetailsFragment(book.id)
        findNavController().navigate(action)
    }
}