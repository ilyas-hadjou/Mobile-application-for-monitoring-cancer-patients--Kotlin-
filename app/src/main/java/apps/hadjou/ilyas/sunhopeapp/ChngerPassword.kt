package apps.hadjou.ilyas.sunhopeapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_chnger_passwor.*

class ChngerPassword : AppCompatActivity() {


    private val fireStorInstance: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance() //root of auth
    }
    private val userDocRef: DocumentReference
        get()=fireStorInstance.document("users/${FirebaseAuth.getInstance().currentUser?.uid.toString()}")



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chnger_passwor)




        enregistrer_password.setOnClickListener{
            val old_pass_word2=old_password2.text.toString().trim()
            val new_pass_word2=new_password2.text.toString().trim()
            val Cnew_pass_word=Cnew_password.text.toString().trim()
        if(new_pass_word2 == Cnew_pass_word){

            userDocRef.get().addOnSuccessListener { document ->

                val data = document.get("password").toString()
                val original_email = document.get("email").toString()

            if (data == old_pass_word2 ) {

                val user = FirebaseAuth.getInstance().currentUser
                val credential = EmailAuthProvider
                    .getCredential(original_email, old_pass_word2)
                   // Prompt the user to re-provide their sign-in credentials
                    user?.reauthenticate(credential)
                     ?.addOnCompleteListener {

                            user?.updatePassword(new_pass_word2).addOnSuccessListener {

                                Toast.makeText(this, "mot de pass a été mis à jour", Toast.LENGTH_LONG).show()
                                userDocRef.update("password", new_pass_word2)
                                FirebaseAuth.getInstance().signOut()
                                val gotoSigninView = Intent(this, SignIn::class.java)
                                startActivity(gotoSigninView)
                                }.addOnFailureListener {

                                Toast.makeText(this, "Error ${it.message}", Toast.LENGTH_SHORT).show()
                            }

                    }
            }
            else {
            Toast.makeText(this, "L'ancien mot de passe est incorrect data :${data} != ${old_pass_word2} ", Toast.LENGTH_LONG).show()
            }

            }

        }
    else
    {
        Toast.makeText(this, "Les nouveaux mots de passe ne sont pas les mêmes", Toast.LENGTH_SHORT).show()
    }




    }
    }
}
