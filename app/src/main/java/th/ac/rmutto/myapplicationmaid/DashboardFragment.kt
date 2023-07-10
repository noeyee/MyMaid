package th.ac.rmutto.myapplicationmaid
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.text.DecimalFormat
import java.text.NumberFormat


class DashboardFragment : Fragment() {

    var recyclerView: RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)

        //For an synchronous task
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val sharedPrefer = requireContext().getSharedPreferences(
            "appPrefer", Context.MODE_PRIVATE
        )
        var custID = sharedPrefer?.getString("custIDPref", null)

//      Log.d("tag", custID!!)
        Log.d("tag", "a1")
        recyclerView = root.findViewById(R.id.recyclerView)

        showDataList(custID!!)
        return root
    }

    fun showDataList(custID: String) {
        val data = ArrayList<Data>()
        val url: String = getString(R.string.root_url) + getString(R.string.Providerhistory_url) + custID
        Log.d("tag", url)
        val okHttpClient = OkHttpClient()
        val request: Request = Request.Builder()
            .url(url).
            get().
            build()

        try {
            val response = okHttpClient.newCall(request).execute()
            Log.e("tag", "x2")

            if (response.isSuccessful) {
                try {
                    val res = JSONArray(response.body!!.string())
                    if (res.length() > 0) {

                        var tempRequestID = 0
                        for (i in 0 until res.length()) {

                            val item: JSONObject = res.getJSONObject(i)
                            var dateStart = item.getString("dateStart")
                            if(dateStart == "null") dateStart = "-"

                            var requestID =  item.getInt("requestID")

                            if(requestID != tempRequestID)
                            {
                                data.add(
                                    Data(
                                        item.getInt("requestID"),
                                        dateStart,
                                        item.getString("serviceName"),
                                        item.getInt("statusID"),
                                        item.getString("comment")

                                    )
                                )
                                tempRequestID = requestID
                            }



                            // Log.e("tag", item.getString("orderid"))
                        }
                        recyclerView!!.adapter = DataAdapter(data)
                    } else {
                        //                        Log.e("tag", "x6")
                        Toast.makeText(context, "ไม่สามารถแสดงข้อมูลได้", Toast.LENGTH_LONG).show()
                    }
                } catch (e: JSONException) { e.printStackTrace() }
            } else { response.code }
        } catch (e: IOException) { e.printStackTrace() }
    }


    internal class Data(
        var requestID : Int, var dateStart: String,
        var serviceName: String, var statusID: Int,var comment: String
    )
    internal inner class DataAdapter(private val list: List<Data>) :
        RecyclerView.Adapter<DataAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view: View = LayoutInflater.from(parent.context).inflate(
                R.layout.item_orderhistory,
                parent, false
            )
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val formatter: NumberFormat = DecimalFormat("#,###")
            val status = arrayOf(
                "กำลังจอง", "รอตรวจสอบ", "ชำระเงินแล้ว")
            val data = list[position]
            holder.data = data
            holder.txtrequestID.text = data.requestID.toString()
            holder.txtDate.text = data.dateStart
            holder.txtservice.text = data.serviceName

            if (data.statusID == 1 && data.comment != "null") {
                holder.txtStatus.text = data.comment
            } else {
                holder.txtStatus.text = status[data.statusID]
            }


            if(data.statusID == 1){
                holder.txtStatus.setTextColor(Color.parseColor("#FF0000"))
            }else{
                holder.txtStatus.setTextColor(Color.parseColor("#000000"))
            }

            holder.linearLayout.setOnClickListener {
                /*
                Toast.makeText(context, "คุณเลือกรหัสการสั่งซื้อ " + holder.txtrequestID.text,
                    Toast.LENGTH_LONG).show()
                */

//                if(data.statusID == 1){
//                    //Go to orderActivity
//                    val intent = Intent(context, PaymentActivity::class.java)
//                    intent.putExtra("requestID", data.requestID)
////                                    intent.putExtra("price", data.totalPrice)
//                    startActivity(intent)
//                }else{
                    //Go to orderActivity
                    val intent = Intent(context, ListActivity::class.java)
                    intent.putExtra("requestID", data.requestID)
                    startActivity(intent)
//                }

            }

            if(position % 2 == 0)
            {
                holder.linearLayout.setBackgroundResource(R.color.purple_200);
            }
            else
            {
                holder.linearLayout.setBackgroundResource(R.color.white);
            }
        }

        override fun getItemCount(): Int {
            return list.size
        }

        internal inner class ViewHolder(itemView: View) :
            RecyclerView.ViewHolder(itemView) {
            var data: Data? = null
            var txtrequestID: TextView = itemView.findViewById(R.id.txtrequest)
            var txtDate: TextView = itemView.findViewById(R.id.txtdateStart)
            var txtservice: TextView = itemView.findViewById(R.id.txtservice)
            var txtStatus: TextView = itemView.findViewById(R.id.txtstatus)
            var linearLayout: LinearLayout  = itemView.findViewById(R.id.linearLayout)

        }

    }
}