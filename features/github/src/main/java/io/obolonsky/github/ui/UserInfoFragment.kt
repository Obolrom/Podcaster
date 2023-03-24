package io.obolonsky.github.ui

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
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
        }
    }
}

@Composable
fun UserInfoContainerScreen(
    viewState: UserInfoState,
    onRepoClick: () -> Unit,
    onSearch: () -> Unit,
) = ComposeMainTheme {

    if (viewState.user != null)
        UserProfile(
            user = viewState.user,
            onRepoClick = onRepoClick,
            onSearch = onSearch,
        )
}

@Composable
fun UserProfile(
    user: GithubUserProfile,
    onRepoClick: () -> Unit,
    onSearch: () -> Unit,
) = Column {
    Text(
        text = user.login,
        fontSize = 24.sp
    )
    Button(
        modifier = Modifier
            .fillMaxWidth(),
        onClick = onRepoClick,
    ) {
        Text(text = "To repo view")
    }
    Button(
        modifier = Modifier
            .fillMaxWidth(),
        onClick = onSearch,
    ) {
        Text(text = stringResource(id = CoreR.string.search_repositories))
    }
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
            avatarUrl = "avatar",
            email = "android@gmail.com"
        )
    )

    UserInfoContainerScreen(
        viewState = state,
        onRepoClick = { },
        onSearch = { },
    )
}