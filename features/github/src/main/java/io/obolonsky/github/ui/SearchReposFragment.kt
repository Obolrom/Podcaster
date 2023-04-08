package io.obolonsky.github.ui

import android.os.Bundle
import android.view.View
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import io.obolonsky.core.di.data.github.GithubRepoView
import io.obolonsky.core.di.data.github.ProgrammingLang
import io.obolonsky.core.di.lazyViewModel
import io.obolonsky.core.di.toaster
import io.obolonsky.github.R
import io.obolonsky.github.databinding.FragmentSearchReposBinding
import io.obolonsky.github.redux.searchrepos.SearchReposSideEffects
import io.obolonsky.github.redux.searchrepos.SearchReposState
import io.obolonsky.github.ui.compose.theme.ComposeMainTheme
import io.obolonsky.github.viewmodels.ComponentViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.viewmodel.observe

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

        binding.searchResultContainer.setViewCompositionStrategy(
            ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
        )
        binding.searchResultContainer.setContent {
            val state by viewModel.collectAsState()

            SearchReposContainerScreen(
                viewState = state,
            )
        }

        viewModel.observe(viewLifecycleOwner, sideEffect = ::sideEffect)
    }

    private fun sideEffect(effect: SearchReposSideEffects) = when (effect) {
        is SearchReposSideEffects.SearchError -> {
            toaster().value.showToast(requireContext(), effect.error.toString())
        }
    }
}

@Composable
fun SearchReposContainerScreen(
    viewState: SearchReposState,
) = ComposeMainTheme {
    Column {
        if (viewState.searchResults != null) {
            ViewerRepos(viewState.searchResults, { _, _ -> })
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
)
@Composable
fun SearchReposResultsContainerPreview() {
    val state = SearchReposState(
        searchResults = listOf(
            GithubRepoView(
                id = "id-1",
                repoName = "RxJava",
                owner = "Obolrom",
                stargazerCount = 0,
                forkCount = 1,
                description = "The best application in the world",
                treeEntries = emptyList(),
                viewerHasStarred = false,
                defaultBranchName = "master",
                isFork = false,
                updatedAt = "2023-03-31T19:59:44Z",
                primaryLanguage = ProgrammingLang(
                    id = "someId",
                    color = "#40c463",
                    langName = "Java",
                )
            ),
            GithubRepoView(
                id = "id-2",
                repoName = "TV-Guide-EPG-Android-Recyclerview",
                owner = "Obolrom",
                stargazerCount = 3,
                forkCount = 1,
                description = "The best application in the world",
                treeEntries = emptyList(),
                viewerHasStarred = false,
                defaultBranchName = "master",
                isFork = false,
                updatedAt = "2021-08-07T14:31:35Z",
                primaryLanguage = ProgrammingLang(
                    id = "someId",
                    color = "#40c463",
                    langName = "Java",
                )
            ),
        )
    )

    SearchReposContainerScreen(state)
}