package apps.hadjou.ilyas.sunhopeapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_add_doctor.*

class AddDoctor : AppCompatActivity() {

    private val fireStorInstance: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance() //root of auth
    }

    private val idDoctorRef =fireStorInstance.collection("Codes")
    var rnds:Int=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_doctor)

        genere_button.setOnClickListener {
            rnds = (100000..999999).random()
            code_generer.setText(rnds.toString())
        }
        ajouter_button.setOnClickListener {

            confermation(rnds)
        }

    }

    private fun showToast(message: String){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show()
    }

    private fun confermation(doc_id:Int){
        val docRef = idDoctorRef.document("doctorid")

        docRef.get().addOnSuccessListener { document ->
            val firestordata = document.getData()
            val data = firestordata?.get(doc_id.toString()).toString()
            if (data != "null") {
                //check id
                showToast("Le code généré [${data}]est existe déja ,réessayer à nouveau")
            }
            else{
                val info= mapOf<String,Int>(doc_id.toString() to doc_id)
                idDoctorRef.document("doctorid").update(info)
                showToast("Code [${data}] ajouté")

            }
        }.addOnFailureListener {
            Toast.makeText(this, "Error ${it.message}", Toast.LENGTH_SHORT).show()
        }

    }
}

