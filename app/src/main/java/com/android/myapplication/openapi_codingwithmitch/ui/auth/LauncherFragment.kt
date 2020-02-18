package com.android.myapplication.openapi_codingwithmitch.ui.auth


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.android.myapplication.openapi_codingwithmitch.R
import kotlinx.android.synthetic.main.fragment_launcher.*

/**
 * A simple [Fragment] subclass.
 */
class LauncherFragment : BaseAuthFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_launcher, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "LauncherFragment ${viewModel}")
        register.setOnClickListener {
            navRegistration()
        }
        login.setOnClickListener {
            navLogin()
        }
        forgot_password.setOnClickListener {
            navForgotPassword()
        }

        focusable_view.requestFocus() //just a helper view to allow us take out the focus from any views if it happens randomly
    }

    private fun navForgotPassword() {
        findNavController().navigate(R.id.action_launcherFragment_to_forgotPasswordFragment)
    }

    private fun navLogin() {
        findNavController().navigate(R.id.action_launcherFragment_to_loginFragment)
    }

    private fun navRegistration() {
        findNavController().navigate(R.id.action_launcherFragment_to_registerFragment)

    }


}
