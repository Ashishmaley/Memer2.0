package com.example.apimaster

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest.*
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var adView : AdView
    private var mInterstitialAd: InterstitialAd? = null
    private lateinit var myAdapter: MyAdapter
    private lateinit var refreshButton: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var cardu: CardView
    private lateinit var cardv: CardView

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
        }

    }

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
        recyclerView = findViewById(R.id.recyclerView)
        progressBar = findViewById(R.id.progressBar)
        progressBar.visibility = View.VISIBLE
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
                progressBar.visibility = View.GONE
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
                progressBar.visibility = View.GONE
                Log.d("Main Activity", "OnFailure: " + t.message)
                swipeRefreshLayout.isRefreshing = false // Mark the refresh as complete
            }
        })
    }

    private fun intersAdd() {

        var adRequest = Builder().build()

        InterstitialAd.load(
            this,
            "ca-app-pub-1500145166835548/5415527257",
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    adError?.toString()?.let { Log.d(TAG, it) }
                    mInterstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    Log.d(TAG, "Ad was loaded.")
                    mInterstitialAd = interstitialAd
                }
            })

    }
}


