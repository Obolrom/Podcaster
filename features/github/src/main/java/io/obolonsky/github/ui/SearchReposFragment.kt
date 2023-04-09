@file:OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)

package io.obolonsky.github.ui

import android.os.Bundle
import android.view.View
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import io.obolonsky.core.di.data.github.GithubRepoView
import io.obolonsky.core.di.data.github.ProgrammingLang
import io.obolonsky.core.di.data.github.Topic
import io.obolonsky.core.di.lazyViewModel
import io.obolonsky.core.di.toaster
import io.obolonsky.github.R
import io.obolonsky.github.databinding.FragmentSearchReposBinding
import io.obolonsky.github.redux.searchrepos.SearchReposSideEffects
import io.obolonsky.github.redux.searchrepos.SearchReposState
import io.obolonsky.github.ui.compose.theme.ComposeMainTheme
import io.obolonsky.github.ui.compose.theme.Typography
import io.obolonsky.github.viewmodels.ComponentViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.viewmodel.observe
import java.text.SimpleDateFormat
import java.util.*

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
            LazyColumn(
                contentPadding = PaddingValues(12.dp)
            ) {
                items(
                    items = viewState.searchResults,
                    key = { repo -> repo.id },
                ) { repo ->
                    SearchRepo(
                        repo = repo,
                        onRepoClick = { _, _ -> },
                        modifier = Modifier
                            .fillParentMaxWidth()
                            .border(width = 0.5.dp, color = Color.LightGray, shape = RoundedCornerShape(size = 5.dp))
                            .padding(start = 12.dp, end = 12.dp, top = 16.dp, bottom = 16.dp),
                    )
                    Spacer(Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun SearchRepo(
    repo: GithubRepoView,
    onRepoClick: (owner: String, repo: String) -> Unit,
    modifier: Modifier = Modifier,
) = Column(modifier = modifier) {
    RepoTitle(
        repo = repo,
        onRepoClick = onRepoClick,
    )
    repo.parent?.let { parentRepo ->
        ForkedRepoFrom(
            parentRepo = parentRepo,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
    repo.description?.let { text ->
        RepoDescription(
            text = text,
            modifier = Modifier.padding(top = 4.dp),
            maxLines = 4,
            color = Color.Gray,
            style = Typography.body2,
        )
    }
    FlowRow(
        modifier = Modifier.padding(top = 8.dp),
    ) {
        repo.topics.forEach { topic ->
            GithubTopic(
                modifier = Modifier.padding(2.dp),
                topic = topic,
            )
        }
    }
    FlowRow(
        modifier = Modifier.padding(top = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repo.primaryLanguage?.let { lang ->
            PrimaryLanguage(lang = lang)
            Spacer(Modifier.width(8.dp))
            Text(text = "·")
            Spacer(Modifier.width(8.dp))
        }
        if (repo.stargazerCount != 0) {
            GithubRepoStarsCount(
                stars = repo.stargazerCount,
            )
            Spacer(Modifier.width(8.dp))
            Text(text = "·")
            Spacer(Modifier.width(8.dp))
        }
        repo.updatedAt
            ?.let(SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ROOT)::parse)
            ?.let(SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH)::format)
            ?.let { updatedAt ->
                Text(
                    text = stringResource(id = io.obolonsky.core.R.string.updated_at, updatedAt),
                    color = Color.Gray,
                    style = Typography.caption,
                )
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
                ),
                topics = listOf(
                    Topic("android"),
                    Topic("shazam"),
                    Topic("kotlin"),
                ),
            ),
        )
    )

    SearchReposContainerScreen(state)
}