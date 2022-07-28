package io.obolonsky.podcaster.ui

import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Bundle
import android.util.Base64
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import by.kirich1409.viewbindingdelegate.viewBinding
import io.obolonsky.core.di.lazyViewModel
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File

class BookFeedFragment : AbsFragment(R.layout.fragment_book_feed) {

    private val songsViewModel: SongsViewModel by lazyViewModel {
        appComponent.songsViewModel().create(it)
    }

    private val binding: FragmentBookFeedBinding by viewBinding()

    override fun initViewModels() {
        songsViewModel.books
            .onEach {
                (binding.recyclerFeed.adapter as? BookFeedPagingAdapter)
                    ?.apply { submitData(lifecycle, it) }
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
        songsViewModel.loadBooks()

        WorkManager.getInstance(requireActivity().applicationContext)
            .beginWith(
                OneTimeWorkRequestBuilder<TestDiWorker>()
                    .build()
            )
            .then(OneTimeWorkRequestBuilder<AnotherOneWorker>().build())
            .enqueue()

        binding.recordAudio.setOnClickListener {
            recordAudio()
            lifecycleScope.launch(Dispatchers.Default) {
                delay(5000L)
                shouldContinue = false
            }
        }
    }

    private var shouldContinue = true

    private fun recordAudio() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.RECORD_AUDIO
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(requireActivity(),
                    arrayOf(Manifest.permission.RECORD_AUDIO),
                    1234)
            }

            withContext(Dispatchers.IO) {
                Timber.d("Recording permission granted")
                val bufferSize = AudioRecord.getMinBufferSize(
                    44100,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
                )

                val audioBuffer = ByteArray(bufferSize / 2)

                val audioRecord = AudioRecord(
                    MediaRecorder.AudioSource.DEFAULT,
                    44100,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    bufferSize
                )

                if (audioRecord.state != AudioRecord.STATE_INITIALIZED) {
                    Timber.d("Recording audio Record can't initialize!")
                    return@withContext
                }

                audioRecord.startRecording()

                var shortsRead = 0
                while (shouldContinue) {
                    val numberOfShort = audioRecord.read(audioBuffer, 0, audioBuffer.size)
                    shortsRead += numberOfShort
                }

                audioRecord.stop()
                audioRecord.release()

                val encoded = Base64.encodeToString(audioBuffer, Base64.NO_WRAP)
                withContext(Dispatchers.Main) {
                    val file = File(requireContext().filesDir, "sample")
                    val sourceRawAudio = requireContext()
                        .resources
                        .openRawResource(R.raw.clinteastwood_portion_mono)
                        .readBytes()

                    file.createNewFile()
                    file.writeBytes(sourceRawAudio)

                    Timber.d("Recording buffer1: $encoded")
                    songsViewModel.detect(file)
                }


                Timber.d(String.format("Recording stopped. Samples read: %d", shortsRead))
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