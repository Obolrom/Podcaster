package io.obolonsky.github.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ForkLeft
import androidx.compose.material.icons.rounded.ForkRight
import androidx.compose.material.icons.rounded.StarOutline
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import io.obolonsky.github.ui.compose.theme.ComposeMainTheme
import kotlinx.coroutines.flow.flowOf
import io.obolonsky.core.R as CoreR
import io.obolonsky.coreui.R as CoreUiR

class GithubRepoFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnLifecycleDestroyed(
                    viewLifecycleOwner
                )
            )
            setContent {
                Screen()
            }
        }
    }
}

@Composable
fun Screen() {
    ComposeMainTheme {
        val context = LocalContext.current
        val starsState = remember { mutableStateOf(1) }

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
        ) {
            RepoDescription(text = "Application for testing")
            StarsForks(stars = starsState, forks = 1, Modifier.padding(8.dp))
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(102.dp),
                onClick = {
                    starsState.value++
                    Toast.makeText(context, "fuck", Toast.LENGTH_SHORT).show()
                },
            ) {
                Text(text = "Hello world")
            }
        }
    }
}

@Composable
fun RepoDescription(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier,
        text = text,
    )
}

@Composable
fun StarsForks(
    stars: MutableState<Int>,
    forks: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(imageVector = Icons.Rounded.StarOutline, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = stringResource(id = CoreR.string.stars_count, stars.value))
        Spacer(modifier = Modifier.width(24.dp))
        Icon(painter = painterResource(id = CoreUiR.drawable.git_fork), contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = stringResource(id = CoreR.string.forks_count, forks))
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
)
@Composable
fun SecondPreview() {
    Screen()
}