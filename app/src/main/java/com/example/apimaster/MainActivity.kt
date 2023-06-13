package com.example.apimaster

import CacheManager
import android.app.Dialog
import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat.OrientationMode
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest.Builder
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private var mInterstitialAd: InterstitialAd? = null
    private lateinit var myAdapter: MyAdapter
    private lateinit var refreshButton: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var cardu: CardView
    private lateinit var cardv: CardView
    private lateinit var cacheManager: CacheManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fetchData()
        intersAdd()
        refreshButton = findViewById(R.id.refreshButton)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        swipeRefreshLayout.setOnRefreshListener {
            showAdd() // Call the method to fetch data again
        }
        refreshButton.setOnClickListener {
            showAdd()
            cacheManager = CacheManager(this)
            fetchData()
        }
    }

    // Function to fetch data from the server
//    private fun fetchDataFromServer() {
//        // Perform the API request to fetch data from the server
//
//        // Once the response is received, check the cache size
//        cacheManager.checkCacheSize()
//
//        // Process and cache the fetched data
//        // ...
//    }
//
//    // Function to retrieve data from the cache
//    private fun retrieveDataFromCache() {
//        // Check if the data exists in the cache and is not expired
//        // If the data is available and not expired, retrieve it from the cache
//        // ...
//
//        // If the data is not available or expired, fetch it from the server
//        fetchDataFromServer()
//    }
//
//
//
//    fun isInternetOff(context: Context): Boolean {
//        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            val network = connectivityManager.activeNetwork ?: return true
//            val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return true
//
//            return !networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
//        } else {
//            val activeNetworkInfo = connectivityManager.activeNetworkInfo ?: return true
//
//            return !activeNetworkInfo.isConnected
//        }
//    }


    private fun showAdd() {
        if (mInterstitialAd!=null)
        {
            mInterstitialAd?.fullScreenContentCallback=object :FullScreenContentCallback(){
                override fun onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent()
                    fetchData()
                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    super.onAdFailedToShowFullScreenContent(p0)
                    fetchData()
                }

            }
            mInterstitialAd?.show(this)
        }
        else{
           fetchData()
        }
    }

    private fun fetchData() {
        cardu = findViewById(R.id.cardView)
        cardv = findViewById(R.id.cardView2)
//        val isIt = isInternetOff(this)
//        if (!isIt)
//        {
//           val dialog = ProgressDialog(this)
//            dialog.setMessage("Check your Internet")
//            dialog.setCancelable(false)
//        }

        recyclerView = findViewById(R.id.recyclerView)
        cardu.visibility = View.VISIBLE
        cardv.visibility = View.VISIBLE
        val retrofitBuilder = Retrofit.Builder()
            .baseUrl("https://meme-api.com/gimme/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiInterface::class.java)

        val retrofitData = retrofitBuilder.getProduct()

        retrofitData.enqueue(object : Callback<MemeData?> {
            override fun onResponse(call: Call<MemeData?>, response: Response<MemeData?>) {
                cardu.visibility = View.GONE
                cardv.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE

                val obj = response.body()
                val product = obj?.memes!!

                myAdapter = MyAdapter(this@MainActivity, product)
                recyclerView.adapter = myAdapter
                recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
                swipeRefreshLayout.isRefreshing = false // Mark the refresh as complete
            }

            override fun onFailure(call: Call<MemeData?>, t: Throwable) {
                Log.d("Main Activity", "OnFailure: " + t.message)
                swipeRefreshLayout.isRefreshing = false // Mark the refresh as complete
            }
        })


    }

    private fun intersAdd() {

        val adRequest = Builder().build()

        InterstitialAd.load(
            this,
            "ca-app-pub-1500145166835548/5415527257",
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    adError.toString().let { Log.d(TAG, it) }
                    mInterstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    Log.d(TAG, "Ad was loaded.")
                    mInterstitialAd = interstitialAd
                }
            })

    }


}


