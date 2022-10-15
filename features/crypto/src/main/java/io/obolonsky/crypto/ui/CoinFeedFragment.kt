package io.obolonsky.crypto.ui

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import io.obolonsky.core.di.data.coinpaprika.CoinPaprika
import io.obolonsky.core.di.lazyViewModel
import io.obolonsky.crypto.R
import io.obolonsky.crypto.databinding.FragmentCryptoFeedBinding
import io.obolonsky.crypto.viewmodels.ComponentViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class CoinFeedFragment : Fragment(R.layout.fragment_crypto_feed) {

    private val componentViewModel by activityViewModels<ComponentViewModel>()

    private val coinFeedViewModel by lazyViewModel {
        componentViewModel.cryptoComponent
            .coinFeedViewModelFactory()
            .create(it)
    }

    private val coinFeedAdapter by lazy {
        CoinFeedAdapter(onClick = ::onCardClick)
    }

    private val binding by viewBinding<FragmentCryptoFeedBinding>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.coinFeed.apply {
            layoutManager = LinearLayoutManager(view.context)
            adapter = coinFeedAdapter
        }

        coinFeedViewModel.coinFeed
            .onEach { coinFeedAdapter.submitList(it) }
            .flowWithLifecycle(lifecycle)
            .launchIn(lifecycleScope)

        coinFeedViewModel.loadFeed()
    }

    private fun onCardClick(coin: CoinPaprika) = parentFragmentManager.commit {
        replace(
            R.id.fragment_container,
            CoinDetailsFragment::class.java,
            bundleOf("id" to coin.id)
        )
        addToBackStack(null)
    }
}