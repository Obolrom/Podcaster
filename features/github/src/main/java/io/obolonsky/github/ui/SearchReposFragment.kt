package io.obolonsky.github.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import io.obolonsky.github.R
import io.obolonsky.github.viewmodels.ComponentViewModel

class SearchReposFragment : Fragment(R.layout.fragment_search_repos) {

    private val componentViewModel by activityViewModels<ComponentViewModel>()
}