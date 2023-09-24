package com.example.testsub

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.appodeal.ads.NativeAd
import com.appodeal.ads.native_ad.views.NativeAdView


class AdapterN(private val nativeAds: List<NativeAd>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.nativee2, parent, false) //
        return NativeAdViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is NativeAdViewHolder) {
            val nativeAdView = holder.nativeAdView
            val nativeAd = nativeAds[position]
            nativeAdView.setNativeAd(nativeAd)
        }
    }

    override fun getItemCount(): Int {
        return nativeAds.size
    }

    class NativeAdViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var nativeAdView: NativeAdView

        init {
            nativeAdView = itemView.findViewById<NativeAdView>(R.id.native_ad_view_news_feed)
        }
    }
}