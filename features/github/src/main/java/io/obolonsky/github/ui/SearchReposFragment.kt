package io.obolonsky.github.ui

import android.os.Bundle
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import io.obolonsky.core.di.lazyViewModel
import io.obolonsky.github.R
import io.obolonsky.github.databinding.FragmentSearchReposBinding
import io.obolonsky.github.redux.searchrepos.SearchReposSideEffects
import io.obolonsky.github.redux.searchrepos.SearchReposState
import io.obolonsky.github.viewmodels.ComponentViewModel
import org.orbitmvi.orbit.viewmodel.observe
import timber.log.Timber

class SearchReposFragment : Fragment(R.layout.fragment_search_repos) {

    private val componentViewModel by activityViewModels<ComponentViewModel>()

    private val viewModel by lazyViewModel {
        componentViewModel.gitHubComponent
            .getSearchReposViewModelFactory()
            .create(it)
    }

    private val binding by viewBinding(FragmentSearchReposBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.searchQuery.doOnTextChanged { text, _, _, _ ->
            viewModel.repoNameSearchQuery.value = text.toString()
        }

        viewModel.observe(viewLifecycleOwner, state = ::render, sideEffect = ::sideEffect)
    }

    private fun render(state: SearchReposState) {
        state.searchResults?.forEach {
            Timber.d("searchRepos success: $it")
        }
    }

    private fun sideEffect(effect: SearchReposSideEffects) {

    }
}