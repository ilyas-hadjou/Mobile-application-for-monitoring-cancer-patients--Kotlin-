package apps.hadjou.ilyas.sunhopeapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_add_patient.*

class AddPatient : AppCompatActivity() {

    private val fireStorInstance: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance() //root of auth
    }

    private val idPatienttorRef =fireStorInstance.collection("Codes")
    var rnds:Int=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_patient)

        genere_patient_button.setOnClickListener {
            rnds = (1000..9999).random()
            code.setText(rnds.toString())
        }
        ajouter_patient_button.setOnClickListener {

            confermation(rnds)
        }

    }

    private fun showToast(message: String){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show()
    }

    private fun confermation(doc_id:Int){
        val docRef = idPatienttorRef.document("Patientid")

        docRef.get().addOnSuccessListener { document ->
            val firestordata = document.getData()
            val data = firestordata?.get(doc_id.toString()).toString()
            if (data != "null") {
                //check id
                showToast("Le code généré [${data}]est existe déja ,réessayer à nouveau")
            }
            else{
                val info= mapOf<String,Int>(doc_id.toString() to doc_id)
                idPatienttorRef.document("Patientid").update(info)
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Error ${it.message}", Toast.LENGTH_SHORT).show()
        }

    }
}