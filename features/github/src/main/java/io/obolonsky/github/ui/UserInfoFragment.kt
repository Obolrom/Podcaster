@file:OptIn(ExperimentalLayoutApi::class)

package io.obolonsky.github.ui

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.People
import androidx.compose.material.icons.rounded.StarOutline
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import coil.compose.AsyncImage
import io.obolonsky.core.di.data.github.*
import io.obolonsky.core.di.lazyViewModel
import io.obolonsky.core.di.toaster
import io.obolonsky.github.R
import io.obolonsky.github.databinding.FragmentUserInfoBinding
import io.obolonsky.github.redux.userinfo.UserInfoSideEffects
import io.obolonsky.github.redux.userinfo.UserInfoState
import io.obolonsky.github.resetNavGraph
import io.obolonsky.github.ui.compose.theme.ComposeMainTheme
import io.obolonsky.github.ui.compose.theme.Typography
import io.obolonsky.github.viewmodels.ComponentViewModel
import io.obolonsky.github.viewmodels.UserInfoViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random
import io.obolonsky.core.R as CoreR
import io.obolonsky.coreui.R as CoreUiR

class UserInfoFragment : Fragment(R.layout.fragment_user_info) {

    private val componentViewModel by activityViewModels<ComponentViewModel>()

    private val toaster by toaster()

    private val viewModel: UserInfoViewModel by lazyViewModel {
        componentViewModel.gitHubComponent
            .getUserInfoViewModelFactory()
            .create(it)
    }
    private val binding by viewBinding(FragmentUserInfoBinding::bind)

    private val logoutResponse = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.webLogoutComplete()
        } else {
            // логаут отменен
            // делаем complete тк github не редиректит после логаута и пользователь закрывает CCT
            viewModel.webLogoutComplete()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.logout.setOnClickListener {
            viewModel.logout()
        }
        binding.composeContainer.setViewCompositionStrategy(
            ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
        )
        binding.composeContainer.setContent {
            val state by viewModel.collectAsState()
            viewModel.collectSideEffect(sideEffect = ::sideEffect)

            UserInfoContainerScreen(
                viewState = state,
                onRepoClick = {
                    findNavController()
                        .navigate(R.id.action_repositoryListFragment_to_githubRepoFragment)
                },
                onSearch = {
                    findNavController()
                        .navigate(R.id.action_repositoryListFragment_to_searchReposFragment)
                },
                onDayClick = viewModel::chartDaySelected,
            )
        }
    }

    private fun sideEffect(effect: UserInfoSideEffects) {
        when (effect) {
            is UserInfoSideEffects.ToastEvent -> {
                toaster.showToast(requireContext(), getString(effect.stringRes))
            }
            is UserInfoSideEffects.LogoutPageEvent -> {
                logoutResponse.launch(effect.intent)
            }
            is UserInfoSideEffects.LogoutPageCompletedEvent -> {
                findNavController().resetNavGraph(R.navigation.nav_graph)
            }
            is UserInfoSideEffects.ChartDayEvent -> {
                toaster.showToast(
                    context = requireContext(),
                    message = "${effect.day.contributionCount} contributions, ${effect.day.date}"
                )
            }
        }
    }
}

@Composable
fun UserInfoContainerScreen(
    viewState: UserInfoState,
    onRepoClick: () -> Unit,
    onSearch: () -> Unit,
    onDayClick: (GithubDay) -> Unit,
) = ComposeMainTheme {

    Column {

        if (viewState.user != null)
            UserProfile(
                user = viewState.user,
                onRepoClick = onRepoClick,
                onSearch = onSearch,
            )

        Spacer(modifier = Modifier.height(12.dp))

        val contributionCalendar = viewState.user?.contributionChart?.days
        if (contributionCalendar != null)
            GithubContributionChart(
                modifier = Modifier.padding(horizontal = 12.dp),
                contributions = contributionCalendar,
                onDayClick = onDayClick,
            )

        if (viewState.repos != null) {
            ViewerRepos(
                repos = viewState.repos,
            )
        }
    }
}

@Composable
fun UserProfile(
    user: GithubUserProfile,
    onRepoClick: () -> Unit,
    onSearch: () -> Unit,
) = Column {
    AvatarLogin(
        modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 8.dp),
        user = user,
    )

    val statusMessage = user.status.message
    if (statusMessage != null) {
        Text(
            modifier = Modifier
                .padding(start = 12.dp, end = 12.dp, top = 16.dp)
                .fillMaxWidth()
                .border(1.dp, Color.LightGray, RoundedCornerShape(4.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            text = statusMessage,
        )
    }
    FollowersFollowing(
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 16.dp),
        user = user,
    )
    Divider()

    Button(
        modifier = Modifier
            .padding(horizontal = 12.dp)
            .fillMaxWidth(),
        onClick = onRepoClick,
    ) {
        Text(text = "To repo view")
    }
    Button(
        modifier = Modifier
            .padding(horizontal = 12.dp)
            .fillMaxWidth(),
        onClick = onSearch,
    ) {
        Text(text = stringResource(id = CoreR.string.search_repositories))
    }
}

@Composable
fun AvatarLogin(
    user: GithubUserProfile,
    modifier: Modifier = Modifier,
) = Row(modifier = modifier) {
    AsyncImage(
        modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .border(1.dp, Color.LightGray, CircleShape),
        model = user.avatarUrl,
        contentDescription = null,
    )
    Spacer(
        modifier = Modifier.width(12.dp),
    )
    Text(
        modifier = Modifier.padding(top = 12.dp),
        text = user.login,
        fontSize = 24.sp
    )
}

@Composable
fun ViewerRepos(
    repos: List<GithubRepoView>,
    modifier: Modifier = Modifier,
) = Column(modifier = modifier) {
    LazyColumn {
        items(
            items = repos,
            key = { repo -> repo.id },
        ) { repo ->
            ViewerRepo(
                repo = repo,
                modifier = Modifier
                    .padding(start = 12.dp, end = 12.dp, top = 24.dp, bottom = 20.dp),
            )
            Divider(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .weight(1f)
            )
        }
    }
}

@Composable
fun ViewerRepo(
    repo: GithubRepoView,
    modifier: Modifier = Modifier,
) = Column(modifier = modifier) {
    RepoTitle(repo = repo)
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
        modifier = Modifier.padding(top = 4.dp),
    ) {
        repo.primaryLanguage?.let { lang ->
            PrimaryLanguage(lang = lang)
            Spacer(Modifier.width(12.dp))
        }
        if (repo.stargazerCount != 0) {
            GithubRepoStarsCount(
                stars = repo.stargazerCount,
            )
            Spacer(Modifier.width(12.dp))
        }
        Timber.d("fuckingFuck ${repo.updatedAt}")
        repo.updatedAt
            ?.let(SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ROOT)::parse)
            ?.let(SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH)::format)
            ?.let { updatedAt ->
                Text(
                    text = updatedAt,
                    color = Color.Gray,
                    style = Typography.caption,
                )
            }
    }
}

@Composable
fun GithubRepoStarsCount(
    stars: Int,
    modifier: Modifier = Modifier,
) = Row(modifier = modifier) {
    Icon(
        modifier = Modifier.size(16.dp),
        imageVector = Icons.Rounded.StarOutline,
        tint = Color.Gray,
        contentDescription = null
    )
    Text(
        text = stars.toString(),
        color = Color.Gray,
        style = Typography.caption,
    )
}

@Composable
fun ForkedRepoFrom(
    parentRepo: GithubRepoView,
    modifier: Modifier = Modifier,
) = Row(modifier = modifier) {
    Text(
        text = stringResource(id = CoreR.string.forked_from),
        color = Color.Gray,
        style = Typography.caption,
    )
    Spacer(Modifier.width(4.dp))
    Text(
        text = "${parentRepo.owner}/${parentRepo.repoName}",
        color = Color.Gray,
        style = Typography.caption,
    )
}

@Composable
fun PrimaryLanguage(
    lang: ProgrammingLang,
    modifier: Modifier = Modifier,
) = Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically,
) {
    // TODO: fix potential NPE with lang.color
    Spacer(
        Modifier
            .size(10.dp)
            .background(Color(android.graphics.Color.parseColor(lang.color)), CircleShape)
    )
    Spacer(Modifier.width(4.dp))
    Text(
        text = lang.langName,
        color = Color.Gray,
        style = Typography.caption,
    )
}

@Composable
fun RepoTitle(
    repo: GithubRepoView,
    modifier: Modifier = Modifier,
) = FlowRow(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically,
) {
    Text(
        text = repo.repoName,
        color = colorResource(id = CoreUiR.color.blue),
        fontWeight = FontWeight.W700,
        style = Typography.h6,
    )
    Spacer(Modifier.width(8.dp))
    Text(
        modifier = Modifier
            .border(1.dp, Color.LightGray, RoundedCornerShape(10.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp),
        text = stringResource(id = repo.visibility.resId),
        style = Typography.caption,
    )
}
@Composable
fun FollowersFollowing(
    user: GithubUserProfile,
    modifier: Modifier = Modifier,
) = Row(
    modifier = modifier,
) {
    Icon(
        imageVector = Icons.Rounded.People,
        contentDescription = null,
    )
    Spacer(modifier = Modifier.width(4.dp))
    Text(text = "${user.followers} followers")
    Spacer(modifier = Modifier.width(4.dp))
    Text(text = "·")
    Spacer(modifier = Modifier.width(4.dp))
    Text(text = "${user.following} following")
}

@Composable
fun GithubContributionChart(
    contributions: List<GithubDay>,
    onDayClick: (GithubDay) -> Unit,
    modifier: Modifier = Modifier,
) = LazyHorizontalGrid(
    modifier = modifier.height(96.dp),
    rows = GridCells.Fixed(7),
    verticalArrangement = Arrangement.spacedBy(2.dp),
    horizontalArrangement = Arrangement.spacedBy(2.dp),
) {
    items(
        items = contributions,
        key = { day -> day.date },
    ) { githubDay ->
        GitDay(
            githubDay = githubDay,
            onDayClick = onDayClick,
        )
    }
}

@Composable
fun GitDay(
    githubDay: GithubDay,
    onDayClick: (GithubDay) -> Unit,
    modifier: Modifier = Modifier,
) {
    Spacer(
        modifier = modifier
            .clip(RoundedCornerShape(2.dp))
            .border(0.dp, Color.LightGray, RoundedCornerShape(2.dp))
            .clickable { onDayClick(githubDay) }
            .background(Color(android.graphics.Color.parseColor(githubDay.color)))
            .size(12.dp)
    )
}

@Preview(
    showBackground = true,
    showSystemUi = true,
)
@Composable
fun UserInfoContainerScreenPreview() {
    val state = UserInfoState(
        isLoading = false,
        user = GithubUserProfile(
            id = "id",
            login = "Obolrom",
            avatarUrl = "https://avatars.githubusercontent.com/u/65775868?v=4",
            email = "android@gmail.com",
            followers = 7,
            following = 1,
            status = GithubUserProfile.Status(
                message = "just a nerd",
                emoji = null,
            ),
            contributionChart = ContributionChart(
                totalContributionsForLastYear = 255,
                days = generateSequence {
                    GithubDay(4, "#40c463", Random(1_000_000).toString())
                }
                    .take(365)
                    .toList(),
            )
        ),
        repos = listOf(
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

    UserInfoContainerScreen(
        viewState = state,
        onRepoClick = { },
        onSearch = { },
        onDayClick = { },
    )
}