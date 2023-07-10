package th.ac.rmutto.myapplicationmaid

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.IOException

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Hide the status bar.
        supportActionBar?.hide()

        //For an synchronous task
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        var editTextUsername: EditText = findViewById(R.id.editTextUsername)
        var editTextPassword: EditText = findViewById(R.id.editTextPassword)
        var buttonLogin: Button = findViewById(R.id.buttonLogin)
//        var buttonRegister: Button = findViewById(R.id.buttonRegister)
//
//        buttonRegister.setOnClickListener {
//            val intent = Intent(this, RegisterActivity::class.java)
//            startActivity(intent)
//            finish()
//        }


        buttonLogin.setOnClickListener {
            Log.d("tag", "x1")
            if(editTextUsername.text.toString() == ""){
                editTextUsername.error = "กรุณาระบุชื่อผู้ใช้"
                return@setOnClickListener
            }

            if(editTextPassword.text.toString() == ""){
                editTextPassword.error = "กรุณาระบุรหัสผ่าน"
                return@setOnClickListener
            }

            //redirect to main page
//            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
//            finish()

            val url = getString(R.string.root_url) + getString(R.string.loginprovider_url)
            Log.d("tag",url)

            val okHttpClient = OkHttpClient()
            val formBody: RequestBody = FormBody.Builder()
                .add("userName",editTextUsername.text.toString())
                .add("password",editTextPassword.text.toString())
                .build()
            val request: Request = Request.Builder()
                .url(url)
                .post(formBody)
                .build()
            try{
                Log.d("tag", "x2")
                val response = okHttpClient.newCall(request).execute()
                if(response.isSuccessful){
                    Log.d("tag", "x3")
                    val obj = JSONObject(response.body!!.string())
                    val message = obj["message"].toString()
                    val status = obj["status"].toString()

                    if (status == "true") {
                        Log.d("tag", "x4")
                        val userid = obj["providerID"].toString()
                        val username = obj["userName"].toString()

                        //Create shared preference to store user data
                        val sharedPrefer: SharedPreferences =
                            getSharedPreferences("appPrefer", Context.MODE_PRIVATE)
                        val editor: SharedPreferences.Editor = sharedPrefer.edit()

                        editor.putString("custIDPref", userid)
                        editor.putString("usernamePref", username)
                        editor.commit()

                        //redirect to main page
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Log.d("tag", "x5")
                        editTextUsername.error = ""
                        return@setOnClickListener

                    }

                }else{
                    Log.d("tag", "x6")
                    response.code
                    Toast.makeText(applicationContext, "ไม่สามารถเชื่อมต่อกับเซิร์ฟเวอร์ได้", Toast.LENGTH_LONG)
                }
            }catch (e: IOException){
                Log.d("tag", "x7")
                e.printStackTrace()
            }
        }//buttonLogin
    }
}

