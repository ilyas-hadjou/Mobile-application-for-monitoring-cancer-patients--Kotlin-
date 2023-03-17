package apps.hadjou.ilyas.sunhopeapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import apps.hadjou.ilyas.sunhopeapp.Glade.GlideApp
import apps.hadjou.ilyas.sunhopeapp.Modul.TextMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_chat.*
import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import apps.hadjou.ilyas.sunhopeapp.recyclerview.MessageItem
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ListenerRegistration
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder

class ChatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        // date with patient
        showDate()
        date.setOnClickListener {
            val goToDate =Intent(this,Date::class.java)
            goToDate.putExtra("uid",contact_uid)
            startActivity(goToDate)
        }

        val contact_name=intent.getStringExtra("name")
        val image_profile=intent.getStringExtra("image_Profile")
        contact_uid=intent.getStringExtra("uid")

        // update user name and image profile
        contact_name_view.text=contact_name
        if(image_profile.isNotEmpty()){
            GlideApp.with(this@ChatActivity)
                .load(storageInstance.getReference(image_profile))
                .into(profile_contact_image)
        }
        else
        {
            profile_contact_image.setImageResource(R.drawable.ic_account_circle_blue)
        }

        createChatChannel{channelId->

            image_Send_Button.setOnClickListener {
                val  getText= write_message.text.toString()
                if(getText.isNotEmpty()){
                val  message=TextMessage(getText,FirebaseAuth.getInstance().currentUser?.uid.toString(),java.util.Date())
                write_message.setText("")
                sentMessage(channelId,message)}
            }
            MessageListener(channelId,::initRecyclerView)
        }

        //back to chat Fragment ( list of contact )
        back_image_view.setOnClickListener{ finish()}
        profile_contact_image.setOnClickListener{ finish()}
        contact_name_view.setOnClickListener { finish() }
    }

    //Function...
    private fun sentMessage(channelId:String ,message: TextMessage) {
        chatChannelCollectionRef.document(channelId).collection("Message").add(message)

    }

    private fun MessageListener(chat_channel: String,onLisent: (List<Item>)->Unit): ListenerRegistration {
        val ChatChannel = fireStorInstance.collection("ChatChannel").document(chat_channel).collection("Message")
        return ChatChannel.orderBy("time").addSnapshotListener { querySnapshot, FirestoreException ->

            if (FirestoreException != null) {
                return@addSnapshotListener
            }
            val items = mutableListOf<Item>()
            querySnapshot!!.documents.forEach { document ->


                        items.add(MessageItem(document.toObject(TextMessage::class.java)!!, this@ChatActivity!!))

                }

                onLisent(items)
            }
    }



    private  fun initRecyclerView(item:List<Item>){
        chat_recycler_view.apply {
            if (this!=null) {
                layoutManager = LinearLayoutManager(this@ChatActivity)
                adapter = GroupAdapter<ViewHolder>().apply {
                    chatSection = Section(item)
                    add(chatSection)

                }


            }
        }
    }
    private fun createChatChannel(onComplete:(chnnalId:String)->Unit){

        // create channel id unique for all message into two users
        val user_id = FirebaseAuth.getInstance().currentUser!!.uid
        val newChatChannel = fireStorInstance.collection("users").document()

        fireStorInstance.collection("users")
            .document(user_id)
            .collection("chatChannel")
            .document(contact_uid)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    onComplete(document["channelId"] as String)
                    return@addOnSuccessListener
                }

                fireStorInstance.collection("users")
                    .document(contact_uid)
                    .collection("chatChannel")
                    .document(user_id)
                    .set(mapOf("channelId" to newChatChannel.id))

                fireStorInstance.collection("users")
                    .document(user_id)
                    .collection("chatChannel")
                    .document(contact_uid)
                    .set(mapOf("channelId" to newChatChannel.id))

                onComplete(newChatChannel.id)
            }
    }
    private fun showDate() {

        userDocRef.get().addOnSuccessListener { document ->
            val firestordata = document.getData()
            val data = firestordata?.get("userid").toString().length
            if (data == 6 || data== 8 ) {
                date.visibility = View.VISIBLE
            }
        }
    }


    //declaration
    private lateinit var chatSection : Section

    private val storageInstance: FirebaseStorage by lazy {
        FirebaseStorage.getInstance() //princpale root of storage
    }
    private val fireStorInstance: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance() //root of auth
    }
    private val chatChannelCollectionRef =fireStorInstance.collection("ChatChannel")

    private val userDocRef: DocumentReference
        get()=fireStorInstance.document("users/${FirebaseAuth.getInstance().currentUser?.uid.toString()}")

    // variable
    var contact_uid:String=""
}
