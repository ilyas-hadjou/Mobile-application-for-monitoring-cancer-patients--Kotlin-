package apps.hadjou.ilyas.sunhopeapp.recyclerview

import android.content.Context
import apps.hadjou.ilyas.sunhopeapp.Modul.TextMessage
import apps.hadjou.ilyas.sunhopeapp.R
import com.google.firebase.auth.FirebaseAuth
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.recycler_view_item_2.*
import kotlinx.android.synthetic.main.recycler_view_item_3.*

class MessageItem ( val message :TextMessage, val context: Context) : Item() {

    override fun bind(viewHolder: ViewHolder, position: Int) {

        if(message.sender_id== FirebaseAuth.getInstance().currentUser?.uid.toString()){
            viewHolder.myMessage.text=message.textMessage
        }
        else {
            viewHolder.fromMessage.text = message.textMessage
        }
    }

    override fun getLayout(): Int {
       if(message.sender_id==FirebaseAuth.getInstance().currentUser?.uid.toString()){
          return R.layout.recycler_view_item_2
        }
        return R.layout.recycler_view_item_3
    }

}