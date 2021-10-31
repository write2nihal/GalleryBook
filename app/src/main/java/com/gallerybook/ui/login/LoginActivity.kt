package com.gallerybook.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.percentlayout.widget.PercentLayoutHelper.PercentLayoutInfo
import androidx.percentlayout.widget.PercentRelativeLayout.LayoutParams
import com.gallerybook.room.GalleryDatabase
import com.gallerybook.room.GalleryScope
import com.gallerybook.BaseActivity
import com.gallerybook.R
import com.gallerybook.databinding.ActivityLoginBinding
import com.gallerybook.ui.dashboard.DashBoardActivity
import com.gallerybook.utils.Validator
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

/**
 * Created by Nihal Srivastava on 30/10/21.
 */
class LoginActivity : BaseActivity(), View.OnClickListener, CoroutineScope {

    private var isLoggedIn: Boolean? = false
    private lateinit var database: GalleryDatabase
    private lateinit var loginBinding: ActivityLoginBinding
    private var isSignInScreen = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginBinding = DataBindingUtil.inflate(layoutInflater, R.layout.activity_login, null, false)
        setContentView(loginBinding.root)
        database = GalleryDatabase.initACDatabase(this)

    }

    override fun onStart() {
        super.onStart()
        loginBinding.llSignin.setOnClickListener(this)
        loginBinding.tvSignupInvoker.setOnClickListener(this)
        loginBinding.tvSigninInvoker.setOnClickListener(this)
        loginBinding.llSignupContent.btnSignup.setOnClickListener(this)
        loginBinding.llSigninContent.btnSignin.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnSignin -> {
                isLoggedIn = false
                validateLogin()
            }
            R.id.tvSigninInvoker -> {
                isSignInScreen = true
                showSignInForm()
            }
            R.id.tvSignupInvoker -> {
                isSignInScreen = false
                showSignupForm()
            }
            R.id.btnSignup -> {
                isLoggedIn = true
                validateSignUp()
            }
        }
        try {

            val methodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            val focusedView: View = currentFocus!!
            methodManager.hideSoftInputFromWindow(
                focusedView.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun validateLogin() {
        if (loginBinding.llSigninContent.etEmail.text.toString().isEmpty()) {
            loginBinding.llSigninContent.etEmail.error = "Please enter email"
        } else if (loginBinding.llSigninContent.etPassword.text.toString().isEmpty()) {
            loginBinding.llSigninContent.etPassword.error = "Please enter password"
        } else if (!Validator.isValidEmail(
                loginBinding.llSigninContent.etEmail.text.toString().trim()
            )
        ) {
            loginBinding.llSigninContent.etEmail.error = "Please enter valid email"
        } else if ((loginBinding.llSigninContent.etPassword.text.toString().trim()).length < 8) {
            loginBinding.llSigninContent.etPassword.error = "Please choose strong password"
        } else {
            loginDetails()
        }
    }

    private fun loginDetails() {
        val scope = async {
            val value = database.galleryDao().fetchStoredUserDetails(
                loginBinding.llSigninContent.etEmail.text.toString(),
                loginBinding.llSigninContent.etPassword.text.toString().trim()
            )
            withContext(Dispatchers.Main) {
                updateUi(value)
            }
        }
    }

    private fun updateUi(value: List<GalleryScope>) {
        if (value.isNotEmpty()) {
            val intent = Intent(this, DashBoardActivity::class.java)
            startActivity(intent)
            this.finish()
        } else {
            Toast.makeText(this, "No Records Founds...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showSignupForm() {
        val paramsLogin: LayoutParams = loginBinding.llSignin.layoutParams as LayoutParams
        val infoLogin: PercentLayoutInfo = paramsLogin.percentLayoutInfo

        infoLogin.widthPercent = 0.15f
        loginBinding.llSignin.requestLayout()
        val paramsSignup: LayoutParams = loginBinding.llSignup.layoutParams as LayoutParams
        val infoSignup: PercentLayoutInfo = paramsSignup.percentLayoutInfo
        infoSignup.widthPercent = 0.85f
        loginBinding.llSignup.requestLayout()
        loginBinding.tvSignupInvoker.visibility = View.GONE
        loginBinding.tvSigninInvoker.visibility = View.VISIBLE
        val translate = AnimationUtils.loadAnimation(
            applicationContext, R.anim.translate_right_to_left
        )
        loginBinding.llSignup.startAnimation(translate)
        val clockwise = AnimationUtils.loadAnimation(
            applicationContext, R.anim.rotate_right_to_left
        )
        loginBinding.llSignupContent.btnSignup.startAnimation(clockwise)
    }

    private fun validateSignUp() {
        if (loginBinding.llSignupContent.etNameSignup.text.toString().isEmpty()) {
            loginBinding.llSignupContent.etNameSignup.error = "Please enter name"
        } else if (loginBinding.llSignupContent.etEmailSignup.text.toString().isEmpty()) {
            loginBinding.llSignupContent.etEmailSignup.error = "Please enter email"
        } else if (loginBinding.llSignupContent.etPasswordSignup.text.toString().isEmpty()) {
            loginBinding.llSignupContent.etPasswordSignup.error = "Please enter password"
        } else if (loginBinding.llSignupContent.etConfirmPassSignup.text.toString().trim()
                .isEmpty()
        ) {
            loginBinding.llSignupContent.etConfirmPassSignup.error = "Please confirm password"
        } else if (!Validator.isValidEmail(
                loginBinding.llSignupContent.etEmailSignup.text.toString().trim()
            )
        ) {
            loginBinding.llSignupContent.etEmailSignup.error = "Please enter valid email"
        } else if (!Validator.isValidPassword(
                loginBinding.llSignupContent.etPasswordSignup.text.toString().trim()
            )
        ) {
            loginBinding.llSignupContent.etPasswordSignup.error = "Please choose strong password"
        } else if (loginBinding.llSignupContent.etMobileSignup.text.toString().trim().isEmpty()) {
            loginBinding.llSignupContent.etMobileSignup.error = "Please enter  mobile number"
        } else if (!Validator.isValidPhone(
                loginBinding.llSignupContent.etMobileSignup.text.toString().trim()
            )
        ) {
            loginBinding.llSignupContent.etMobileSignup.error = "Please enter valid mobile number"
        } else if (loginBinding.llSignupContent.etPasswordSignup.text.toString()
                .trim() != loginBinding.llSignupContent.etConfirmPassSignup.text.toString().trim()
        ) {
            loginBinding.llSignupContent.etConfirmPassSignup.error = "Password mismatch"
        } else {
            var papa = async {

                database.galleryDao().insertOperation(
                    GalleryScope(
                        0,
                        loginBinding.llSignupContent.etNameSignup.text.toString().trim(),
                        loginBinding.llSignupContent.etEmailSignup.text.toString().trim(),
                        loginBinding.llSignupContent.etPasswordSignup.text.toString().trim(),
                        loginBinding.llSignupContent.etMobileSignup.text.toString().trim(),
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "")
                )
            }
            papa.onAwait
            clearFields()

        }
    }

    private fun clearFields() {
        loginBinding.llSignupContent.etNameSignup.setText("")
        loginBinding.llSignupContent.etEmailSignup.setText("")
        loginBinding.llSignupContent.etPasswordSignup.setText("")
        loginBinding.llSignupContent.etMobileSignup.setText("")
        Toast.makeText(this, "Registration Done. please login...", Toast.LENGTH_SHORT).show()
        showSignInForm()
    }

    private fun showSignInForm() {
        val paramsLogin: LayoutParams =
            loginBinding.llSignin.layoutParams as LayoutParams
        val infoLogin: PercentLayoutInfo = paramsLogin.percentLayoutInfo
        infoLogin.widthPercent = 0.85f
        loginBinding.llSignin.requestLayout()
        val paramsSignup: LayoutParams =
            loginBinding.llSignup.layoutParams as LayoutParams
        val infoSignup: PercentLayoutInfo = paramsSignup.percentLayoutInfo
        infoSignup.widthPercent = 0.15f
        loginBinding.llSignup.requestLayout()
        val translate = AnimationUtils.loadAnimation(
            applicationContext, R.anim.translate_left_to_right
        )
        loginBinding.llSignin.startAnimation(translate)
        loginBinding.tvSignupInvoker.visibility = View.VISIBLE
        loginBinding.tvSigninInvoker.visibility = View.GONE
        val clockwise = AnimationUtils.loadAnimation(
            applicationContext, R.anim.rotate_left_to_right
        )
        loginBinding.llSigninContent.btnSignin.startAnimation(clockwise)
    }

    override val coroutineContext: CoroutineContext =
        Dispatchers.IO + SupervisorJob()
}