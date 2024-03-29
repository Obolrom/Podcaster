package io.obolonsky.crypto.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import coil.imageLoader
import coil.request.ImageRequest
import io.obolonsky.core.di.data.coinpaprika.CoinPaprika
import io.obolonsky.core.di.lazyViewModel
import io.obolonsky.coreui.utils.CrossFadeTransitionFactory
import io.obolonsky.crypto.R
import io.obolonsky.crypto.databinding.FragmentCoinDetailsBinding
import io.obolonsky.crypto.viewmodels.CoinDetailsViewModel.Companion.ID_KEY
import io.obolonsky.crypto.viewmodels.ComponentViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class CoinDetailsFragment : Fragment(R.layout.fragment_coin_details) {

    private val componentViewModel by activityViewModels<ComponentViewModel>()

    private val coinDetailsViewModel by lazyViewModel {
        it.set(ID_KEY, requireNotNull(arguments?.getString("id")))

        componentViewModel.cryptoComponent
            .coinDetailViewModelFactory()
            .create(it)
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
            .transitionFactory(CrossFadeTransitionFactory())
            .target(binding.logo)
            .build()
        context?.imageLoader?.enqueue(request)

        binding.name.text = coinPaprika.name

        coinPaprika.description?.let { binding.description.text = it }

        binding.rank.text = coinPaprika.rank.toString()
    }
}