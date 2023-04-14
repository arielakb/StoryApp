package com.dicoding.storyapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.dicoding.storyapp.R
import com.dicoding.storyapp.databinding.FragmentRegisterBinding
import com.dicoding.storyapp.utils.animateVisibility
import com.dicoding.storyapp.viewmodel.RegisterViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding

    private var registerJob: Job = Job()
    private val viewModel: RegisterViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            btnRegister.setOnClickListener {
                registerAuth()
            }
            btnLogin.setOnClickListener(
                Navigation.createNavigateOnClickListener(R.id.action_registerFragment_to_loginFragment)
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    private fun registerAuth() {
        showLoading(true)
        val name = binding?.name?.text.toString().trim()
        val email = binding?.email?.text.toString().trim()
        val password = binding?.password?.text.toString()

        lifecycleScope.launchWhenResumed {
            if (registerJob.isActive) registerJob.cancel()

            registerJob = launch {
                viewModel.userRegister(name, email, password).collect { result ->
                    result.onSuccess {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.registration_success),
                            Toast.LENGTH_SHORT
                        ).show()

                        findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                    }

                    result.onFailure {
                        binding?.let { it1 ->
                            Snackbar.make(
                                it1.root,
                                getString(R.string.registration_error_message),
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                        showLoading(false)
                    }
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
            name.isEnabled = !isLoading
            btnRegister.isEnabled = !isLoading

            if (isLoading) {
                viewLoading.animateVisibility(true)
            } else {
                viewLoading.animateVisibility(false)
            }
        }
    }
}