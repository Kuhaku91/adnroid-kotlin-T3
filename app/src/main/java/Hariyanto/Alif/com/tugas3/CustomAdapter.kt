package Hariyanto.Alif.com.tugas3

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso

class CustomAdapter (val context : Context, arrayList: ArrayList<HashMap<String, Any>>) : BaseAdapter() {
    val F_NAME = "file_name"
    val F_TYPE = "file_type"
    val F_URL = "file_url"
    val list = arrayList
    var uri = Uri.EMPTY

    inner class ViewHolder() {
        var txFileName: TextView? = null
        var txFileType: TextView? = null
        var txFileUrl: TextView? = null
        var imv: ImageView? = null
    }

    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(position: Int): Any {
        return list.get(position)
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var holder = ViewHolder()
        var view =convertView
        if (convertView == null){
            var inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)
                    as LayoutInflater
            view = inflater.inflate(R.layout.row_data,null,true)
            holder.txFileName = view!!.findViewById(R.id.txFileName) as TextView
            holder.txFileType = view!!.findViewById(R.id.txFileType) as TextView
            holder.txFileUrl = view!!.findViewById(R.id.txFileUrl) as TextView
            holder.imv = view!!.findViewById(R.id.imv) as ImageView

            view.tag = holder
        }else{
            holder = view!!.tag as ViewHolder
        }
        var fileType = list.get(position).get(F_TYPE).toString()
        uri = Uri.parse(list.get(position).get(F_URL).toString())

        holder.txFileName!!.setText(list.get(position).get(F_NAME).toString())
        holder.txFileType!!.setText(fileType)
        holder.txFileUrl!!.setText(uri.toString())
        holder.txFileUrl!!.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW).setData(
                Uri.parse(holder.txFileUrl!!.text.toString())
            )
            context.startActivities(arrayOf(intent))
        }
        when(fileType){
            ".pdf" -> {holder.imv!!.setImageResource(android.R.drawable.ic_dialog_dialer)}
            ".docx" -> {holder.imv!!.setImageResource(android.R.drawable.ic_menu_edit)}
            ".mp4" -> {holder.imv!!.setImageResource(android.R.drawable.ic_media_play)}
            ".jpg" -> {Picasso.get().load(uri).into(holder.imv)}

        }
        return view!!
    }

}