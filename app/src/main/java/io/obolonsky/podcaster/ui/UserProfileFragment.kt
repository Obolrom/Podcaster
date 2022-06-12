package io.obolonsky.podcaster.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import coil.load
import coil.util.CoilUtils
import dagger.hilt.android.AndroidEntryPoint
import io.obolonsky.podcaster.R
import io.obolonsky.podcaster.data.room.StatefulData
import io.obolonsky.podcaster.data.room.entities.UserProfile
import io.obolonsky.podcaster.databinding.FragmentProfileBinding
import io.obolonsky.podcaster.misc.launchWhenStarted
import io.obolonsky.podcaster.viewmodels.ProfileViewModel
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class UserProfileFragment : AbsFragment(R.layout.fragment_profile) {

    private val binding by viewBinding<FragmentProfileBinding>()

    private val userViewModel by viewModels<ProfileViewModel>()

    override fun initViewModels() {
        userViewModel.userProfile
            .onEach(::onUserProfile)
            .launchWhenStarted(lifecycleScope)
    }

    override fun initViews(savedInstanceState: Bundle?) {
        userViewModel.fetchUserInfo()
    }

    @SuppressLint("SetTextI18n")
    private fun onUserProfile(userProfileData: StatefulData<UserProfile>) {
        when (userProfileData) {
            is StatefulData.Success -> {
                userProfileData.data!!.apply {
                    binding.username.text = "Jake Wharton"
                    binding.rating.text = getString(R.string.rating, raiting)
                    binding.totalAuditions.text = getString(R.string.total_auditions, auditionCount)
                    binding.subscribers.text = getString(R.string.subscribers, 11)
                    binding.balance.text = getString(R.string.balance, 0)
                }

                binding.firstSubProfileImage.load(
                    "https://4688dg3ia2142lq8ndca3zt1-wpengine.netdna-ssl.com/wp-content/uploads/2019/08/Flea.jpg"
                ) {
                    crossfade(400)
                }

                binding.secondSubProfileImage.load(
                    "https://hsto.org/r/w1560/webt/jw/rj/ax/jwrjax9dcgvxn3uvakuy9veflvu.jpeg"
                ) {
                    crossfade(400)
                }

                binding.firstPublication.load(
                    "https://gp1.wac.edgecastcdn.net/802892/http_public_production/photos/images/799395/original/resize:300x225/crop:x0y64w507h380/hash:1463088025/Blue_Stahli_-_Scrape_Cover_v1.png?1463088025"
                ) {
                    crossfade(400)
                }

                binding.secondPublication.load(
                    "https://upload.wikimedia.org/wikipedia/uk/9/92/Lords_of_the_Boards2.jpg"
                ) {
                    crossfade(400)
                }

                binding.thirdPublication.load(
                    "https://m.media-amazon.com/images/M/MV5BYWE5NDI1MzktYWE4Ny00MDZjLThiNjYtMjU2ZTE2MjFkOTIyXkEyXkFqcGdeQXVyNTk1NTMyNzM@._V1_.jpg"
                ) {
                    crossfade(400)
                }

                binding.profileImage.load(
                    "https://avatars.githubusercontent.com/u/66577?v=4"
                ) {
                    crossfade(400)
                }
            }

            is StatefulData.Error -> { }

            else -> error("Unsupported state")
        }
    }
}