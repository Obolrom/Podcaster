package io.obolonsky.crypto.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import coil.decode.DataSource
import coil.imageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import coil.transition.CrossfadeTransition
import coil.transition.Transition
import io.obolonsky.core.di.data.coinpaprika.CoinPaprika
import io.obolonsky.core.di.lazyViewModel
import io.obolonsky.crypto.R
import io.obolonsky.crypto.databinding.FragmentCoinDetailsBinding
import io.obolonsky.crypto.viewmodels.ComponentViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class CoinDetailsFragment : Fragment(R.layout.fragment_coin_details) {

    private val componentViewModel by activityViewModels<ComponentViewModel>()

    private val coinDetailsViewModel by lazyViewModel {
        componentViewModel.cryptoComponent
            .coinDetailViewModelFactory()
            .create(it, requireNotNull(arguments?.getString("id")))
    }

    private val binding by viewBinding<FragmentCoinDetailsBinding>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        coinDetailsViewModel.coinDetails
            .onEach(::onCoinPaprika)
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .launchIn(viewLifecycleOwner.lifecycleScope)

        coinDetailsViewModel.loadDetails()
    }

    private fun onCoinPaprika(coinPaprika: CoinPaprika) {
        val request = ImageRequest.Builder(binding.root.context)
            .data(coinPaprika.logo)
            .transitionFactory { transitionTarget, imageResult ->
                val cross = CrossfadeTransition.Factory(400)

                Transition.Factory { target, result ->
                    cross.create(
                        target = target,
                        result = (result as? SuccessResult)?.takeIf {
                            it.dataSource == DataSource.MEMORY_CACHE
                        }?.run {
                            copy(dataSource = DataSource.MEMORY)
                        } ?: result
                    )
                }.create(transitionTarget, imageResult)
            }
            .target(binding.logo)
            .build()
        context?.imageLoader?.enqueue(request)

        binding.name.text = coinPaprika.name
    }
}