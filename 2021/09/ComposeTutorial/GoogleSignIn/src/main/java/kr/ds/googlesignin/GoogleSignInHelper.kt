package kr.ds.googlesignin

import android.app.Activity.RESULT_OK
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import org.json.JSONObject
import timber.log.Timber

class GoogleSignInHelper(private val activity: ComponentActivity, private val callback: (String) -> Unit) {

    private var mGoogleSignInClient: GoogleSignInClient? = null

    private val requestGoogleSignInLauncher = activity.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            if (RESULT_OK == it.resultCode) {
                handleSignInResult(task)
            } else {
                val message = task.result.toString()
                Timber.d(message)
                Firebase.crashlytics.log(message)
                Firebase.crashlytics.recordException(task.exception ?: Exception("GoogleSignIn Fail"))
            }
        } catch (e: Exception) {
            Timber.e(e.localizedMessage)
            Firebase.crashlytics.recordException(e)
        }
    }

    fun prepareGoogleSignIn(serverClientId: String) {
        // [START configure_signin]
        // Request only the user's ID token, which can be used to identify the
        // user securely to your backend. This will contain the user's basic
        // profile (name, profile picture URL, etc) so you should not need to
        // make an additional call to personalize your application.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(serverClientId)
            .requestEmail()
            .build()

        // [END configure_signin]

        // Build GoogleAPIClient with the Google Sign-In API and the above options.
        mGoogleSignInClient = GoogleSignIn.getClient(activity, gso)
    }

    fun getIdToken() {
        // Show an account picker to let the user choose a Google account from the device.
        // If the GoogleSignInOptions only asks for IDToken and/or profile and/or email then no
        // consent screen will be shown here.
        val signInIntent = mGoogleSignInClient!!.signInIntent
        requestGoogleSignInLauncher.launch(signInIntent)
    }

    fun refreshIdToken() {
        // Attempt to silently refresh the GoogleSignInAccount. If the GoogleSignInAccount
        // already has a valid token this method may complete immediately.
        //
        // If the user has not previously signed in on this device or the sign-in has expired,
        // this asynchronous branch will attempt to sign in the user silently and get a valid
        // ID token. Cross-device single sign on will occur in this branch.
        mGoogleSignInClient!!.silentSignIn()
            .addOnCompleteListener(
                activity
            ) { task -> handleSignInResult(task) }
    }

    // [START handle_sign_in_result]
    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            val idToken = account!!.idToken
            Timber.d("account:${account.zab()}\nidToken:$idToken, email:${account.email}, name:${account.displayName}, photoUrl:${account.photoUrl}")

            val obj = JSONObject()
            obj.apply {
                put("type", "google")
                put("tokenId", account.idToken)
            }
            Timber.d("json: $obj")
            val accountInfo = obj.toString()
            callback.invoke(accountInfo)
        } catch (e: ApiException) {
            Timber.e(e, "handleSignInResult:error")
            Firebase.crashlytics.recordException(e)
        }
    }
    // [END handle_sign_in_result]
}