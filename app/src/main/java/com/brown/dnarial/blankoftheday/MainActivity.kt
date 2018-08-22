package com.brown.dnarial.blankoftheday


import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Build
import android.support.annotation.NonNull
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task


import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult



class MainActivity : AppCompatActivity() {

    private val mAuth: FirebaseAuth? = FirebaseAuth.getInstance()




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        val changingWord = findViewById<TextView>(R.id.changingWordTextView)
        var countDownTimer: CountDownTimer
        val titleShift = mutableListOf<String>("Words", "Verse", "Haiku", "Dream", "World", "Smile", "Story", "Glory", "Birth", "Plant", "Truth", "Voice", "Value", "Sound", "Human")
        val indexArray = intArrayOf(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14)
        val timeArray = longArrayOf(2000,4000,6000,8000,10000,12000,14000,16000,18000,20000,22000,24000,26000,28000,30000)


        // I want to run this code continuously in an infinite loop without slowing the UI
        fun WordChanger(num: Int, time: Long) {

            countDownTimer = object : CountDownTimer(time, 1000) { // starts at 3 seconds

                override fun onTick(secondsUntilDone: Long) {

                }

                override fun onFinish() {

                    //Log.i("We're done!", "No more countdown")
                    changingWord.text = titleShift[num]
                    changingWord.textSize = 34f
                }
            }.start()
        }

        //  To do this first create a handler
        val handler = Handler()

        //We don't want the loop running on the main thread so we run it on another
        runOnUiThread(Runnable { //allows this loop to run on a separate thread and hopefully improve metrics long term
            kotlin.run {

                // Define the code block to be executed by creating an object which will do the execution
                val runnable = object : Runnable {
                    override fun run() {
                        // Insert custom code here
                        for (i in indexArray) {

                            WordChanger(i, timeArray[i])
                        }
                        // Repeat every 30 seconds
                        handler.postDelayed(this, 30000)
                    }
                }

                // Start the Runnable immediately
                handler.post(runnable)
            }
        })



        // add fun pop ups to encourage users while getting haikus and quotes page up and running.
        // start the poem activity when the poem button is pressed
        val poemPage = findViewById<Button>(R.id.poemsButton)
        poemPage.setOnClickListener {

            //val inflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            //val pw = PopupWindow(inflater.inflate(R.layout.login_screen, null, false), 1000, 2000, true)
            //pw.showAtLocation(mainLayout, Gravity.CENTER, 0, 0)

            val intent = Intent(this,PoemActivity::class.java)
            //this.finish()  users didnt like this effect
            startActivity(intent)
        }

        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        

        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // This section is for creating an account and logging in/////////

        val emailText = findViewById<EditText>(R.id.emailEditText)
        val passwordText = findViewById<EditText>(R.id.passwordEditText)
        val createAccountButton = findViewById<Button>(R.id.createAccountButton)
        val logButton = findViewById<Button>(R.id.loginButton)
        val poemButton = findViewById<Button>(R.id.poemsButton)
        emailText.textSize = 20f
        passwordText.textSize = 20f



        // This will check to make sure the email and password fields are not blank
        fun validateForm():Boolean {

            var valid = true
            val email = emailText.text.toString()
            if (email.isEmpty()) {

                emailText.error = "Required"
                valid = false
            } else {

                emailText.error = null
            }

            val password = passwordText.text.toString()
            if (password.isEmpty()) {
                passwordText.error = "Required."
                valid = false;
            } else {
                passwordText.error = null
            }
            return valid
        }

        // This will display the login screen based on whether a user is logged in or not
        fun updateUI(user: FirebaseUser?) {

            if (user != null) {
                emailText.visibility = View.INVISIBLE
                passwordText.visibility = View.INVISIBLE
                createAccountButton.visibility = View.INVISIBLE
                logButton.visibility = View.INVISIBLE
                //quoteButton.visibility = View.VISIBLE
                poemButton.visibility = View.VISIBLE
                //haikuButton.visibility = View.VISIBLE
                //haikuText.visibility = View.VISIBLE
                //quoteText.visibility = View.VISIBLE
            } else {
                emailText.visibility = View.VISIBLE
                passwordText.visibility = View.VISIBLE
                createAccountButton.visibility = View.VISIBLE
                logButton.visibility = View.VISIBLE
                //quoteButton.visibility = View.INVISIBLE
                poemButton.visibility = View.INVISIBLE
                //haikuButton.visibility = View.INVISIBLE
                //haikuText.visibility = View.INVISIBLE
                //quoteText.visibility = View.INVISIBLE

            }
        }

        // This will allow a user to create an account and let them know why if it does not work
        // This will also be used to call the createNotification function
        fun createAccount(email:String, password:String) {
            Log.d("TAG", "createAccount:" + email);

            if (!validateForm()) {

            }else {

                mAuth?.createUserWithEmailAndPassword(email, password)?.addOnSuccessListener {
                            Toast.makeText(this,
                                    "You have successfully created an account." ,
                                    Toast.LENGTH_SHORT).show()
                        }?.addOnFailureListener {

                    val email = emailText.text.toString()
                    val password = passwordText.text.toString()
                    if (!email.contains("@")){
                        emailText.error = "Valid Email Required."
                    }else if(password.length < 5){
                        passwordText.error = "Password must be at least 5 units long"
                    }else {
                        Toast.makeText(this,
                                "This email has already been used. Please try a  new one or reset password.",
                                Toast.LENGTH_LONG).show()
                    }


                }
                // [END create_user_with_email]
            }

        }

        

        // this will allow a user to sign in with a created account
        fun signIn(email:String, password:String) {
            Log.d("TAG", "signIn:" + email);
            if (!validateForm()) {

            }else {

                mAuth?.signInWithEmailAndPassword(email, password)?.addOnSuccessListener {
                    Toast.makeText(this,
                            "You have successfully logged in.",
                            Toast.LENGTH_SHORT).show()
                    val user: FirebaseUser? = mAuth.currentUser
                    updateUI(user)
                }?.addOnFailureListener {
                    Toast.makeText(this,
                            "Your email or password may be incorrect.",
                            Toast.LENGTH_SHORT).show()
                }

            }
        }


        // [START on_start_check_user]
        fun onStart() {
            super.onStart()
            // Check if user is signed in (non-null) and update UI accordingly.
            val currentUser = mAuth?.currentUser
            updateUI(currentUser)
        }
        onStart()
        // [END on_start_check_user]



        createAccountButton.setOnClickListener {

            val email = emailText.text.toString()
            val password = passwordText.text.toString()
            createAccount(email, password)
        }

        logButton.setOnClickListener {
            val email = emailText.text.toString()
            val password = passwordText.text.toString()
            //createNotificationChannel()
            println(NotificationChannel.CONTENTS_FILE_DESCRIPTOR)

            signIn(email,password)
        }



    }

}



