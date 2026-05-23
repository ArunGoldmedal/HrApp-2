package com.goldmedal.hrapp.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.goldmedal.hrapp.R
import com.goldmedal.hrapp.data.model.SendOtpData
import com.goldmedal.hrapp.databinding.ActivityInputDetailBinding
import com.goldmedal.hrapp.util.generateRandomCaptcha
import com.goldmedal.hrapp.util.getDeviceId
import com.goldmedal.hrapp.util.snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InputDetailsActivity : AppCompatActivity(), AuthListener<Any> {

    private  val viewModel: LoginViewModel by viewModels()
    private lateinit var binding: ActivityInputDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_input_detail)

        binding.viewmodelinput = viewModel

        viewModel.authListener = this

        viewModel.strDeviceId = getDeviceId(this@InputDetailsActivity)
        viewModel.strGeneratedCaptcha =  generateRandomCaptcha()
        binding.tvCaptcha.text = viewModel.strGeneratedCaptcha
    }
    override fun onStarted() {
        binding.progressBar.start()
    }

    override fun onSuccess(_object: List<Any?>) {

        viewOtpScreen(_object as List<SendOtpData>)
        binding.progressBar.stop()
    }

    private fun viewOtpScreen(list: List<SendOtpData>) {

        Intent(this@InputDetailsActivity, VerifyOTPActivity::class.java).also {
            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            it.putExtra("MobileNo", list[0].MobileNo)
            startActivity(it)
        }
    }

    override fun onFailure(message: String) {
        binding.progressBar.stop()
        binding.rootLayout.snackbar(message)
    }

    override fun setCaptcha(strCaptcha: String) {
        binding.tvCaptcha.text = strCaptcha
    }


//    // Construct a request for phone numbers and show the picker
//    private fun requestHint() {
//        val hintRequest = HintRequest.Builder()
//                .setPhoneNumberIdentifierSupported(true)
//                .build()
//        val intent = Auth.CredentialsApi.getHintPickerIntent(
//                mGoogleApiClient, hintRequest)
//        startIntentSenderForResult(intent.intentSender,
//                RESOLVE_HINT, null, 0, 0, 0)
//    }

    // Obtain the phone number from the result
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == RESOLVE_HINT) {
//            if (resultCode == Activity.RESULT_OK) {
//                val credential: Credential = data!!.getParcelableExtra(Credential.EXTRA_KEY)
//                // credential.getId();  <-- will need to process phone number string
//            }
//        }
//    }




}
