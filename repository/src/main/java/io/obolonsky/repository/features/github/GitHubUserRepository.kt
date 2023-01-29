package io.obolonsky.repository.features.github

import dagger.Reusable
import io.obolonsky.core.di.Reaction
import io.obolonsky.core.di.data.github.GithubUser
import io.obolonsky.core.di.data.github.GithubUserProfile
import io.obolonsky.core.di.repositories.github.GitHubUserRepo
import io.obolonsky.network.apihelpers.github.GetGithubUserApiHelper
import io.obolonsky.network.apihelpers.github.GetGithubUserProfileApiHelper
import io.obolonsky.network.apihelpers.github.GetGithubViewerProfileApiHelper
import javax.inject.Inject

@Reusable
class GitHubUserRepository @Inject constructor(
    private val getGithubUserApiHelper: GetGithubUserApiHelper,
    private val githubUserProfileApiHelper: GetGithubUserProfileApiHelper,
    private val githubUserViewerApiHelper: GetGithubViewerProfileApiHelper,
) : GitHubUserRepo {

    @Deprecated("do not user rest implementation")
    override suspend fun getUserInformation(): Reaction<GithubUser> {
        return getGithubUserApiHelper.load(Unit)
    }

    override suspend fun getViewerProfile(): Reaction<GithubUserProfile> {
        return githubUserViewerApiHelper.load(Unit)
    }
}