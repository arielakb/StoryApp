package com.dicoding.storyapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.paging.ExperimentalPagingApi
import com.dicoding.storyapp.R
import com.dicoding.storyapp.databinding.FragmentLoginBinding
import com.dicoding.storyapp.ui.MainActivity.Companion.EXTRA_TOKEN
import com.dicoding.storyapp.utils.animateVisibility
import com.dicoding.storyapp.viewmodel.LoginViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job

@AndroidEntryPoint
@ExperimentalPagingApi
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding

    private var loginJob: Job = Job()
    private val viewModel: LoginViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            btnLogin.setOnClickListener {
                loginAuth()
            }
            btnRegister.setOnClickListener(
                Navigation.createNavigateOnClickListener(R.id.action_loginFragment_to_registerFragment)
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    private fun loginAuth() {
        showLoading(true)
        val email = binding?.email?.text.toString().trim()
        val password = binding?.password?.text.toString()

        lifecycleScope.launchWhenResumed {
            if (loginJob.isActive) loginJob.cancel()
             viewModel.userLogin(email, password).collect { result ->
                    result.onSuccess { credentials ->
                        credentials.loginResult?.token?.let { token ->
                            viewModel.saveAuthToken(token)
                            Intent(requireContext(), MainActivity::class.java).also { intent ->
                                intent.putExtra(EXTRA_TOKEN, token)
                                startActivity(intent)
                                requireActivity().finish()
                            }
                        }

                        Toast.makeText(
                            requireContext(),
                            getString(R.string.login_success_message),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    result.onFailure {
                        binding?.let { it1 ->
                            Snackbar.make(
                                it1.root,
                                getString(R.string.login_error_message),
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }

                        showLoading(false)
                    }
                }
            }
        }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showLoading(isLoading: Boolean) {
        binding?.apply {
            email.isEnabled = !isLoading
            password.isEnabled = !isLoading
            btnLogin.isEnabled = !isLoading

            if (isLoading) {
                viewLoading.animateVisibility(true)
            } else {
                viewLoading.animateVisibility(false)
            }
        }
    }

}