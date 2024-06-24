import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.Timestamp
import com.shahbaz.letstalk.R
import com.shahbaz.letstalk.databinding.ReceiveMessageLayoutBinding
import com.shahbaz.letstalk.databinding.SendMessageLayoutBinding
import com.shahbaz.letstalk.datamodel.MessageModel
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class MessageAdapter(options: FirestoreRecyclerOptions<MessageModel>, private val uid: String) :
    FirestoreRecyclerAdapter<MessageModel, RecyclerView.ViewHolder>(options) {

    private val ITEM_SEND = 1
    private val ITEM_RECEIVED = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ITEM_SEND)
            SendViewholder(
                SendMessageLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        else
            ReceivedViewholder(
                ReceiveMessageLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, model: MessageModel) {
        if (getItemViewType(position) == ITEM_SEND) {
            val viewHolder = holder as SendViewholder
            viewHolder.bind(model)
        } else {
            val viewHolder = holder as ReceivedViewholder
            viewHolder.bind(model)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (uid == getItem(position).senderId) ITEM_SEND else ITEM_RECEIVED
    }

    inner class SendViewholder(private val binding: SendMessageLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(model: MessageModel) {
            binding.sendMessage.text = model.message
            binding.timeTv.text= timeExtractor(model.timestamp)
        }
    }

    inner class ReceivedViewholder(private val binding: ReceiveMessageLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(model: MessageModel) {
            binding.receivedMessage.text = model.message
            binding.timeTv.text= timeExtractor(model.timestamp)
        }
    }

    companion object {
        @RequiresApi(Build.VERSION_CODES.O)
        fun timeExtractor(timestamp: Timestamp): String {
            // Convert Timestamp to Date
            val date = timestamp.toDate()

            // Convert Date to Instant
            val instant = date.toInstant()

            // Convert Instant to LocalDateTime
            val dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())

            // Extract hour and minute
            val hour = dateTime.hour
            val minute = dateTime.minute

            // Format the hour and minute as "HH:mm"
            return String.format("%02d:%02d", hour, minute)
        }
    }
}
