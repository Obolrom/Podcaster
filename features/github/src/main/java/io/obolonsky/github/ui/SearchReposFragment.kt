package io.obolonsky.github.ui

import android.os.Bundle
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import io.obolonsky.core.di.data.github.GithubRepository
import io.obolonsky.core.di.lazyViewModel
import io.obolonsky.core.di.toaster
import io.obolonsky.github.R
import io.obolonsky.github.databinding.FragmentSearchReposBinding
import io.obolonsky.github.redux.searchrepos.SearchReposSideEffects
import io.obolonsky.github.redux.searchrepos.SearchReposState
import io.obolonsky.github.ui.compose.theme.ComposeMainTheme
import io.obolonsky.github.ui.compose.theme.Shapes
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
        LazyColumn {
            val items = viewState.searchResults ?: return@LazyColumn
            items(items) { repoItem ->
                SearchRepoItemCard(repoItem)
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Composable
fun SearchRepoItemCard(
    repoItem: GithubRepository,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .background(Color.LightGray, Shapes.large)
            .padding(8.dp)
            .fillMaxWidth(),
    ) {
        Text(
            text = repoItem.name,
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = repoItem.stargazerCount.toString(),
        )
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
            GithubRepository(
                name = "Podcaster",
                nameWithOwner = "Podcaster/Obolrom",
                stargazerCount = 3,
            ),
            GithubRepository(
                name = "RxJava",
                nameWithOwner = "ReactiveX/RxJava",
                stargazerCount = 46_800,
            ),
        )
    )

    SearchReposContainerScreen(state)
}