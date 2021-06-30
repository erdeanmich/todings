package com.erdeanmich.todings.login.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.erdeanmich.todings.R
import com.erdeanmich.todings.login.view.LoginViewModel
import com.erdeanmich.todings.overview.activities.OverviewActivity
import com.erdeanmich.todings.web.service.Retrofit
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.*
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
import java.util.*

class LoginActivity : AppCompatActivity() {
    private val inputDelay = 1500L
    private var mailDebounceJob: Job? = null
    private var passwordDebounceJob: Job? = null
    private var isFirstMailCheck = true
    private var isFirstPasswordCheck = true
    private val loginButton: Button by lazy { findViewById(R.id.login_button) }
    private val mailLayout: TextInputLayout by lazy { findViewById(R.id.login_mail_input_layout) }
    private val mailInput: TextInputEditText by lazy { findViewById(R.id.login_mail_edit_text) }
    private val passwordLayout: TextInputLayout by lazy { findViewById(R.id.login_password_input_layout) }
    private val passwordInput: TextInputEditText by lazy { findViewById(R.id.login_password_edit_text) }
    private val progressSpinner: ProgressBar by lazy { findViewById(R.id.progressSpinner) }
    private val loginError: TextView by lazy { findViewById(R.id.login_error) }
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val socket = Socket()
                socket.connect(InetSocketAddress("192.168.0.243", 8080), 10000)
                socket.close()
            } catch (e: Exception) {
                showOverview(true)
            }

        }

        loginButton.isEnabled = false
        initTextWatchers()
        initLoginButton()
        initViewModelObservers()

    }

    private fun initViewModelObservers() {
        viewModel.getMailValidStatus().observe(this, { isValid ->
            if (isValid || isFirstMailCheck) {
                isFirstMailCheck = false
                mailLayout.error = null
            } else {
                mailLayout.error = "Please enter a valid email address"
            }
        })

        viewModel.getPasswordValidStatus().observe(this, { isValid ->
            if (isValid || isFirstPasswordCheck) {
                passwordLayout.error = null
                isFirstPasswordCheck = false
            } else {
                passwordLayout.error = "Please enter a 6-digit password"
            }
        })

        viewModel.getValidStatus().observe(this, { isValid ->
            loginButton.isEnabled = isValid
        })
    }

    private fun initLoginButton() {
        loginButton.setOnClickListener {
            progressSpinner.visibility = View.VISIBLE
            lifecycleScope.launch(Dispatchers.IO) {
                val response = viewModel.login()
                if (!response.isSuccessful) {
                    return@launch
                }
                delay(3000L)
                progressSpinner.visibility = View.INVISIBLE

                if (response.body() == false) {
                    withContext(Dispatchers.Main) {
                        loginError.visibility = View.VISIBLE
                    }
                    return@launch
                }

                val localItems = viewModel.getToDosInDB()
                if (localItems.isEmpty()) {
                    viewModel.getRemoteToDos()
                } else {
                    viewModel.syncLocalToDosToRemote()
                }

                showOverview()
            }
        }
    }

    private fun showOverview(localOnly: Boolean = false) {
        val intent = Intent(this, OverviewActivity::class.java)
        intent.putExtra("localOnly", localOnly)
        startActivity(intent)
    }

    private fun initTextWatchers() {
        mailInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                loginError.visibility = View.INVISIBLE
            }

            override fun afterTextChanged(s: Editable?) {
                mailLayout.error = null
                mailDebounceJob?.cancel()
                mailDebounceJob = lifecycleScope.launch(Dispatchers.Main) {
                    delay(inputDelay)
                    viewModel.setMail(s.toString())
                }
            }

        })

        passwordInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                loginError.visibility = View.INVISIBLE
            }

            override fun afterTextChanged(s: Editable?) {
                passwordLayout.error = null
                passwordDebounceJob?.cancel()
                passwordDebounceJob = lifecycleScope.launch(Dispatchers.Main) {
                    delay(inputDelay)
                    viewModel.setPassword(s.toString())
                }
            }
        })
    }
}