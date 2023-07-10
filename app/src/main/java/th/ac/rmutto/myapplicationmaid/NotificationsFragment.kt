package th.ac.rmutto.myapplicationmaid


import android.app.Activity
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
import androidx.activity.result.contract.ActivityResultContracts
import com.github.drjacky.imagepicker.ImagePicker
import com.github.drjacky.imagepicker.constant.ImageProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.squareup.picasso.Picasso
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException


class NotificationsFragment : Fragment() {

    var imgViewFile: ImageView? = null
    var txtFirstName: TextView? = null
    var txtLastName: TextView? = null
    var txtaddress: TextView? = null
    var txtmobilePhone: TextView? = null
    var txtEmail: TextView? = null
    var txtUsername: TextView? = null

    var txtprovince: TextView? = null
    var txtdistrict: TextView? = null
    var txtsubdistrict: TextView? = null

    var file: File? = null



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_notifications, container, false)

        //For an synchronous task
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)


        val sharedPrefer = requireContext().getSharedPreferences(
            "appPrefer", Context.MODE_PRIVATE)
        var custID = sharedPrefer?.getString("custIDPref", null)
        //var username = sharedPrefer?.getString("usernamePref", null)

        imgViewFile = root.findViewById(R.id.imgViewFile)
        txtFirstName = root.findViewById(R.id.editTextfirstName)
        txtLastName = root.findViewById(R.id.editTextlastName)
        txtaddress = root.findViewById(R.id.editTextaddress)
        txtmobilePhone = root.findViewById(R.id.editTextmobilePhone)
        txtEmail = root.findViewById(R.id.editTextEmail)
        txtUsername = root.findViewById(R.id.editUserName)

        txtprovince = root.findViewById(R.id.txtprovince)
        txtdistrict = root.findViewById(R.id.txtdistrict)
        txtsubdistrict = root.findViewById(R.id.txtsubdistrict)



        val buttonUpdate: Button = root.findViewById(R.id.buttonUpdate)


//        buttonUpdate.setOnClickListener {
//
//            val fragmentTransaction = requireActivity().
//            supportFragmentManager.beginTransaction()
//            fragmentTransaction.addToBackStack(null)
//            fragmentTransaction.replace(R.id.nav_host_fragment_activity_main, CustomerUpdateActivity())
//            fragmentTransaction.commit()
//        }


        buttonUpdate.setOnClickListener {
            //return to login page
            val intent = Intent(context, ProviderUpdateActivity::class.java)
            startActivity(intent)
        }

        viewUser(custID!!)

        return root
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
                        var firstName = data.getString("firstName")
                        var lastName = data.getString("lastName")
                        var address = data.getString("address")
                        var mobilePhone = data.getString("mobilePhone")
                        var email = data.getString("email")
                        var userName = data.getString("userName")

                        var province = data.getString("provinceName")
                        var district = data.getString("districtName")
                        var subdistrict = data.getString("subdistrictName")


                        if (!imagefile.equals("null") && !imagefile.equals("")){//รูปมาโชว
                            val image_url = getString(R.string.root_url) +
                                    getString(R.string.customer_image_url) + imagefile
                            Picasso.get().load(image_url).into(imgViewFile)

                            Log.d("tag", image_url)
                        }



//                        upload or pick a picture
//                        val launcher =
//                            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
//                                if (it.resultCode == Activity.RESULT_OK) {
//                                    val uri = it.data?.data!!
//                                    val path = RealPathUtil.getRealPath(context, uri)
//                                    file = File(path.toString())
//                                    imgViewFile?.setImageURI(uri)
//                                }
//                            }
//
//                        floatingActionButton?.setOnClickListener {
//                            ImagePicker.Companion.with(this)
//                                .crop()
//                                .cropOval()
//                                .maxResultSize(480, 480)
//                                .provider(ImageProvider.BOTH) //Or bothCameraGallery()
//                                .createIntentFromDialog { launcher.launch(it) }
//                        }

                        if(firstName.equals("null"))firstName = "-"
                        txtFirstName?.text = firstName

                        if(lastName.equals("null"))lastName = "-"
                        txtLastName?.text = lastName

                        if(address.equals("null"))address = "-"
                        txtaddress?.text = address

                        if(mobilePhone.equals("null"))mobilePhone = "-"
                        txtmobilePhone?.text = mobilePhone

                        if(email.equals("null"))email = "-"
                        txtEmail?.text = email

                        if(userName.equals("null"))userName = "-"
                        txtUsername?.text = userName


                        if(province.equals("null"))province = "-"
                        txtprovince?.text = province

                        if(district.equals("null"))district = "-"
                        txtdistrict?.text = district

                        if(subdistrict.equals("null"))subdistrict = "-"
                        txtsubdistrict?.text = subdistrict

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
}