package apps.hadjou.ilyas.sunhopeapp.recyclerview


import android.content.Context
import android.widget.ImageView
import apps.hadjou.ilyas.sunhopeapp.Glade.GlideApp
import apps.hadjou.ilyas.sunhopeapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.recycler_view_item_1.*

class DateItem( val uid :String,val context: Context) : Item() {
    val storageInstance: FirebaseStorage by lazy {
        FirebaseStorage.getInstance() //princpale root of storage
    }

    private val fireStorInstance: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private val userDocRef: DocumentReference
        get()=fireStorInstance.document("users/${FirebaseAuth.getInstance().currentUser?.uid.toString()}")
    private val contactDocRef: DocumentReference
        get()=fireStorInstance.document("users/${uid}")

    override fun bind(viewHolder: ViewHolder, position: Int) {

        contactDocRef.get().addOnSuccessListener { document ->
            viewHolder.name_date.text= document.get("name").toString()
            if(document.get("imageProfile")!=null){
                GlideApp.with(context)
                    .load(storageInstance.getReference(document.get("imageProfile").toString()))
                    .into(viewHolder.circle_profile_image_1)
            }
            else {

                viewHolder.circle_profile_image_1.setImageResource(R.drawable.ic_account_circle_blue)
            }

        }
        val UserDocRef = userDocRef.collection("dates").document(uid)
        UserDocRef.get().addOnSuccessListener {document ->
            val day = document.get("day").toString()
            val month = document.get("month").toString()
            val hour = document.get("hour").toString()
            val minute = document.get("minute").toString()

            viewHolder.time.text="${hour}:${minute}"
            viewHolder.Date.text="${day}/${month}"
            viewHolder.rendez_vous.text="Rendez Vous"

        }






    }

    override fun getLayout(): Int {
        return R.layout.recycler_view_item_1

    }

}