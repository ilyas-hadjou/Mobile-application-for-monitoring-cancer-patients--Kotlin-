package apps.hadjou.ilyas.sunhopeapp.recyclerview

import android.content.Context
import apps.hadjou.ilyas.sunhopeapp.Glade.GlideApp
import apps.hadjou.ilyas.sunhopeapp.Modul.User
import apps.hadjou.ilyas.sunhopeapp.R
import com.google.firebase.storage.FirebaseStorage
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.recycler_view_item.*


class Chatitem( val uid :String, val user: User, val context: Context) : Item() {
     val storageInstance: FirebaseStorage by lazy {
        FirebaseStorage.getInstance() //princpale root of storage
    }
    override fun bind(viewHolder: ViewHolder, position: Int) {

        viewHolder.nameTextView.text=user.name
        viewHolder.timeView.text="10:15"
        viewHolder.lastMessageView.text="last Message"

        if(user.imageProfile.isNotEmpty()){
            GlideApp.with(context)
                .load(storageInstance.getReference(user.imageProfile))
                    .into(viewHolder.itemImageCircle)

        }
        else {
            viewHolder.itemImageCircle.setImageResource((R.drawable.ic_account_circle_black_24dp))
        }
    }

      override fun getLayout(): Int {
        return R.layout.recycler_view_item

      }

}
