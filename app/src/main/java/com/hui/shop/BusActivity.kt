package com.hui.shop

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_bus.*
import kotlinx.android.synthetic.main.row_bus.view.*
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.net.URL

class BusActivity : AppCompatActivity() {
    private val TAG = BusActivity::class.java.simpleName
    var buses: Buses? = null
    val retrofit = Retrofit.Builder()
        .baseUrl("https://data.tycg.gov.tw/opendata/datalist/datasetMeta/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bus)

        val Bus = "https://data.tycg.gov.tw/opendata/datalist/datasetMeta/download?id=b3abedf0-aeae-4523-a804-6e807cbad589&rid=bf55b21a-2b7c-4ede-8048-f75420344aed"
        BusTask().execute(Bus)

        recycler.layoutManager = LinearLayoutManager(this@BusActivity)
        recycler.setHasFixedSize(true)
    }


    inner class BusTask : AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg pamams: String?): String {
            val url = URL(pamams[0])
            val json = url.readText()
            parseGson(json)
            return json
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            recycler.adapter = BusAdapter()
        }
    }

    private fun parseGson(json: String) {
        val busService = retrofit.create(BusService::class.java)
        buses = busService.listBus()
            .execute()
            .body()
        buses?.datas?.forEach {
            Log.d(TAG, "Bus: ${it.BusID} ${it.RouteID} ${it.Speed}")
        }
    }

    inner class BusAdapter() : RecyclerView.Adapter<BusHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_bus, parent, false)
            return BusHolder(view)
        }

        override fun getItemCount(): Int {
            return buses?.datas?.size?: 0
        }

        override fun onBindViewHolder(holder: BusHolder, position: Int) {
            val bus = buses?.datas?.get(position)
            holder.bindBus(bus!!)
        }

    }


    inner class BusHolder(view: View) : RecyclerView.ViewHolder(view) {
        val BusIDText : TextView = view.BusID
        val RouteIDText : TextView = view.RouteID
        val SpeedText : TextView = view.Speed
        fun bindBus(bus: Bus) {
            BusIDText.text = bus.BusID
            RouteIDText.text = bus.RouteID
            SpeedText.text = bus.Speed
        }
    }
}

data class Buses(
    val datas: List<Bus>
)

data class Bus(
    val Azimuth: String,
    val BusID: String,
    val BusStatus: String,
    val DataTime: String,
    val DutyStatus: String,
    val GoBack: String,
    val Latitude: String,
    val Longitude: String,
    val ProviderID: String,
    val RouteID: String,
    val Speed: String,
    val ledstate: String,
    val sections: String
)

interface BusService {
    @GET("download?id=b3abedf0-aeae-4523-a804-6e807cbad589&rid=bf55b21a-2b7c-4ede-8048-f75420344aed")
    fun listBus(): Call<Buses>
}