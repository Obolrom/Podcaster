package io.obolonsky.github.interactors

import io.obolonsky.core.di.Reaction
import io.obolonsky.core.di.data.github.GithubRepository
import io.obolonsky.core.di.repositories.github.GitHubUserRepo
import io.obolonsky.core.di.scopes.FeatureScope
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@FeatureScope
class SearchReposInteractor @Inject constructor(
    private val userRepository: GitHubUserRepo,
) {

    fun getReposBySearchQuery(repoName: String): Flow<Reaction<List<GithubRepository>>> {
        return userRepository.getReposBySearchQuery(repoName)
    }
}