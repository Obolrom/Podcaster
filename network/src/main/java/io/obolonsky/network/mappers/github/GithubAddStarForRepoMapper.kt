package io.obolonsky.network.mappers.github

import io.obolonsky.core.di.data.github.GithubRepoStarToggle
import io.obolonsky.core.di.utils.Mapper
import io.obolonsky.network.github.AddStarForRepoMutation
import io.obolonsky.network.github.RemoveStarForRepoMutation

class GithubAddStarForRepoMapper : Mapper<AddStarForRepoMutation.Data, GithubRepoStarToggle> {

    override fun map(input: AddStarForRepoMutation.Data): GithubRepoStarToggle {
        val onRepo = requireNotNull(input.addStar?.starrable?.onRepository)

        return GithubRepoStarToggle(
            viewerHasStarred = onRepo.viewerHasStarred,
            stargazerCount = onRepo.stargazerCount,
        )
    }
}

class GithubRemoveStarForRepoMapper : Mapper<RemoveStarForRepoMutation.Data, GithubRepoStarToggle> {

    override fun map(input: RemoveStarForRepoMutation.Data): GithubRepoStarToggle {
        val onRepo = requireNotNull(input.removeStar?.starrable?.onRepository)

        return GithubRepoStarToggle(
            viewerHasStarred = onRepo.viewerHasStarred,
            stargazerCount = onRepo.stargazerCount,
        )
    }
}