package apps.hadjou.ilyas.sunhopeapp.Modul

import java.util.*

data class TextMessage (val textMessage: String,val sender_id:String,val time: Date) {
    constructor():this("","",Date(0))
}