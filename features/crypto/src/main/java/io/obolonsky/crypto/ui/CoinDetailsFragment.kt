package io.obolonsky.crypto.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import io.obolonsky.core.di.lazyViewModel
import io.obolonsky.crypto.R
import io.obolonsky.crypto.databinding.FragmentCoinDetailsBinding
import io.obolonsky.crypto.viewmodels.ComponentViewModel
import kotlinx.coroutines.flow.launchIn

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
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .launchIn(viewLifecycleOwner.lifecycleScope)

        coinDetailsViewModel.loadDetails()
    }
}