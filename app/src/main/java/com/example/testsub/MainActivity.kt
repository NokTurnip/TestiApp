package com.example.testsub

import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.appodeal.ads.Appodeal
import com.appodeal.ads.Appodeal.cache
import com.appodeal.ads.Appodeal.getNativeAds
import com.appodeal.ads.Appodeal.hide
import com.appodeal.ads.Appodeal.initialize
import com.appodeal.ads.Appodeal.isInitialized
import com.appodeal.ads.Appodeal.isLoaded
import com.appodeal.ads.Appodeal.muteVideosIfCallsMuted
import com.appodeal.ads.Appodeal.setBannerCallbacks
import com.appodeal.ads.Appodeal.setInterstitialCallbacks
import com.appodeal.ads.Appodeal.setRewardedVideoCallbacks
import com.appodeal.ads.Appodeal.show
import com.appodeal.ads.BannerCallbacks
import com.appodeal.ads.InterstitialCallbacks
import com.appodeal.ads.NativeAd
import com.appodeal.ads.NativeCallbacks
import com.appodeal.ads.RewardedVideoCallbacks


class MainActivity : AppCompatActivity() {
    private lateinit var hideButton: Button
    private lateinit var bannerButton: Button
    private lateinit var interstitialButton: Button
    private lateinit var rewardedVideoButton: Button
    private lateinit var nativeButton: Button
    var recyclerView: RecyclerView? = null
    var nativeAds: MutableList<NativeAd> = ArrayList()
    var adapter: AdapterN? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        Appodeal.setTesting(testMode = true)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        isLoaded(Appodeal.BANNER)
        isInitialized(Appodeal.BANNER)
        isLoaded(Appodeal.INTERSTITIAL)
        muteVideosIfCallsMuted(true)
        isLoaded(Appodeal.REWARDED_VIDEO)

        bannerButton = findViewById(R.id.banner_button)
        bannerButton!!.setOnClickListener { view ->
            showBanner(view)
        }
        interstitialButton = findViewById(R.id.interstitial_button)
        interstitialButton!!.setOnClickListener { view ->
            showInterstitial(view)
        }

        rewardedVideoButton = findViewById(R.id.rewarded_button)
        rewardedVideoButton!!.setOnClickListener { view ->
            showRewardedVideo(view)
        }

        nativeButton = findViewById(R.id.native_button)
        nativeButton!!.setOnClickListener { view ->
            showNativeAd(view)
        }

        hideButton = findViewById(R.id.hide_button)
        hideButton!!.setOnClickListener { view ->
            hideAds()
        }


        recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView!!.setLayoutManager(LinearLayoutManager(this@MainActivity))
        adapter = AdapterN(nativeAds)
        recyclerView!!.setAdapter(adapter)
        configureAds()
    }

    var bannerCount = 0
    fun showBanner(view: View?) {
        hide(this@MainActivity, Appodeal.NATIVE)
        show(this@MainActivity, Appodeal.BANNER_TOP)
        bannerCount = 0
    }

    fun showInterstitial(view: View?) {
        show(this@MainActivity, Appodeal.INTERSTITIAL)
        hide(this@MainActivity, Appodeal.BANNER)
    }

    fun showRewardedVideo(view: View?) {
        show(this@MainActivity, Appodeal.REWARDED_VIDEO)
    }

    fun showNativeAd(view: View?) {
        recyclerView!!.visibility = View.VISIBLE
        bannerButton!!.visibility = View.GONE
        hide(this@MainActivity, Appodeal.BANNER)
        interstitialButton!!.visibility = View.GONE
        rewardedVideoButton!!.visibility = View.GONE
        nativeButton!!.visibility = View.GONE
    }



    fun hideAds() {
        hide(this@MainActivity, Appodeal.INTERSTITIAL or Appodeal.BANNER or Appodeal.REWARDED_VIDEO or Appodeal.NATIVE)
        recyclerView!!.visibility = View.GONE
        bannerButton!!.visibility = View.VISIBLE
        interstitialButton!!.visibility = View.VISIBLE
        rewardedVideoButton!!.visibility = View.VISIBLE
        nativeButton!!.visibility = View.VISIBLE
    }

    fun configureAds() {
        initialize(this@MainActivity, "5fd1ca355a1d1d9a1e20354c72725b5915534db14ccba1ac", Appodeal.BANNER or Appodeal.INTERSTITIAL or Appodeal.REWARDED_VIDEO or Appodeal.NATIVE)
        cache(this@MainActivity, Appodeal.NATIVE, 5)
        if (isLoaded(Appodeal.NATIVE) || nativeAds.size > 0) {
            nativeButton!!.isEnabled = true
        }
        Appodeal.setNativeCallbacks(object : NativeCallbacks {
            override fun onNativeLoaded() {
                nativeButton!!.isEnabled = true
                val nativeBuffer = getNativeAds(1)
                if (nativeBuffer.isNotEmpty() && nativeAds.size < 5) {
                    nativeAds.add(nativeBuffer[nativeBuffer.size - 1])
                    adapter!!.notifyItemInserted(0)

                }
            }


            override fun onNativeFailedToLoad() {}
            override fun onNativeShown(nativeAd: NativeAd?) {}
            override fun onNativeShowFailed(nativeAd: NativeAd?) {}
            override fun onNativeClicked(nativeAd: NativeAd?) {}
            override fun onNativeExpired() {}
        })
        setBannerCallbacks(object : BannerCallbacks {
            override fun onBannerLoaded(height: Int, isPrecache: Boolean) {
                bannerButton!!.isEnabled = true
            }

            override fun onBannerFailedToLoad() {}
            override fun onBannerShown() {
                bannerCount++
                if (bannerCount > 5) {
                    hideAds()
                    bannerCount = 0
                }
            }

            override fun onBannerShowFailed() {}
            override fun onBannerClicked() {}
            override fun onBannerExpired() {}
        })
        var interstitialLock = false
        setInterstitialCallbacks(object : InterstitialCallbacks {
            override fun onInterstitialLoaded(isPrecache: Boolean) {
                if (!interstitialLock) {
                    interstitialButton!!.isEnabled = true
                }
            }

            override fun onInterstitialFailedToLoad() {}
            override fun onInterstitialShown() {
                interstitialButton!!.isEnabled = false
                interstitialLock = true
            }

            override fun onInterstitialShowFailed() {}
            override fun onInterstitialClicked() {}
            override fun onInterstitialClosed() {
                object : CountDownTimer(60000, 1000) {
                    override fun onTick(millisUntilFinished: Long) {}
                    override fun onFinish() {
                        interstitialButton!!.isEnabled = true
                        interstitialLock = false
                    }
                }.start()
            }

            override fun onInterstitialExpired() {}
        })
        var videoCount = 3
        setRewardedVideoCallbacks(object : RewardedVideoCallbacks {
            override fun onRewardedVideoLoaded(isPrecache: Boolean) {
                if (videoCount > 0) {
                    rewardedVideoButton!!.isEnabled = true
                }
            }

            override fun onRewardedVideoFailedToLoad() {}
            override fun onRewardedVideoShown() {
                videoCount--
                if (videoCount <= 0) {
                    rewardedVideoButton!!.isEnabled = false
                }
            }

            override fun onRewardedVideoShowFailed() {}
            override fun onRewardedVideoFinished(amount: Double, name: String?) {}
            override fun onRewardedVideoClosed(finished: Boolean) {}
            override fun onRewardedVideoExpired() {}
            override fun onRewardedVideoClicked() {}
        })
    }
}