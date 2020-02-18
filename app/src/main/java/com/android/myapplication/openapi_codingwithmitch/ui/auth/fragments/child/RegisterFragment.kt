package com.android.myapplication.openapi_codingwithmitch.ui.auth.fragments.child


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.android.myapplication.openapi_codingwithmitch.R
import com.android.myapplication.openapi_codingwithmitch.ui.auth.fragments.BaseAuthFragment
import com.android.myapplication.openapi_codingwithmitch.ui.auth.state.RegistrationFields
import kotlinx.android.synthetic.main.fragment_login.input_email
import kotlinx.android.synthetic.main.fragment_register.*

/**
 * A simple [Fragment] subclass.
 */
class RegisterFragment : BaseAuthFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "RegisterFragment ${viewModel}")

        subscribeObservers()
    }

    fun subscribeObservers() {
        //when the registerfragment comes into the view
        //we want to update the ui
        viewModel.viewState.observe(viewLifecycleOwner, Observer { authViewState ->
            authViewState.registrationFields?.let { registrationFields ->

                /*
                * update the ui using the previous data if available
                * */
                registrationFields.registration_email?.let { emailString ->
                    input_email.setText(emailString)
                }
                registrationFields.registration_confirm_password?.let { passwordString ->
                    input_password_confirm.setText(passwordString)
                }
                registrationFields.registration_password?.let { passwordString ->
                    input_password_register.setText(passwordString)
                }
                registrationFields.registration_username?.let { usernameString ->
                    input_username.setText(usernameString)
                }
            }
        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        //this will update the field in the viewstate instance and update the livedata ViewState
        viewModel.setRegistrationFields(
            //taking out the entry in the view and update
            RegistrationFields(
                input_email.text.toString(),
                input_username.text.toString(),
                input_password_register.text.toString(),
                input_password_confirm.text.toString()
            )

        )
    }
}