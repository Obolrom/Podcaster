package io.obolonsky.github.ui

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import coil.load
import io.obolonsky.core.di.lazyViewModel
import io.obolonsky.core.di.toaster
import io.obolonsky.github.R
import io.obolonsky.github.databinding.FragmentUserInfoBinding
import io.obolonsky.github.redux.userinfo.UserInfoSideEffects
import io.obolonsky.github.redux.userinfo.UserInfoState
import io.obolonsky.github.resetNavGraph
import io.obolonsky.github.viewmodels.ComponentViewModel
import io.obolonsky.github.viewmodels.UserInfoViewModel
import org.orbitmvi.orbit.viewmodel.observe

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
        binding.searchRepositories.setOnClickListener {
            findNavController()
                .navigate(R.id.action_repositoryListFragment_to_searchReposFragment)
        }

        viewModel.observe(viewLifecycleOwner, state = ::render, sideEffect = ::sideEffect)
    }

    private fun render(state: UserInfoState) {
        val isLoading = state.isLoading
        val user = state.user

        binding.progressBar.isVisible = isLoading
        binding.getUserInfo.isEnabled = !isLoading
        binding.userInfo.isVisible = !isLoading

        if (user != null) {
            binding.avatar.load(user.avatarUrl) {
                crossfade(400)
            }
            binding.username.text = user.login
            binding.userInfo.text = user.toString()
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