package io.obolonsky.github.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import io.obolonsky.core.di.data.github.GithubRepository
import io.obolonsky.github.databinding.SearchRepoItemBinding

class SearchReposAdapter(
    diffUtil: ItemCallback<GithubRepository> = DefaultDiffUtil(),
) : ListAdapter<GithubRepository, SearchRepoViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchRepoViewHolder {
        val binding = SearchRepoItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SearchRepoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchRepoViewHolder, position: Int) {
        getItem(position)?.let(holder::bind)
    }

    class DefaultDiffUtil : ItemCallback<GithubRepository>() {
        override fun areItemsTheSame(
            oldItem: GithubRepository,
            newItem: GithubRepository
        ): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(
            oldItem: GithubRepository,
            newItem: GithubRepository
        ): Boolean {
            return oldItem == newItem
        }
    }
}

class SearchRepoViewHolder(
    private val binding: SearchRepoItemBinding,
) : ViewHolder(binding.root) {

    fun bind(item: GithubRepository): Unit = with(binding) {
        repoName.text = item.nameWithOwner
    }
}