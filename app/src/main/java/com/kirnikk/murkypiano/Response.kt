package com.kirnikk.murkypiano

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kirnikk.murkypiano.databinding.HotelItemBinding
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface HotelApi {
    @Headers("X-RapidAPI-Key: 2d0aa0e228msh890f2bcd85d6ad1p1aad06jsnfb74638e2c5b")
    @GET("./locations/v2/search?query=athens")
    fun getHotels(): Single<HotelsResponse>

    @Headers("X-RapidAPI-Key: 2d0aa0e228msh890f2bcd85d6ad1p1aad06jsnfb74638e2c5b")
    @GET("/properties/get-details")
    fun getHotel(@Query("id") id: Int): Single<SelectedHotelResponse>

    @Headers("X-RapidAPI-Key: 2d0aa0e228msh890f2bcd85d6ad1p1aad06jsnfb74638e2c5b")
    @GET("/properties/get-hotel-photos")
    fun getImage(@Query("id")id: Int): Single<ImageResponse>
}

class EntityAdapter(
    private val onItemClicked: (String) -> Unit
) :
    ListAdapter<Entity, EntityViewHolder>(EntityDiffUtilCallBack()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntityViewHolder {
        return EntityViewHolder(
            HotelItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: EntityViewHolder, position: Int) {
        holder.bind(getItem(position), onItemClicked)
    }
}

class EntityViewHolder(private val binding: HotelItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(entity: Entity, onItemClicked: (String) -> Unit) {
        binding.hotelInfo.text = entity.name
        binding.root.setOnClickListener {
            onItemClicked(entity.destinationId)
        }
    }
}

class EntityDiffUtilCallBack : DiffUtil.ItemCallback<Entity>() {
    override fun areItemsTheSame(oldItem: Entity, newItem: Entity): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: Entity, newItem: Entity): Boolean {
        return oldItem == newItem
    }
}

data class HotelsResponse(
    val suggestions: List<Suggestion>
)

data class Suggestion(
    val group: String,
    val entities: List<Entity>
)

data class Entity(
    val destinationId: String,
    val name: String
)

data class ImageResponse(
    val hotelImages: List<HotelImage>
)

data class HotelImage(
    val baseUrl: String
)

data class SelectedHotelResponse(
    val data: Data
)

data class Data(
    val body: Body
)

data class Body(
    val overview: OverView,
    val propertyDescription: PropertyDescription
)

data class OverView(
    val overviewSections: List<Section>
)

data class Section(
    val type: String,
    val content: List<String>
)

data class PropertyDescription(
    val address: Address,
    val starRatingTitle: String
)

data class Address(
    val fullAddress: String
)