package io.obolonsky.github.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.InsertDriveFile
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarOutline
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import io.obolonsky.core.di.data.github.GithubRepoView
import io.obolonsky.core.di.data.github.RepoOwner
import io.obolonsky.core.di.data.github.RepoTreeEntry
import io.obolonsky.core.di.lazyViewModel
import io.obolonsky.core.di.toaster
import io.obolonsky.github.redux.repoview.GithubRepoViewState
import io.obolonsky.github.redux.repoview.RepoViewSideEffects
import io.obolonsky.github.ui.compose.theme.ComposeMainTheme
import io.obolonsky.github.ui.compose.theme.Shapes
import io.obolonsky.github.viewmodels.ComponentViewModel
import io.obolonsky.github.viewmodels.GithubRepoViewViewModel
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import io.obolonsky.core.R as CoreR
import io.obolonsky.coreui.R as CoreUiR

class GithubRepoFragment : Fragment() {

    private val componentViewModel by activityViewModels<ComponentViewModel>()

    private val args by navArgs<GithubRepoFragmentArgs>()

    private val viewModel: GithubRepoViewViewModel by lazyViewModel {
        componentViewModel.gitHubComponent
            .getGithubRepoViewViewModelFactory()
            .create(
                savedStateHandle = it,
                owner = args.owner,
                repo = args.repoName,
            )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val context = LocalContext.current
                val coroutineScope = rememberCoroutineScope()
                val snackBarHostState = remember { SnackbarHostState() }

                val state by viewModel.collectAsState()

                viewModel.collectSideEffect { sideEffect ->
                    when (sideEffect) {
                        is RepoViewSideEffects.TogglingStarFailed -> {
                            toaster().value.showToast(context, sideEffect.error.toString())
                        }
                        is RepoViewSideEffects.TogglingStarSucceed -> coroutineScope.launch {
                            snackBarHostState.currentSnackbarData?.dismiss()
                            val message = context.getString(sideEffect.messageResId)
                            snackBarHostState.showSnackbar(message)
                        }
                    }
                }

                Box {
                    AnimatedVisibility(
                        visible = state.model != null,
                        enter = fadeIn(animationSpec = tween()),
                    ) {
                        Screen(
                            viewState = state,
                            onStarClick = { viewModel.toggleRepoStar() },
                            onViewCodeClick = { viewModel.showRepoTree() },
                            onBranchDialogOpen = { viewModel.loadBranches() },
                            onBranchSelected = { viewModel.selectBranch(it) },
                        )
                    }

                    Box(
                        modifier = Modifier.align(Alignment.Center),
                    ) {
                        AnimatedVisibility(
                            visible = state.model == null,
                            exit = fadeOut(animationSpec = tween()),
                        ) {
                            CircularProgressIndicator(
                                color = colorResource(id = CoreUiR.color.blue),
                            )
                        }
                    }

                    SnackbarHost(
                        modifier = Modifier.align(Alignment.BottomCenter),
                        hostState = snackBarHostState,
                    )
                }
            }
        }
    }
}

@Composable
fun Screen(
    viewState: GithubRepoViewState,
    onStarClick: () -> Unit,
    onViewCodeClick: () -> Unit,
    onBranchDialogOpen: () -> Unit,
    onBranchSelected: (String) -> Unit,
) = ComposeMainTheme {
    Column(
        modifier = Modifier.padding(6.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(id = CoreUiR.drawable.repo_svgrepo_com),
                contentDescription = null,
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = viewState.model?.owner?.login.orEmpty(),
                color = colorResource(id = CoreUiR.color.blue),
            )
            Spacer(Modifier.width(4.dp))
            Text(text = "/")
            Spacer(Modifier.width(4.dp))
            Text(
                text = viewState.model?.repoName.orEmpty(),
                color = colorResource(id = CoreUiR.color.blue),
                fontWeight = FontWeight.W500,
            )
        }

        RepoDescription(
            text = viewState.model?.description.orEmpty(),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
        StarsForks(
            stars = viewState.model?.stargazerCount ?: 0,
            forks = viewState.model?.forkCount ?: 0,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        )

        Row(
            modifier = Modifier.padding(8.dp),
        ) {

            StarButton(
                viewerHasStarred = viewState.model?.viewerHasStarred ?: false,
                modifier = Modifier.weight(1f),
                onStarClick = onStarClick,
            )
            Spacer(modifier = Modifier.width(8.dp))
            StarButton(
                viewerHasStarred = viewState.model?.viewerHasStarred ?: false,
                modifier = Modifier.weight(1f),
                onStarClick = onStarClick,
            )
        }

        Card(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            border = BorderStroke(0.25.dp, Color.Gray),
        ) {
            val interactionSource = remember { MutableInteractionSource() }
            val openDialog = remember { mutableStateOf(false) }

            if (openDialog.value) {
                AlertDialog(
                    onDismissRequest = {
                        openDialog.value = false
                    },
                    buttons = {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(210.dp)
                            ,
                            shape = Shapes.large,
                        ) {
                            Column {
                                Text(
                                    modifier = Modifier
                                        .padding(12.dp, 12.dp, 12.dp),
                                    text = "Switch branches",
                                    fontWeight = FontWeight.W500,
                                )
                                Divider(modifier = Modifier.padding(vertical = 4.dp))

                                val items = viewState.model?.branches

                                if (items != null) {
                                    LazyColumn(
                                        modifier = Modifier
                                            .padding(start = 16.dp, end = 16.dp, bottom = 12.dp),
                                    ) {
                                        items(items) { branch ->
                                            Text(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable {
                                                        onBranchSelected(branch)
                                                    }
                                                    .padding(vertical = 4.dp),
                                                text = branch,
                                            )
                                            Divider()
                                        }
                                    }
                                }
                            }
                        }
                    }
                )
            }

            Row(
                modifier = Modifier
                    .clickable(
                        indication = rememberRipple(),
                        interactionSource = interactionSource,
                        onClick = {
                            onBranchDialogOpen()
                            openDialog.value = true
                        },
                    )
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = painterResource(id = CoreUiR.drawable.git_branch_svgrepo_com),
                    contentDescription = null,
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = viewState.model?.defaultBranchName.orEmpty())
                Spacer(modifier = Modifier.width(2.dp))
                Icon(
                    imageVector = Icons.Rounded.ArrowDropDown,
                    contentDescription = null,
                )
            }
        }

        Card(
            modifier = Modifier.padding(8.dp),
        ) {
            Column {
                Text(
                    modifier = Modifier
                        .height(36.dp)
                        .background(colorResource(id = CoreUiR.color.black_10))
                        .fillMaxWidth(),
                    text = "Stub",
                )
                Divider()
                AnimatedVisibility(
                    visible = viewState.shouldShowRepoTree,
                    enter = fadeIn(tween(500))
                ) {
                    LazyColumn {
                        val items = viewState.model?.treeEntries.orEmpty()
                        itemsIndexed(items) { index, treeEntry ->
                            RepoTreeEntry(
                                treeEntry = treeEntry,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            )
                            if (index != items.size - 1) {
                                Divider()
                            }
                        }
                    }
                }
                AnimatedVisibility(
                    visible = !viewState.shouldShowRepoTree,
                    enter = fadeIn(tween())
                ) {
                    val interactionSource = remember { MutableInteractionSource() }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                indication = rememberRipple(),
                                interactionSource = interactionSource,
                                onClick = onViewCodeClick,
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(vertical = 8.dp),
                            text = "View code",
                            color = colorResource(id = CoreUiR.color.blue),
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RepoTreeEntry(
    treeEntry: RepoTreeEntry,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (treeEntry.mode == 16384) {
            Icon(
                imageVector = Icons.Rounded.Folder,
                contentDescription = null,
                tint = colorResource(id = CoreUiR.color.blue),
            )
        } else {
            Icon(
                imageVector = Icons.Outlined.InsertDriveFile,
                contentDescription = null,
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = treeEntry.name,
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = treeEntry.lastCommit.date,
        )
    }
}

@Composable
fun RepoDescription(
    text: String,
    modifier: Modifier = Modifier,
    maxLines: Int = Int.MAX_VALUE,
    color: Color = Color.Unspecified,
    style: TextStyle = LocalTextStyle.current,
) {
    Text(
        modifier = modifier,
        text = text,
        maxLines = maxLines,
        color = color,
        style = style,
    )
}

@Composable
fun StarsForks(
    stars: Int,
    forks: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(imageVector = Icons.Rounded.StarOutline, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = stringResource(id = CoreR.string.stars_count, stars))
        Spacer(modifier = Modifier.width(24.dp))
        Icon(painter = painterResource(id = CoreUiR.drawable.git_fork), contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = stringResource(id = CoreR.string.forks_count, forks))
    }
}

@Composable
fun StarButton(
    viewerHasStarred: Boolean,
    modifier: Modifier = Modifier,
    onStarClick: () -> Unit,
) {
    Card(
        modifier = modifier,
        elevation = 2.dp,
    ) {
        val imageVector =
            if (viewerHasStarred) Icons.Rounded.Star
            else Icons.Rounded.StarOutline
        val messageResId =
            if (viewerHasStarred) CoreR.string.starred
            else CoreR.string.star

        Row(
            modifier = Modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(bounded = true),
                    onClick = onStarClick,
                )
                .padding(vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = imageVector,
                contentDescription = null,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = stringResource(messageResId))
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
)
@Composable
fun SecondPreview() {
    val state = GithubRepoViewState(
        model = GithubRepoView(
            id = "id",
            repoName = "RxJava",
            owner = RepoOwner(
                login = "Obolrom",
                avatarUrl = "url",
            ),
            stargazerCount = 3,
            forkCount = 1,
            description = "The best application in the world",
            treeEntries = listOf(
                RepoTreeEntry(
                    name = "README.md",
                    type = "tree",
                    mode = 16384,
                    treePath = "",
                    lastCommit = RepoTreeEntry.LastCommit(
                        message = "best feature ever",
                        date = "2022-10-14"
                    ),
                ),
                RepoTreeEntry(
                    name = ".gitignore",
                    type = "blob",
                    mode = 33188,
                    treePath = "",
                    lastCommit = RepoTreeEntry.LastCommit(
                        message = "best feature ever",
                        date = "2022-10-14"
                    ),
                ),
            ),
            viewerHasStarred = true,
            defaultBranchName = "master",
        ),
        shouldShowRepoTree = true,
    )

    Screen(
        viewState = state,
        onStarClick = { },
        onViewCodeClick = { },
        onBranchDialogOpen = { },
        onBranchSelected = { },
    )
}