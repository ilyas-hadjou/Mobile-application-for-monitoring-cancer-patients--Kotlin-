package apps.hadjou.ilyas.sunhopeapp.Fragment


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import apps.hadjou.ilyas.sunhopeapp.ChatActivity
import apps.hadjou.ilyas.sunhopeapp.Glade.GlideApp
import apps.hadjou.ilyas.sunhopeapp.Modul.User
import apps.hadjou.ilyas.sunhopeapp.R
import apps.hadjou.ilyas.sunhopeapp.recyclerview.Chatitem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.storage.FirebaseStorage
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.OnItemClickListener
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.fragment_chat.*




class ChatFragment : Fragment() {

    // declaration...
    private val fireStorInstance: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance() //root of auth
    }
    private val storageInstance: FirebaseStorage by lazy {
        FirebaseStorage.getInstance() //princpale root of storage
    }
    private val userDocRef: DocumentReference
        get()=fireStorInstance.document("users/${FirebaseAuth.getInstance().currentUser?.uid.toString()}")
    private lateinit var chatSection : Section

    // on Create View function
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Get the custom view for this fragment layout
        val view = inflater.inflate(R.layout.fragment_chat, container, false)

        // glade the profile pgoto
        gladeprofileimage()
        glade_user_name()

        //input the users into recycle view and show them
        userDocRef.get().addOnSuccessListener { document ->
            val firestordata = document.getData()
            val data = firestordata?.get("userid").toString()
            addChatListener(data,::initRecyclerView)
        }


        return view
    }


    // Function...

    // return data of user to put them in recycle View
    private fun addChatListener(data:String,onLisent: (List<Item>)->Unit):ListenerRegistration {
        return fireStorInstance.collection("users").addSnapshotListener{querySnapshot, FirestoreException ->

            if(FirestoreException != null){
                return@addSnapshotListener
            }
            val items= mutableListOf<Item>()
              querySnapshot!!.documents.forEach{document_Contact ->
                  val test = document_Contact["userid"].toString()
                if(data.length==4 && activity!= null && document_Contact.exists()){
                    if(test.length != 4) {
                        items.add(Chatitem(document_Contact.id, document_Contact.toObject(User::class.java)!!, activity!!))
                    }
                }
                else{
                    if (data!=test) {
                        items.add(Chatitem(document_Contact.id,document_Contact.toObject(User::class.java)!!, activity!!))
                    }
                }
             }

            onLisent(items)
        }

    }

    private  fun initRecyclerView(item:List<Item>){
        chat_Recycler_View.apply {
            if (activity != null) {
                layoutManager = LinearLayoutManager(activity)
                adapter = GroupAdapter<ViewHolder>().apply {
                    chatSection = Section(item)
                    add(chatSection)
                    setOnItemClickListener(onItemClick)
                }


            }
        }

    }

    //when user click on item
    private val onItemClick=OnItemClickListener{item,view ->
        if(item is Chatitem) {
            val goToChat = Intent(activity, ChatActivity::class.java)
            goToChat.putExtra("name",item.user.name)
            goToChat.putExtra("image_Profile",item.user.imageProfile)
            goToChat.putExtra("uid",item.uid)

            activity!!.startActivity(goToChat)

        }
    }

    //
    private fun gladeprofileimage(){

        fireStorInstance.document("users/${FirebaseAuth.getInstance().currentUser?.uid.toString()}")
            .get()
            .addOnSuccessListener {
                val user = it.toObject(User::class.java)!!
                val tv = view!!.findViewById<ImageView>(R.id.circleImageView)
                if(user.imageProfile.isNotEmpty()){
                    GlideApp.with(activity!!)
                        .load(storageInstance.getReference(user.imageProfile))
                        .into(tv)
                }
                else {
                    tv.setImageResource(R.drawable.ic_account_circle_blue)
                }

            }
    }


    private fun showDoc():Boolean {
        var data : Int=0
        userDocRef.get().addOnSuccessListener { document ->
            val firestordata = document.getData()
             data = firestordata?.get("userid").toString().length
        }
        if(data ==4) {
            return true
        }
        return false
    }

    private fun showToast(message: String){
        Toast.makeText(activity,message, Toast.LENGTH_SHORT).show()
    }

    private fun glade_user_name(){
        fireStorInstance.document("users/${FirebaseAuth.getInstance().currentUser?.uid.toString()}")
            .get()
            .addOnSuccessListener {
                val user = it.toObject(User::class.java)!!
                val user_name=view!!.findViewById<TextView>(R.id.rendez_vous)
                user_name.setText(user.name)
            }
    }

}

