package io.obolonsky.podcaster.ui

import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.work.*
import by.kirich1409.viewbindingdelegate.viewBinding
import coil.load
import dagger.hilt.android.AndroidEntryPoint
import io.obolonsky.podcaster.R
import io.obolonsky.podcaster.background.StudyService
import io.obolonsky.podcaster.background.StudyWorker
import io.obolonsky.podcaster.data.misc.toaster
import io.obolonsky.podcaster.data.room.StatefulData
import io.obolonsky.podcaster.data.room.entities.Book
import io.obolonsky.podcaster.databinding.FragmentBookDetailsBinding
import io.obolonsky.podcaster.misc.NetworkBroadcastReceiver
import io.obolonsky.podcaster.misc.launchWhenStarted
import io.obolonsky.podcaster.viewmodels.SongsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.concurrent.timerTask

@AndroidEntryPoint
class BookDetailsFragment : AbsFragment(R.layout.fragment_book_details) {

    private val songsViewModel: SongsViewModel by activityViewModels()

    private val binding: FragmentBookDetailsBinding by viewBinding()

    private val args: BookDetailsFragmentArgs by navArgs()

    private val toaster by toaster()

    private val networkBroadcastReceiver by lazy {
        NetworkBroadcastReceiver(::onNetworkConnectionChanged)
    }

    override fun initViewModels() {
        songsViewModel.book
            .onEach { statefulData ->
                when (statefulData) {
                    is StatefulData.Success -> {
                        statefulData.data?.let { onBook(it) }
                        handleLoading(false)
                    }

                    is StatefulData.Error -> {
                        handleLoading(false)
                        toaster.showToast(requireContext(), "error")
                    }

                    is StatefulData.Loading -> {
//                        handleLoading(true)
                    }
                }
            }
            .launchWhenStarted(lifecycleScope)
    }

    override fun initViews(savedInstanceState: Bundle?) {
        songsViewModel.loadBook(args.detailsBookId)

      /*  val serviceIntent = Intent(requireContext(), StudyService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context?.startForegroundService(serviceIntent).also {
                Timber.d("studyService startService ${it?.shortClassName}")
            }
        } else {
            context?.startService(serviceIntent)
        }*/

        val taskA = OneTimeWorkRequestBuilder<StudyWorker>().setId("A")
        val taskB = OneTimeWorkRequestBuilder<StudyWorker>().setId("B")
        val taskC = OneTimeWorkRequestBuilder<StudyWorker>().setId("C")

        val workManager = WorkManager.getInstance(requireContext())
        val continuationA = workManager.beginWith(taskA.build())
        val continuationB = workManager.beginWith(taskB.build())
        val continuationFinal = WorkContinuation.combine(
            listOf(continuationA, continuationB)
        )/*.then(taskC.build())*/
        continuationFinal.enqueue()

        binding.listen.setOnClickListener {
            findNavController()
                .navigate(R.id.action_bookDetailsFragment_to_newPlayerFragment)
        }
    }

    private fun OneTimeWorkRequest.Builder.setId(id: String) = this.setInputData(
        workDataOf("id" to id)
    )

    private fun onBook(book: Book) {
        binding.bookBanner.load(book.imageUrl) {
            crossfade(500)
        }

        binding.bookTitle.text = book.title

        book.bookAuthor?.let { binding.bookAuthor.text = it.fullName }

        binding.bookRatingBar.rating = book.raiting.toFloat()

        binding.bookRatingNumber.text = String.format("%.1f", book.raiting.toFloat())

        binding.bookRatedByUsers.text = getString(R.string.details_rated_by_users, book.auditions)

        binding.bookDescription.text = book.description

        binding.totalDuration.text = getString(
            R.string.book_total_duration,
            book.duration?.div(TimeUnit.MINUTES.toMillis(1))
        )

        binding.bookMemorySize.text = "15 MB"
    }

    override fun onStart() {
        super.onStart()
        context?.let { networkBroadcastReceiver.registerReceiver(it) }
    }

    override fun onStop() {
        super.onStop()
        context?.let { networkBroadcastReceiver.unregisterReceiver(it) }
    }

    private fun onNetworkConnectionChanged(isConnected: Boolean) {
        if (isConnected) {
            songsViewModel.loadBook(args.detailsBookId)
        } else {
            toaster.showToast(requireContext(), "Network disconnected")
        }
    }
}