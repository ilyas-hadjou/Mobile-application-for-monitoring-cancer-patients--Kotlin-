package apps.hadjou.ilyas.sunhopeapp.Fragment


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import apps.hadjou.ilyas.sunhopeapp.AddDoctor
import apps.hadjou.ilyas.sunhopeapp.AddPatient
import apps.hadjou.ilyas.sunhopeapp.R
import apps.hadjou.ilyas.sunhopeapp.recyclerview.DateItem
import com.getbase.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.activity_date.*
import kotlinx.android.synthetic.main.fragment_home.*
import java.util.*


class HomeFragment : Fragment() {


    private val userDocRef: DocumentReference
        get()=fireStorInstance.document("users/${FirebaseAuth.getInstance().currentUser?.uid.toString()}")

    private val fireStorInstance: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance() //root of auth
    }
    private val id_Doc_Patient_Ref =fireStorInstance.collection("Codes")

    private lateinit var chatSection : Section
    internal  var number:String?=""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
  // Get the custom view for this fragment layout
        val view = inflater.inflate(R.layout.fragment_home, container, false)

       val first_fab = view.findViewById<FloatingActionButton>(R.id.first_action_button)
       val second_fab = view.findViewById<FloatingActionButton>(R.id.second_action_button)
        val  third_fab =view.findViewById<FloatingActionButton>(R.id. third_action_button)

        // controle floating action bar visibility
        showFab(first_fab,second_fab,third_fab)

        first_fab.setOnClickListener{
            val goToAddDoc = Intent(activity, AddDoctor::class.java)
            startActivity(goToAddDoc)
        }
        second_fab.setOnClickListener{
            val goToAddPatient = Intent(activity, AddPatient::class.java)
            startActivity(goToAddPatient)
        }
        third_fab.setOnClickListener {
            // fet the number
            number="0667276128"
           makePhoneCall("066276128")

        }

        //input the users into recycle view and show them
        addChatListener(::initRecyclerView)
        return view
    }

    private fun addChatListener(onLisent: (List<Item>)->Unit): ListenerRegistration {
        return userDocRef.collection("dates").addSnapshotListener { querySnapshot, FirestoreException ->
            val now = Calendar.getInstance()
            val dateCalnder = Calendar.getInstance()
            var day: String = ""
            var month: String = ""
            var hour: String = ""
            var minute: String = ""
            var year: String
            if (FirestoreException != null) {
                return@addSnapshotListener
            }
            val items = mutableListOf<Item>()
            querySnapshot!!.documents.forEach { document ->

                    day = document.get("day").toString()
                    month = document.get("month").toString()
                    hour = document.get("hour").toString()
                    minute = document.get("minute").toString()
                    year = document.get("year").toString()
                    dateCalnder.set(year.toInt(), month.toInt(), day.toInt(), hour.toInt(), minute.toInt())
                    if (dateCalnder.after(now)) {
                            if(document.exists() && activity!=null ){
                                items.add(DateItem(document.id, activity!!))
                            }
                    }

                    onLisent(items)
                }
            }
    }

    private  fun initRecyclerView(item:List<Item>){
        date_Recycler_View.apply {
            if (activity !=null) {
                layoutManager = LinearLayoutManager(activity)
                adapter = GroupAdapter<ViewHolder>().apply {
                    chatSection = Section(item)
                    add(chatSection)

                }


            }
        }
    }


    private fun showToast(message: String){
        Toast.makeText(activity,message,Toast.LENGTH_SHORT).show()
    }

    fun makePhoneCall(number: String) : Boolean {
        try {
            val intent = Intent(Intent.ACTION_CALL)
            intent.setData(Uri.parse("tel:$number"))
            startActivity(intent)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }


    private fun showFab(first_fab:FloatingActionButton,second_fab:FloatingActionButton, third_fab:FloatingActionButton) {

        userDocRef.get().addOnSuccessListener { document ->
            val firestordata = document.getData()
            val data = firestordata?.get("userid").toString().length
            when(data) {

                8 -> {
                    third_fab.visibility=View.GONE
                }
                6 -> {
                    first_fab.visibility=View.GONE
                    third_fab.visibility=View.GONE
                }
                4 -> {
                    first_fab.visibility=View.GONE
                    second_fab.visibility=View.GONE
                }
                else -> showToast("err reding id")
            }
        }
    }
}
