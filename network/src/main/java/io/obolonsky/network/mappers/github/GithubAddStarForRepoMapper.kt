package io.obolonsky.network.mappers.github

import io.obolonsky.core.di.utils.Mapper
import io.obolonsky.network.github.AddStarForRepoMutation
import io.obolonsky.network.github.RemoveStarForRepoMutation

class GithubAddStarForRepoMapper : Mapper<AddStarForRepoMutation.Data, Boolean> {

    override fun map(input: AddStarForRepoMutation.Data): Boolean {
        return input.addStar?.starrable?.onRepository?.viewerHasStarred ?: false
    }
}

class GithubRemoveStarForRepoMapper : Mapper<RemoveStarForRepoMutation.Data, Boolean> {

    override fun map(input: RemoveStarForRepoMutation.Data): Boolean {
        return input.removeStar?.starrable?.onRepository?.viewerHasStarred ?: false
    }
}