package apps.hadjou.ilyas.sunhopeapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_sign_in.*
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth



class SignIn : AppCompatActivity() ,TextWatcher {
    private val mAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        //click sur sign Up
        signUp2.setOnClickListener {
            val goToSignUpView = Intent(this@SignIn,SignUp::class.java)
            startActivity(goToSignUpView)
        }

        //click sur sign in
        signIn1.setOnClickListener{

            val userEmail= emailSignIn.text.toString().trim()
            val userpassword= mdpSignIn.text.toString().trim()

            //check if user exists
            mAuth.signInWithEmailAndPassword(userEmail,userpassword).addOnCompleteListener{
                if(it.isSuccessful){
                    val goToHomeView = Intent(this,MainActivity::class.java)
                      Toast.makeText(this, "Welcom", Toast.LENGTH_SHORT).show()
                      goToHomeView.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or  Intent.FLAG_ACTIVITY_CLEAR_TASK)
                      startActivity(goToHomeView)
                }
                else {
                    Toast.makeText(this, it.exception!!.message,Toast.LENGTH_SHORT).show()
                }
            }

        }

        // changed edit text listener
        emailSignIn.addTextChangedListener(this)
        mdpSignIn.addTextChangedListener(this)
    }


    // The user still sign in
    override fun onStart() {
        super.onStart()
        if(mAuth.currentUser?.uid !=null){
            val goToMainView = Intent(this,MainActivity::class.java)
            startActivity(goToMainView)
        }

    }

    //Function...
    override fun afterTextChanged(s: Editable?) {}
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        signIn1.isEnabled=
                          emailSignIn.text.trim().isNotEmpty()
                          && mdpSignIn.text.trim().isNotEmpty()
                          && mdpSignIn.text.length>8
    }

}


