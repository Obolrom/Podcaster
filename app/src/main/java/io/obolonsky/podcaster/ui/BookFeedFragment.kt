package io.obolonsky.podcaster.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.*
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import androidx.core.net.toFile
import androidx.core.net.toUri
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.ObjectOutputStream
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

        binding.recordAudio.setOnClickListener {
            recordAudio()
            lifecycleScope.launch(Dispatchers.Default) {
                delay(5000L)
                shouldContinue = false
            }
        }

        binding.stopRecording.setOnClickListener {
            val recordIntent = Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION)
            startActivityForResult(recordIntent, 0)
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

                val audioBuffer = ShortArray(bufferSize)

                val audioRecord = AudioRecord(
                    MediaRecorder.AudioSource.MIC,
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

//                val encoded = Base64.encodeToString(audioBuffer, Base64.NO_WRAP)
                withContext(Dispatchers.Main) {
                    val file = File(requireContext().filesDir, "sample")
                    val file2 = File(requireContext().filesDir, "wtf")
                    val sourceRawAudio = requireContext()
                        .resources
                        .openRawResource(R.raw.clinteastwood_portion_mono)
                        .readBytes()

                    file.createNewFile()
                    file.writeBytes(sourceRawAudio)


                    ObjectOutputStream(FileOutputStream(file2)).use {
                        it.writeObject(audioBuffer)
                    }

//                    Timber.d("Recording buffer1: $encoded")
//                    songsViewModel.detect(file)
                }


                Timber.d(String.format("Recording stopped. Samples read: %d", shortsRead))
            }
        }
    }

    private fun playAudio() {
//        ObjectInputStream(FileInputStream(File(""))).use {
//            it.read
//        }

        val bufferSize = AudioTrack.getMinBufferSize(
            44100,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )

        val audioTrack = AudioTrack(
            AudioManager.STREAM_MUSIC,
            44100,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize,
            AudioTrack.MODE_STREAM
        )


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

                    bytes?.let(File(requireContext().filesDir, "bitch.wav")::writeBytes)
                    val file = File(requireContext().filesDir, "fuck.mp3") // File(requireContext().filesDir, "bitch.wav")
                    songsViewModel.recognize(file)
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