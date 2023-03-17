package apps.hadjou.ilyas.sunhopeapp

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_date.*
import java.util.*

class Date : AppCompatActivity() {

    //declaration

    private val fireStorInstance: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance() //root of auth
    }
    private val userDocRef: DocumentReference
        get()=fireStorInstance.document("users/${FirebaseAuth.getInstance().currentUser?.uid.toString()}")

    private var contact_uid:String=""
    private var Year= 0
    private var Day=0
    private var Month=0
    private var Hour=0
    private var Minute=0



    @SuppressLint("SuspiciousIndentation")
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_date)

        val now = Calendar.getInstance()
        temp_button.setOnClickListener {
          val timePiker= TimePickerDialog(this, TimePickerDialog.OnTimeSetListener{ view, hourOfDay, minute->

                time_text.setText("${hourOfDay}:${minute}")
                Hour=hourOfDay
                Minute=minute
            },
            now.get(Calendar.HOUR_OF_DAY),now.get(Calendar.MINUTE),true)
            timePiker.show()
        }
        date_button.setOnClickListener {
            val datePicker=DatePickerDialog(this, DatePickerDialog.OnDateSetListener{view,year,month,dayOfMonth->
                date_text.setText("${dayOfMonth}/${month+1}/${year}")
                Year=year
                Month=month+1
                Day=dayOfMonth

            },
                now.get(Calendar.YEAR),now.get(Calendar.MONTH),now.get(Calendar.DAY_OF_MONTH))

            datePicker.show()
        }

        Enregistrer_Date.setOnClickListener {
            contact_uid =intent.getStringExtra("uid")
            val contactDocRef = fireStorInstance.collection("users").document(contact_uid)

            val info = mutableMapOf<String,Any>(
                "year" to Year,
                "month" to Month,
                "day" to Day,
                "hour" to Hour,
                "minute" to Minute
            )

             //update info on database with document = UID of Auth
           userDocRef
                .collection("dates")
                .document(contact_uid)
                .set(info)
            contactDocRef
                .collection("dates")
                .document(FirebaseAuth.getInstance().currentUser?.uid.toString())
                .set(info)

            showToast("Le Rendez-Vous a été créé avec [${Day}/${Month}/${Year}] à [${Hour}:${Minute}]")



        }




    }
    private fun showToast(message: String){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show()
    }
}
