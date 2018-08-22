package com.brown.dnarial.blankoftheday


import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_poem.*
import java.text.SimpleDateFormat
import java.util.*


class PoemActivity : AppCompatActivity() {

    private val mDatabase: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var uploadCount = 0
    private val email = mAuth.currentUser!!.email.toString().replace(".","")
    private var currentTime = Calendar.getInstance()




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_poem)


        // this function will be used to unloaded content to the database
        fun addPoemToDatabase() {
            val poemA = PoemSelection()
            val poemB = PoemSelection()
            val poemC = PoemSelection()
            val poemD = PoemSelection()

            poemA.poem = "Can’st thou conjure a vanished morn of spring,\n" +
                    "     Or bid the ashes of the sunset glow\n" +
                    "Again to redness? Are we strong to wring\n" +
                    "     From trodden grapes the juice drunk long ago?\n" +
                    "Can leafy longings stir in Autumn’s blood,\n" +
                    "     Or can I wear a pearl dissolved in wine,\n" +
                    "Or go a-Maying in a winter wood,\n" +
                    "     Or paint with youth thy wasted cheek, or mine?\n" +
                    "What bloom, then, shall abide, since ours hath sped?\n" +
                    "     Thou art more lost to me than they who dwell\n" +
                    "In Egypt’s sepulchres, long ages fled;\n" +
                    "     And would I touch—Ah me! I might as well\n" +
                    "Covet the gold of Helen’s vanished head,\n" +
                    "     Or kiss back Cleopatra from the dead!"
            poemA.author = "\n" + "Willa Cather, 1873 - 1947\n"
            poemA.title = "Aftermath"

            poemB.poem = "Led by a star, a golden star,\n" +
                    "The youngest star, an olden star,\n" +
                    "Here the kings and the shepherds are,\n" +
                    "Akneeling on the ground.\n" +
                    "What did they come to the inn to see?\n" +
                    "God in the Highest, and this is He,\n" +
                    "A baby asleep on His mother’s knee\n" +
                    "And with her kisses crowned.\n" +
                    "\n" +
                    "Now is the earth a dreary place,\n" +
                    "A troubled place, a weary place.\n" +
                    "Peace has hidden her lovely face\n" +
                    "And turned in tears away.\n" +
                    "Yet the sun, through the war-cloud, sees\n" +
                    "Babies asleep on their mother’s knees.\n" +
                    "While there are love and home—and these—\n" +
                    "There shall be Christmas Day."
            poemB.author = "\n" + "Joyce Kilmer, 1886 - 1918\n"
            poemB.title = "Wartime Christmas"

            poemC.poem = "Come, white angels, to baby and me;\n" +
                    "     Touch his blue eyes with the image of sleep,\n" +
                    "     In his surprise he will cease to weep;\n" +
                    "Hush, child, the angels are coming to thee!\n" +
                    "\n" +
                    "Come, white doves, to baby and me;\n" +
                    "     Softly whirr in the silent air,\n" +
                    "     Flutter about his golden hair:\n" +
                    "Hark, child, the doves are cooing to thee!\n" +
                    "\n" +
                    "Come, white lilies, to baby and me;\n" +
                    "     Drowsily nod before his eyes,\n" +
                    "     So full of wonder, so round and wise:\n" +
                    "Hist, child, the lily-bells tinkle for thee!\n" +
                    "\n" +
                    "Come, white moon, to baby and me;\n" +
                    "     Gently glide o’er the ocean of sleep,\n" +
                    "     Silver the waves of its shadowy deep:\n" +
                    "Sleep, child, and the whitest of dreams to thee."
            poemC.author = "Elizabeth Drew Stoddard"
            poemC.title = "A Baby Song"

            poemD.poem = "Much have I spoken of the faded leaf;\t\n" +
                    "    Long have I listened to the wailing wind,\t\n" +
                    "And watched it ploughing through the heavy clouds,\t\n" +
                    "    For autumn charms my melancholy mind.\t\n" +
                    " \n" +
                    "When autumn comes, the poets sing a dirge:\n" +
                    "    The year must perish; all the flowers are dead;\t\n" +
                    "The sheaves are gathered; and the mottled quail\t\n" +
                    "    Runs in the stubble, but the lark has fled!\t\n" +
                    " \n" +
                    "Still, autumn ushers in the Christmas cheer,\t\n" +
                    "    The holly-berries and the ivy-tree:\n" +
                    "They weave a chaplet for the Old Year’s bier,\t\n" +
                    "    These waiting mourners do not sing for me!\t\n" +
                    " \n" +
                    "I find sweet peace in depths of autumn woods,\t\n" +
                    "    Where grow the ragged ferns and roughened moss;\t\n" +
                    "The naked, silent trees have taught me this,—\n" +
                    "    The loss of beauty is not always loss!"
            poemD.author = "Elizabeth Drew Stoddard"
            poemD.title = "November"

            var poemListIn = listOf(poemA, poemB, poemC, poemD)


            mDatabase.child("Poems").setValue(poemListIn)
        }

        //addPoemToDatabase()

        // this creates an empty poem list and author list
        val poemListOut = mutableListOf<PoemSelection>()
        val haikuListOut = mutableListOf<HaikuSelection>()
        val quoteListOut = mutableListOf<QuoteSelection>()


        //moving around these items
        val poemRecycleView = findViewById<RecyclerView>(R.id.poemRecyclerView)
        val haikuRecycleView = findViewById<RecyclerView>(R.id.haikuRecyclerView)
        val quoteRecycleView = findViewById<RecyclerView>(R.id.quoteRecyclerView)


        // this will query the database and add the items to the empty poem list
        val poemQuery: Query = mDatabase.child("Poems")
        val haikuQuery: Query = mDatabase.child("Haikus")
        val quoteQuery: Query = mDatabase.child("Quotes")

        poemQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Goes through each item in this part of the database and add it to the empty list

                for (singleSnapshot in dataSnapshot.children) {
                    val poems = PoemSelection()
                    poems.poem = singleSnapshot.child("poem").value.toString()
                    poems.author = singleSnapshot.child("author").value.toString()
                    poems.title = singleSnapshot.child("title").value.toString()
                    poemListOut.add(poems)
                }

                // this will take the list of poems and implement their layout in the adapter using the recyclerview

                val layoutManager = LinearLayoutManager(this@PoemActivity)
                poemRecycleView.layoutManager = layoutManager
                val adapter = PoemAdapter(poemListOut)
                poemRecycleView.adapter = adapter

            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("Server Response", "onCancelled", databaseError.toException())
            }

        })

        haikuQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Goes through each item in this part of the database and add it to the empty list

                for (singleSnapshot in dataSnapshot.children) {
                    val haikus = HaikuSelection()
                    haikus.haiku = singleSnapshot.child("haiku").value.toString()
                    haikus.author = singleSnapshot.child("author").value.toString()
                    haikus.title = singleSnapshot.child("title").value.toString()
                    haikuListOut.add(haikus)
                }

                // this will take the list of haikus and implement their layout in the adapter using the recyclerview

                val layoutManager = LinearLayoutManager(this@PoemActivity)
                haikuRecycleView.layoutManager = layoutManager
                val adapter = HaikuAdapter(haikuListOut)
                haikuRecycleView.adapter = adapter

            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("Server Response", "onCancelled", databaseError.toException())
            }

        })

        quoteQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Goes through each item in this part of the database and add it to the empty list

                for (singleSnapshot in dataSnapshot.children) {
                    val quotes = QuoteSelection()
                    quotes.quote = singleSnapshot.child("quote").value.toString()
                    quotes.author = singleSnapshot.child("author").value.toString()
                    quotes.title = singleSnapshot.child("title").value.toString()
                    quoteListOut.add(quotes)
                }

                // this will take the list of quotes and implement their layout in the adapter using the recyclerview

                val layoutManager = LinearLayoutManager(this@PoemActivity)
                quoteRecycleView.layoutManager = layoutManager
                val adapter = QuoteAdapter(quoteListOut)
                quoteRecycleView.adapter = adapter

            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("Server Response", "onCancelled", databaseError.toException())
            }

        })



        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //////This section is for handling the functionality of the buttons on the page
        val preuploadButton = findViewById<Button>(R.id.preuploadButton)
        val uploadButton = findViewById<Button>(R.id.uploadButton)
        val signOut = findViewById<ImageView>(R.id.signOutImageView)
        val backOut = findViewById<ImageView>(R.id.backImageView)

        // this boolean tracks the back button and allows it to do different things based on it's current value
        var open = true

        //this function will update the UI based on the conditions involved for the button
        fun updateUploadUI(){
            preuploadButton.visibility = View.VISIBLE
            nameEditText.visibility = View.INVISIBLE
            titleEditText.visibility = View.INVISIBLE
            poemEditText.visibility = View.INVISIBLE
            uploadButton.visibility = View.INVISIBLE
            open = true
        }

        // this function takes the value of "open" and updates the functionality of the back button accordingly
        fun updateButton(){
            if (!open ){
                backOut.setOnClickListener( View.OnClickListener {

                    if (quotesRadioButton.isChecked){
                        quoteRecycleView.visibility = View.VISIBLE
                    }else if (haikusRadioButton.isChecked){
                        haikuRecycleView.visibility = View.VISIBLE
                    }else if (poemsRadioButton.isChecked){
                        poemRecycleView.visibility = View.VISIBLE
                    }
                    updateUploadUI()
                    updateButton()

                })
            }else{
                backOut.setOnClickListener( View.OnClickListener {

                    val intent = Intent(this,MainActivity::class.java)
                    //this.finish() users dont like this
                    startActivity(intent)
                })
            }
        }
        updateButton()

        //used for setting the title, explanation needed here
        var titleText = findViewById<EditText>(R.id.titleEditText)

        // This will take you the the upload screen
        preuploadButton.setOnClickListener {

            //changes the hint left in the quote title section so users don't try to enter a title (unneeded for a quote)

            if (quotesRadioButton.isChecked){
                quoteRecycleView.visibility = View.INVISIBLE
                titleText.hint = "No Title Required"
            }else if (haikusRadioButton.isChecked){
                haikuRecycleView.visibility = View.INVISIBLE
                titleText.hint = "Type Title Here"
            }else if (poemsRadioButton.isChecked){
                poemRecycleView.visibility = View.INVISIBLE
                titleText.hint = "Type Title Here"
            }

            preuploadButton.visibility = View.INVISIBLE
            nameEditText.visibility = View.VISIBLE
            titleEditText.visibility = View.VISIBLE
            poemEditText.visibility = View.VISIBLE
            uploadButton.visibility = View.VISIBLE
            backOut.visibility = View.VISIBLE
            open = false
            updateButton()
        }
        //This will allow you to upload content and return to the individual content screens. Needs more explanation inside the function
        uploadButton.setOnClickListener {

            val userPoem = PoemSelection()
            val userHaiku = HaikuSelection()
            val userQuote = QuoteSelection()



            if (quotesRadioButton.isChecked){

                userQuote.author = nameEditText.text.toString()
                userQuote.quote = poemEditText.text.toString()
                userQuote.title = "Quote"

                if( userQuote.author == "" || userQuote.quote == "" ){

                    Toast.makeText(applicationContext, "One of the required fields is blank. Please try again.", Toast.LENGTH_SHORT).show()
                }else{

                    mDatabase.child("User Posts").child(email +  " " + uploadCount.toString() + " " + currentTime.time.toString()).setValue(userQuote)
                    updateButton()
                    updateUploadUI()
                    quoteRecycleView.visibility = View.VISIBLE
                }

            }else if (haikusRadioButton.isChecked){

                userHaiku.author = nameEditText.text.toString()
                userHaiku.haiku = poemEditText.text.toString()
                userHaiku.title = titleEditText.text.toString()

                if( userHaiku.author == "" || userHaiku.haiku == ""  || userHaiku.title == ""){

                    Toast.makeText(applicationContext, "One of the required fields is blank. Please try again.", Toast.LENGTH_SHORT).show()
                }else{

                    mDatabase.child("User Posts").child(email +  " " + uploadCount.toString() + " " + currentTime.time.toString()).setValue(userHaiku)
                    updateButton()
                    updateUploadUI()
                    haikuRecycleView.visibility = View.VISIBLE
                }

            }else if (poemsRadioButton.isChecked){

                userPoem.author = nameEditText.text.toString()
                userPoem.poem = poemEditText.text.toString()
                userPoem.title = titleEditText.text.toString()

                if( userPoem.author == "" || userPoem.poem == ""  || userPoem.title == ""){

                    Toast.makeText(applicationContext, "One of the required fields is blank. Please try again.", Toast.LENGTH_SHORT).show()
                }else{

                    mDatabase.child("User Posts").child(email +  " " + uploadCount.toString() + " " + currentTime.time.toString()).setValue(userHaiku)
                    updateButton()
                    updateUploadUI()
                    poemRecycleView.visibility = View.VISIBLE
                }
            }

            uploadCount++
        }
        // This will allow you to sign out and return you to the login screen
        signOut.setOnClickListener( View.OnClickListener {

            mAuth.signOut()
            if (mAuth.currentUser == null){
                Toast.makeText(this,
                        "You have successfully logged out.",
                        Toast.LENGTH_SHORT).show()

                val intent = Intent(this,MainActivity::class.java)
                this.finish()
                startActivity(intent)
            }else{
                Toast.makeText(this,
                        "You are still logged in.",
                        Toast.LENGTH_SHORT).show()
            }
        })

        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /////////This section is for handling the radio buttons////////////////////////////////////




        poemsRadioButton.isChecked = true
        haikuRecycleView.visibility = View.INVISIBLE
        quoteRecycleView.visibility = View.INVISIBLE

        poemsRadioButton.setOnClickListener {

            if (uploadButton.visibility == View.VISIBLE){

                titleText.hint = "Type Title Here"
            }else{

                poemRecycleView.visibility = View.VISIBLE
                haikuRecycleView.visibility = View.INVISIBLE
                quoteRecycleView.visibility = View.INVISIBLE
            }

        }

        quotesRadioButton.setOnClickListener {

            if (uploadButton.visibility == View.VISIBLE){

                titleText.hint = "No Title Required"
            }else{

                quoteRecycleView.visibility = View.VISIBLE
                haikuRecycleView.visibility = View.INVISIBLE
                poemRecycleView.visibility = View.INVISIBLE
            }


        }

        haikusRadioButton.setOnClickListener {

            if (uploadButton.visibility == View.VISIBLE){

                titleText.hint = "Type Title Here"
            }else{

                haikuRecycleView.visibility = View.VISIBLE
                poemRecycleView.visibility = View.INVISIBLE
                quoteRecycleView.visibility = View.INVISIBLE
            }


        }


        ////////////////////////////////////////////////////////////////////////////////////
/*
        val channelid = "newUploads"
        val mBuilder = NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.journal)
                .setContentTitle("New Uploads")
                .setContentText("New Poems Have Been Uploaded")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setChannelId(channelid)


        var notificationManager = NotificationManagerCompat.from(this)
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(1, mBuilder.build())

        fun addUserIDToDatabase(){
            val userID = FirebaseAuth.getInstance().uid
            val emails = mAuth.currentUser!!.email.toString()
            mDatabase.child(emails.replace(".","")).child("userID").setValue(userID)
        }


        var test = true
        val userQuery: Query = mDatabase.child(mAuth.currentUser!!.email.toString().replace(".",""))

        userQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Goes through each item in this part of the database to determine is the user exists

                for (singleSnapshot in dataSnapshot.children) {
                    val user = singleSnapshot.value.toString()

                    println(user)
                }

                //if (!test || userQuery == null){
                //   addUserIDToDatabase()
                //    println("JUST ADDED")
                //}else{
                //    println("it's already in the database!!!!!")
               // }
                println(test)
                println(userQuery)

                //println(FirebaseAuth.getInstance().uid.toString())

            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("Server Response", "onCancelled", databaseError.toException())
            }
        })



        //println(count)


        //addUserIDToDatabase()


*/
    }

}
