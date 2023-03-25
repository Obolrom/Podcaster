package io.obolonsky.github.ui

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.People
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import coil.compose.AsyncImage
import io.obolonsky.core.di.data.github.ContributionChart
import io.obolonsky.core.di.data.github.GithubDay
import io.obolonsky.core.di.data.github.GithubUserProfile
import io.obolonsky.core.di.lazyViewModel
import io.obolonsky.core.di.toaster
import io.obolonsky.github.R
import io.obolonsky.github.databinding.FragmentUserInfoBinding
import io.obolonsky.github.redux.userinfo.UserInfoSideEffects
import io.obolonsky.github.redux.userinfo.UserInfoState
import io.obolonsky.github.resetNavGraph
import io.obolonsky.github.ui.compose.theme.ComposeMainTheme
import io.obolonsky.github.viewmodels.ComponentViewModel
import io.obolonsky.github.viewmodels.UserInfoViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import kotlin.random.Random
import io.obolonsky.core.R as CoreR

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
                modifier = Modifier.padding(horizontal = 8.dp),
                contributions = contributionCalendar,
                onDayClick = onDayClick,
            )
    }
}

@Composable
fun UserProfile(
    user: GithubUserProfile,
    onRepoClick: () -> Unit,
    onSearch: () -> Unit,
) = Column {
    AvatarLogin(
        modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 8.dp),
        user = user,
    )

    val statusMessage = user.status.message
    if (statusMessage != null) {
        Text(
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp, top = 16.dp)
                .fillMaxWidth()
                .border(1.dp, Color.LightGray, RoundedCornerShape(4.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            text = statusMessage,
        )
    }
    FollowersFollowing(
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 16.dp),
        user = user,
    )
    Divider()

    Button(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .fillMaxWidth(),
        onClick = onRepoClick,
    ) {
        Text(text = "To repo view")
    }
    Button(
        modifier = Modifier
            .padding(horizontal = 8.dp)
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
    )

    UserInfoContainerScreen(
        viewState = state,
        onRepoClick = { },
        onSearch = { },
        onDayClick = { },
    )
}