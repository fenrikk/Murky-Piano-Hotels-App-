package com.kirnikk.murkypiano

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import com.kirnikk.murkypiano.databinding.ActivityHotelObserveBinding
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class HotelObserveActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHotelObserveBinding
    private lateinit var api: HotelApi
    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHotelObserveBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val id = intent.extras?.getInt("ID") ?: 0
        Log.d("HOTELS", "onCreate: $id")

        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://hotels4.p.rapidapi.com")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()

        api = retrofit.create(HotelApi::class.java)

        compositeDisposable.add(
            api.getHotel(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    binding.address.text = it.data.body.propertyDescription.address.fullAddress
                    binding.amenities.text =
                        it.data.body.overview.overviewSections[0].content.toString()
                    binding.rating.text = it.data.body.propertyDescription.starRatingTitle
                }, {
                    it.printStackTrace()
                })

        )
        compositeDisposable.add(
            api.getImage(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    val response = it.hotelImages[0].baseUrl
                    val image = response.replace("{size}", "z")
                    Glide.with(binding.photo).load(image).circleCrop().into(binding.photo)
                }, {
                    it.printStackTrace()
                })
        )

        binding.backButton.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }
}