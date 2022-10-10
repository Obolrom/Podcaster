package io.obolonsky.crypto.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.commit
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import io.obolonsky.core.di.data.coinpaprika.CoinPaprika
import io.obolonsky.core.di.lazyViewModel
import io.obolonsky.crypto.R
import io.obolonsky.crypto.databinding.ActivityCryptoBinding
import io.obolonsky.crypto.viewmodels.ComponentViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class CryptoActivity : AppCompatActivity() {

    private val componentViewModel by viewModels<ComponentViewModel>()

    private val coinFeedViewModel by lazyViewModel {
        componentViewModel.cryptoComponent
            .coinFeedViewModelFactory()
            .create(it)
    }

    private val binding by viewBinding<ActivityCryptoBinding>()

    private val coinFeedAdapter by lazy {
        CoinFeedAdapter(onClick = ::onCardClick)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crypto)

        binding.coinFeed.apply {
            layoutManager = LinearLayoutManager(this@CryptoActivity)
            adapter = coinFeedAdapter
        }

        coinFeedViewModel.coinFeed
            .onEach { coinFeedAdapter.submitList(it) }
            .flowWithLifecycle(lifecycle)
            .launchIn(lifecycleScope)

        coinFeedViewModel.loadFeed()
    }

    private fun onCardClick(coin: CoinPaprika) = supportFragmentManager.commit {
        replace(
            R.id.fragment_container,
            CoinDetailsFragment::class.java,
            bundleOf("id" to coin.id)
        )
        addToBackStack(null)
    }
}