package apps.hadjou.ilyas.sunhopeapp

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_sign_up.*


class SignUp : AppCompatActivity() ,TextWatcher {

    // declaration

    private val mAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    val db = FirebaseFirestore.getInstance()
    val patientRef = db.collection("Codes").document("Patientid")
    val docRef = db.collection("Codes").document("Patientid")

    // On create
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        //click sur sign up
        signUp1.setOnClickListener{
            if (validate()) {

                val userEmail = emailSignUp.text.toString().trim()
                val userpassword = mdpSignUp.text.toString().trim()
                val userId = idSignUp.text.toString().trim()
                val username=username.text.toString().trim()

                // accès a la base des données
                val idRef = db.collection("Codes").document("Patientid")

                     //check id
                    if (confermation(userId)) {

                        // creation of user
                        mAuth.createUserWithEmailAndPassword(userEmail, userpassword).addOnCompleteListener {
                            if (it.isSuccessful) {

                                // Remove the user id field from the document
                                val updates = hashMapOf<String, Any>(
                                    userId to FieldValue.delete()
                                )
                                      patientRef.update(updates).addOnCompleteListener {

                                      }.addOnFailureListener {
                                          docRef.update(updates)
                                        }


                                val info = mutableMapOf<String,Any>(
                                    "name" to username,
                                     "email" to userEmail,
                                      "password" to userpassword,
                                       "userid" to userId)

                                         // update info on database with document = UID of Auth
                                          db.collection("users").document(mAuth.currentUser?.uid.toString()).set(info)

                                         // go to edit main activity ( edit Fragment )
                                        val goToEditView = Intent(this, MainActivity::class.java)
                                      Toast.makeText(this, "Welcom", Toast.LENGTH_SHORT).show()

                                    // finish activity sign up
                                  goToEditView.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                startActivity(goToEditView)
                            }
                            else {
                                Toast.makeText(this, it.exception!!.message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    else {
                        Toast.makeText(this, "id non existe", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        // changed edit text listener
        idSignUp.addTextChangedListener(this)
        mdpSignUp.addTextChangedListener(this)
        mdpSignUp2.addTextChangedListener(this)
        username.addTextChangedListener(this)
    }



    //Function...
    override fun afterTextChanged(s: Editable?) {}
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        signUp1.isEnabled=
                          idSignUp.text.trim().isNotEmpty()
                          && mdpSignUp.text.trim().isNotEmpty()
                          && mdpSignUp2.text.trim().isNotEmpty()
                          && username.text.trim().isNotEmpty()
                          && mdpSignUp.text.length>=8
                          && mdpSignUp2.text.length>=8
    }

    private fun validate(): Boolean {
        var temp = true
        val pass = mdpSignUp.getText().toString()
        val cpass = mdpSignUp2.getText().toString()
           if (pass != cpass) {
              Toast.makeText(this, "Password Not matching", Toast.LENGTH_SHORT).show()
               temp = false
           }
        return temp
    }

    private fun confermation(userid : String):Boolean{
     var temp=true

        // si id don't existe in patient id then will be search in doctor id
        patientRef.get().addOnSuccessListener { document ->
            val firestordata = document.getData()
            val data = firestordata?.get(userid).toString()
            if (data != "null") {
                //check id
                Toast.makeText(this, "id [${userid}] is existe ", Toast.LENGTH_SHORT).show()
            }
            else{
                docRef.get().addOnSuccessListener { document ->
                    val firestordata = document.getData()
                    val data = firestordata?.get(userid).toString()
                    if (data != "null") {
                        //check id
                        Toast.makeText(this, "id [${userid}] is existe ", Toast.LENGTH_SHORT).show()
                    }
                    else{
                        temp=false
                    }
                }.addOnFailureListener {
                    Toast.makeText(this, "Error ${it.message}", Toast.LENGTH_SHORT).show()
                }

            }
        }.addOnFailureListener {
            Toast.makeText(this, "Error ${it.message}", Toast.LENGTH_SHORT).show()
        }


        return temp
    }




}







