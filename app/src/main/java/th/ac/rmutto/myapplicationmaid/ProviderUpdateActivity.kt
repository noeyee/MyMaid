package th.ac.rmutto.myapplicationmaid

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import com.github.drjacky.imagepicker.ImagePicker
import com.github.drjacky.imagepicker.constant.ImageProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.squareup.picasso.Picasso
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.util.ArrayList


class ProviderUpdateActivity : AppCompatActivity() {

    var editTextPassword: EditText? = null
    var editTextfirstName: EditText? = null
    var editTextlastName: EditText? = null
    var editTextaddress: EditText? = null
    var editTextmobilePhone: EditText? = null
    var editTextEmail: EditText? = null
    var editUserName: EditText? = null
    var spinner1: Spinner? = null
    var spinner2: Spinner? = null
    var spinner3: Spinner? = null

    var imgViewFile: ImageView? = null
    var  buttonUpdate: Button? = null

    private var province = ArrayList<Province>()
    private var district = ArrayList<District>()
    private var subdistrict = ArrayList<Subdistrict>()
    var provinceID = ""
    var districtID = ""
    var subdistrictID = ""
    var isProvinceLoad = true
    var isDistrictLoad = true


    var file: File? = null
    var floatingActionButton: FloatingActionButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_provider_update)

        //For an synchronous task
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)


        val sharedPrefer = this.getSharedPreferences(
            "appPrefer", Context.MODE_PRIVATE
        )
        var custID = sharedPrefer?.getString("custIDPref", null)
//        var username = sharedPrefer?.getString("usernamePref", null)


        editTextfirstName = findViewById(R.id.editTextfirstName)
        editTextlastName = findViewById(R.id.editTextlastName)
        editTextaddress = findViewById(R.id.editTextaddress)
        editTextmobilePhone = findViewById(R.id.editTextmobilePhone)
        editTextEmail = findViewById(R.id.editTextEmail)
        editUserName = findViewById(R.id.editUserName)
        editTextPassword = findViewById(R.id.editTextPassword)
        spinner1 = findViewById(R.id.spinner1)
        spinner2 = findViewById(R.id.spinner2)
        spinner3 = findViewById(R.id.spinner3)
        imgViewFile = findViewById(R.id.imgViewFile)

        var buttonUpdate: Button = findViewById(R.id.buttonUpdate)

        viewUser(custID!!)


        province.add(Province("0", "เลือกจังหวัด"))
        district.add(District("0", "เลือกอำเภอ"))
        subdistrict.add(Subdistrict("0", "เลือกตำบล"))

        listProvince()
        listDistrict(provinceID)
        listSubdistrict(districtID)


        //upload or pick a picture
        //upload or pick a picture
        val launcher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    val uri = it.data?.data!!
                    val path = RealPathUtil.getRealPath(applicationContext, uri)
                    file = File(path.toString())
                    imgViewFile?.setImageURI(uri)
                }
            }

        imgViewFile?.setOnClickListener {
            Log.d("tag","click1")
            ImagePicker.Companion.with(this)
                .crop()
                .cropOval()
                .maxResultSize(480, 480)
                .provider(ImageProvider.BOTH) //Or bothCameraGallery()
                .createIntentFromDialog { launcher.launch(it) }
            Log.d("tag","click2")
        }

        //Province
        val adapterProvince = ArrayAdapter(
            this,android.R.layout.simple_spinner_item, province)
        spinner1?.adapter = adapterProvince
        spinner1?.setSelection(province.indexOfFirst{ it.provinceID == provinceID })
        spinner1?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                val province = spinner1!!.selectedItem as Province
                provinceID = province.provinceID

                if(isProvinceLoad) {
                    isProvinceLoad = false
                }else{
                    district.clear()
                    district.add(District("0", "เลือกอำเภอ"))
                    listDistrict(province.provinceID)
                    spinner2?.setSelection(district.indexOfFirst { it.districtID == "0" })

                    subdistrict.clear()
                    subdistrict.add(Subdistrict("0", "เลือกตำบล"))
                    spinner3?.setSelection(subdistrict.indexOfFirst { it.subdistrictID == "0" })

                }

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }


        //District
        val adapterDistrict = ArrayAdapter(
            this,android.R.layout.simple_spinner_item, district)
        spinner2!!.adapter = adapterDistrict
        spinner2?.setSelection(district.indexOfFirst{ it.districtID == districtID })
        spinner2!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                val district = spinner2!!.selectedItem as District
                districtID = district.districtID

                if(isDistrictLoad) {
                    isDistrictLoad = false
                }else {
                    subdistrict.clear()
                    subdistrict.add(Subdistrict("0", "เลือกตำบล"))
                    listSubdistrict(districtID)
                    spinner3?.setSelection(subdistrict.indexOfFirst { it.subdistrictID == "0" })
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        //Subdistrict
        val adapterSubdistrict = ArrayAdapter(
            this,android.R.layout.simple_spinner_item, subdistrict)
        spinner3!!.adapter = adapterSubdistrict
        spinner3?.setSelection(subdistrict.indexOfFirst{ it.subdistrictID == subdistrictID })
        spinner3!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                val subdistrict = spinner3!!.selectedItem as Subdistrict
                subdistrictID = subdistrict.subdistrictID
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }



        buttonUpdate.setOnClickListener {
            val url = getString(R.string.root_url) + getString(R.string.updateprovider_url)
            Log.d("tag",url)

            val okHttpClient = OkHttpClient()
            var formBody: RequestBody? = null

            if(file != null){
                formBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("firstName",editTextfirstName?.text.toString())
                    .addFormDataPart("lastName",editTextlastName?.text.toString())
                    .addFormDataPart("address",editTextaddress?.text.toString())
                    .addFormDataPart("mobilePhone",editTextmobilePhone?.text.toString())
                    .addFormDataPart("email",editTextEmail?.text.toString())
                    .addFormDataPart("userName",editUserName?.text.toString())
                    .addFormDataPart("subdistrictID", subdistrictID)
                    .addFormDataPart("password", editTextPassword?.text.toString())
                    .addFormDataPart("imagefile", file?.name,
                        RequestBody.create("application/octet-stream".toMediaTypeOrNull(), file!!))
                    .addFormDataPart("providerID",custID!!)
                    .build()
            }else{
                formBody = FormBody.Builder()
                    .add("firstName",editTextfirstName?.text.toString())
                    .add("lastName",editTextlastName?.text.toString())
                    .add("address",editTextaddress?.text.toString())
                    .add("mobilePhone",editTextmobilePhone?.text.toString())
                    .add("email",editTextEmail?.text.toString())
                    .add("userName",editUserName?.text.toString())
                    .add("password", editTextPassword?.text.toString())
                    .add("subdistrictID", subdistrictID)
                    .add("providerID",custID!!)
                    .build()
            }

            val request: Request = Request.Builder()
                .url(url)
                .post(formBody!!)
                .build()
            try{
                val response = okHttpClient.newCall(request).execute()
                if(response.isSuccessful){
                    val obj = JSONObject(response.body!!.string())
                    val message = obj["message"].toString()
                    val status = obj["status"].toString()

                    if (status == "true") {

                        //redirect to main page
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        editUserName?.error = ""
                        return@setOnClickListener

                    }

                }else{
                    response.code
                    Toast.makeText(applicationContext, "ไม่สามารถเชื่อมต่อกับเซิร์ฟเวอร์ได้", Toast.LENGTH_LONG)
                }
            }catch (e: IOException){
                e.printStackTrace()
            }
        }//buttonLogin


    }

    fun viewUser(custID: String) //แสดงรายละเอียด
    {
        Log.d("tag", "x1")
        var url: String = getString(R.string.root_url) + getString(R.string.profileProvider_url) + custID
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
                        var imagefile = data.getString("imagefile")
                        var userName = data.getString("userName")
                        var firstName = data.getString("firstName")
                        var lastName = data.getString("lastName")
                        var password= data.getString("password")
                        var address= data.getString("address")
                        var email = data.getString("email")
                        var mobilePhone = data.getString("mobilePhone")

                        provinceID = data.getString("provinceID")
                        districtID = data.getString("districtID")
                        subdistrictID = data.getString("subdistrictID")


                        if (!imagefile.equals("null") && !imagefile.equals("")){//รูปมาโชว
                            val image_url = getString(R.string.root_url) +
                                    getString(R.string.customer_image_url) + imagefile
                            Picasso.get().load(image_url).into(imgViewFile)

                            Log.d("tag", image_url)
                        }
//

                        if(firstName.equals("null"))firstName = "-"
                        editTextfirstName?.setText (firstName)

                        if(lastName.equals("null"))lastName = "-"
                        editTextlastName?.setText (lastName)

                        if(address.equals("null"))address = "-"
                        editTextaddress?.setText (address)

                        if(mobilePhone.equals("null"))mobilePhone = "-"
                        editTextmobilePhone?.setText (mobilePhone)

                        if(email.equals("null"))email = "-"
                        editTextEmail?.setText (email)

                        if(userName.equals("null"))userName = "-"
                        editUserName?.setText (userName)

                        if(password.equals("null"))password = "-"
                        editTextPassword?.setText (password)

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


    private fun listProvince() {
        //province.add(Province("0", "เลือกจังหวัด"))
        val urlProvince: String = getString(R.string.root_url) + getString(R.string.provinceProvider_url)
        val okHttpClient = OkHttpClient()
        val request: Request = Request.Builder().url(urlProvince).get().build()
        try {
            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                try {
                    val res = JSONArray(response.body!!.string())
                    if (res.length() > 0) {
                        for (i in 0 until res.length()) {
                            val item: JSONObject = res.getJSONObject(i)
                            province.add(
                                Province(
                                    item.getString("provinceID"),
                                    item.getString("provinceName")
                                )
                            )
                        }
                    }
                } catch (e: JSONException) { e.printStackTrace() }
            } else { response.code }
        } catch (e: IOException) { e.printStackTrace() }
    }

    private fun listDistrict(provinceID: String) {

        //district.add(District("0", "เลือกอำเภอ"))
        val urlDistrict: String = getString(R.string.root_url) +
                getString(R.string.districtProvider_url) + provinceID

        //val urlDistrict: String = getString(R.string.root_url) + getString(R.string.district_url)
        Log.d("log", urlDistrict)
        val okHttpClient = OkHttpClient()
        Log.d("log", "x1")
        val request: Request = Request.Builder().url(urlDistrict).get().build()
        //Log.d("log", "x2")
        try {
            Log.d("log", "x3")
            val response = okHttpClient.newCall(request).execute()
            Log.d("log", "x4")
            if (response.isSuccessful) {
                Log.d("log", "x5")
                try {
                    Log.d("log", "x6")
                    //Log.d("log", response.body!!.string())
                    val res = JSONArray(response.body!!.string())
                    Log.d("log", res.length().toString())
                    if (res.length() > 0) {
                        Log.d("log", "x7")
                        for (i in 0 until res.length()) {
                            //Log.d("log", i.toString())
                            val item: JSONObject = res.getJSONObject(i)
                            district.add(
                                District(
                                    item.getString("districtID"),
                                    item.getString("districtName")
                                )
                            )
                        }
                    }
                } catch (e: JSONException) { e.printStackTrace() }
            } else { response.code }
        } catch (e: IOException) { e.printStackTrace() }
    }

    private fun listSubdistrict(districtID: String) {

        val urlSubdistrict: String = getString(R.string.root_url) +
                getString(R.string.subdistrictProvider_url) + districtID
        Log.d("log", urlSubdistrict)
        val okHttpClient = OkHttpClient()
        val request: Request = Request.Builder().url(urlSubdistrict).get().build()
        try {
            Log.d("log","x1")
            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                Log.d("log","x2")
                try {
                    Log.d("log","x1")
                    //Log.d("log", response.body!!.string())
                    val res = JSONArray(response.body!!.string())
                    if (res.length() > 0) {
                        for (i in 0 until res.length()) {
                            //Log.d("log", i.toString())
                            val item: JSONObject = res.getJSONObject(i)
                            subdistrict.add(
                                Subdistrict(
                                    item.getString("subdistrictID"),
                                    item.getString("subdistrictName")
                                )
                            )
                        }
                    }
                } catch (e: JSONException) { e.printStackTrace() }
            } else { response.code }
        } catch (e: IOException) { e.printStackTrace() }
    }


    class Province(var provinceID: String, var provinceName: String)
    {
        override fun toString(): String {
            return provinceName
        }

    }

    class District(var districtID: String, var districtName: String)
    {
        override fun toString(): String {
            return districtName
        }

    }

    internal class Subdistrict(var subdistrictID: String, var subdistrictName: String)
    {
        override fun toString(): String {
            return subdistrictName
        }

    }

}