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
import com.android.myapplication.openapi_codingwithmitch.ui.auth.state.AuthStateEvent
import com.android.myapplication.openapi_codingwithmitch.ui.auth.state.LoginFields
import kotlinx.android.synthetic.main.fragment_login.*

/**
 * A simple [Fragment] subclass.
 */
class LoginFragment : BaseAuthFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "LoginFragment ${viewModel}")
        subscribeObservers()
        login_button.setOnClickListener {
            //we are setting the stateEvent to AttemptLoginEvent
            login()
        }
    }

    fun subscribeObservers() {
        //when the loginFragment comes into the view
        //we want to update the ui
        viewModel.viewState.observe(viewLifecycleOwner, Observer { authViewState ->
            authViewState.loginFields?.let { loginFields ->

                /*
                * update the ui
                * */
                loginFields.login_email?.let { emailString ->
                    input_email.setText(emailString)
                }
                loginFields.login_password?.let { passwordString ->
                    input_password.setText(passwordString)
                }
            }
        })
    }

    fun login(){
        viewModel.setStateEvent(
            AuthStateEvent.LoginAttemptEvent(input_email.text.toString(),
                input_password.text.toString())
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //this will update the field in the viewstate instance and update the livedata ViewState
        viewModel.setLoginFields(
            //taking out the entry in the view and update
            LoginFields(input_email.text.toString(),input_password.text.toString())
        )
    }


}
