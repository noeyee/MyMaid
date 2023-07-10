package th.ac.rmutto.myapplicationmaid

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import th.ac.rmutto.myapplicationmaid.R
import java.io.IOException
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONArray



class HomeFragment : Fragment() {
    var txtservice: TextView? = null
    var textdate: TextView? = null
    var texttime: TextView? = null
    var texthour: TextView? = null
    var textpay: TextView? = null
    var btok: Button? = null
    var orderid: TextView? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //eturn inflater.inflate(R.layout.fragment_reserve, container, false)
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        //For an synchronous task
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val sharedPrefer = requireContext().getSharedPreferences(
            "appPrefer", Context.MODE_PRIVATE)
        var custID = sharedPrefer?.getString("custIDPref", null)
        //var username = sharedPrefer?.getString("usernamePref", null)

        btok = root.findViewById(R.id.btok)

        btok?.setOnClickListener {
            val url = getString(R.string.root_url) + getString(R.string.requestupdateProvider_url)
            Log.d("tag", url)
            val okHttpClient = OkHttpClient()
            val formBody = FormBody.Builder()
//            Log.d("tag", "s1")
                .add("providerID", custID!!)
                .add("requestID", orderid?.text.toString())
                .build()
            Log.d("tag", "2")
            val request: Request = Request.Builder()
                .url(url)
                .post(formBody)
                .build()
            Log.d("tag", "3")
            try {
                val response = okHttpClient.newCall(request).execute()
                Log.d("tag", "4")
                if (response.isSuccessful) {
                    val obj = JSONObject(response.body!!.string())
                    val message = obj["message"].toString()
                    val status = obj["status"].toString()

                    if (status == "true") {
                        val newFragment = HomeFragment()
                    val transaction = requireActivity().supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.mainhome, newFragment)
                    transaction.addToBackStack(null) // Optional: Add the transaction to the back stack
                    transaction.commit()
                        btok?.visibility = View.GONE

                    }


                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


        txtservice = root.findViewById(R.id.edittextservice)
        textdate = root.findViewById(R.id.edittextdate)
        texttime = root.findViewById(R.id.edittexttime)
        texthour = root.findViewById(R.id.edittexthour)
        textpay = root.findViewById(R.id.edittextpay)
        orderid = root.findViewById(R.id.textView10)

        viewUser(custID!!)
        return root
    }


    fun viewUser(custID: String) //แสดงรายละเอียด
    {
        Log.d("tag", "x1")
        var url: String = getString(R.string.root_url) + getString(R.string.requestprovider_url) + custID
        Log.d("tag", url)
        val okHttpClient = OkHttpClient()
        val request: Request = Request.Builder()
            .url(url)
            .get()
            .build()
        try {
            Log.d("tag", "a2")
            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                Log.d("tag", "aa2")
                try {
                    Log.d("tag", "x2")
                    val data = JSONObject(response.body!!.string())
                    if (data.length() > 0) {
                        Log.d("tag", "x3")
                        var serviceName = data.getString("serviceName")
                        var dateStart = data.getString("dateStart")
                        var timeStart = data.getString("timeStart")
                        var hour = data.getString("hour")
                        var price = data.getString("price")
                        var orderid1 = data.getString("requestID")


                        if(serviceName.equals("null"))serviceName = "-"
                        txtservice?.text = serviceName

                        if(dateStart.equals("null"))dateStart = "-"
                        textdate?.text = dateStart

                        if(timeStart.equals("null"))timeStart = "-"
                        texttime?.text = timeStart

                        if(hour.equals("null"))hour = "-"
                        texthour?.text = hour

                        if(price.equals("null"))price = "-"
                        textpay?.text = price

                        if(orderid1.equals("null"))orderid1 = "-"
                        orderid?.text = orderid1

                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            } else {
                response.code
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

//


//    fun clearData(custID: String) {
//        // ลบข้อมูลในฟิลด์แต่ละช่อง
//        edittextservice.text.clear()
//        txtservice.text = ""
//
//        edittextdate.text.clear()
//        textdate.text = ""
//
//        edittexttime.text.clear()
//        texttime.text = ""
//
//        edittexthour.text.clear()
//        texthour.text = ""
//
//        edittextpay.text.clear()
//        textpay.text = ""
//    }
}
