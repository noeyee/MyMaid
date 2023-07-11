package th.ac.rmutto.myapplicationmaid

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class ListActivity : AppCompatActivity() {
    var txtservice: TextView? = null
    var textdate: TextView? = null
    var texttime: TextView? = null
    var texthour: TextView? = null
    var textpay: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        val sharedPrefer = this.getSharedPreferences(
            "appPrefer", Context.MODE_PRIVATE
        )
        var custID = sharedPrefer?.getString("custIDPref", null)
//        var username = sharedPrefer?.getString("usernamePref", null)


        txtservice = findViewById(R.id.edittextservice)
        textdate = findViewById(R.id.edittextdate)
        texttime = findViewById(R.id.edittexttime)
        texthour = findViewById(R.id.edittexthour)
        textpay = findViewById(R.id.edittextpay)
        viewUser(custID!!)

    }




    fun viewUser(custID: String) //แสดงรายละเอียด
    {
        Log.d("tag", "x1")
        var url: String = getString(R.string.root_url) + getString(R.string.requestcurrent_url) + custID
        Log.d("tag", url)
        val okHttpClient = OkHttpClient()
        val request: Request = Request.Builder()
            .url(url)
            .get()
            .build()
        try {
            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
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