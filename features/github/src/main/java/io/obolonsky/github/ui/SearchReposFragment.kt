package io.obolonsky.github.ui

import android.os.Bundle
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import io.obolonsky.core.di.lazyViewModel
import io.obolonsky.core.di.toaster
import io.obolonsky.github.R
import io.obolonsky.github.databinding.FragmentSearchReposBinding
import io.obolonsky.github.redux.searchrepos.SearchReposSideEffects
import io.obolonsky.github.redux.searchrepos.SearchReposState
import io.obolonsky.github.viewmodels.ComponentViewModel
import org.orbitmvi.orbit.viewmodel.observe

class SearchReposFragment : Fragment(R.layout.fragment_search_repos) {

    private val componentViewModel by activityViewModels<ComponentViewModel>()

    private val viewModel by lazyViewModel {
        componentViewModel.gitHubComponent
            .getSearchReposViewModelFactory()
            .create(it)
    }

    private val binding by viewBinding(FragmentSearchReposBinding::bind)

    private val repoAdapter = SearchReposAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.searchResult.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = repoAdapter
        }

        binding.searchQuery.doOnTextChanged { text, _, _, _ ->
            viewModel.repoNameSearchQuery.value = text.toString()
        }

        viewModel.observe(viewLifecycleOwner, state = ::render, sideEffect = ::sideEffect)
    }

    private fun render(state: SearchReposState) {
        repoAdapter.submitList(state.searchResults.orEmpty())
    }

    private fun sideEffect(effect: SearchReposSideEffects) = when (effect) {
        is SearchReposSideEffects.SearchError -> {
            toaster().value.showToast(requireContext(), effect.error.toString())
        }
    }

    override fun onDestroyView() {
        binding.searchResult.adapter = null
        super.onDestroyView()
    }
}