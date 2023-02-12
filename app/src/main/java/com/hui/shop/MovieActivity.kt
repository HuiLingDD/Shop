package com.hui.shop

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_movie.*
import kotlinx.android.synthetic.main.row_movie.view.*
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET
import java.net.URL

class MovieActivity : AppCompatActivity() {
    private val TAG = MovieActivity::class.java.simpleName
    var movies : List<Movie>? = null
    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.jsonserve.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie)

        /*// Anko
        doAsync {
            val url = URL("https://api.jsonserve.com/y-P1VA").readText
            movies = Gson().fromJson<List<Movie>>(json,
                object : TypeToken<List<Movie>>(){}.type)
            val movieService = retrofit.create(MovieService::class.java)
            movies = movieService.listMovies()
                .execute()
                .body()
            movies?.forEach {
//             info("${it.Title} ${it.imdbRating}")
        }
            uiThread{
                recycler.layoutManager = LinearLayoutManager(this@MovieActivity)
                recycler.setHasFixedSize(true)
                recycler.adapter = MovieAdapter()
            }
        }*/

        // get json
        val Movie = "https://api.jsonserve.com/y-P1VA"
        MovieTask().execute(Movie)

        recycler.layoutManager = LinearLayoutManager(this@MovieActivity)
        recycler.setHasFixedSize(true)
    }

    inner class MovieTask : AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg params: String?): String {
            val url = URL(params[0])
            val json = url.readText()
            parseGson(json)
            return json
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            recycler.adapter = MovieAdapter()
        }
    }

    private fun parseGson(json: String?) {
//        movies = Gson().fromJson<List<Movie>>(json,
//            object : TypeToken<List<Movie>>(){}.type)
        val movieServie = retrofit.create(MovieServie::class.java)
        movies = movieServie.listMovies()
            .execute()
            .body()
        movies?.forEach {
//            info("${it.Title} ${it.imdbRating}")
            Log.d(TAG, "${it.Title} ${it.imdbRating}")
        }
    }

    inner class MovieAdapter() : RecyclerView.Adapter<MovieHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_movie, parent, false)
            return MovieHolder(view)
        }

        override fun getItemCount(): Int {
            val size = movies?.size?: 0
            return size
        }

        override fun onBindViewHolder(holder: MovieHolder, position: Int) {
            val movie = movies?.get(position)
            holder.bindMovie(movie!!)
        }

    }

    inner class MovieHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleText : TextView = view.movie_title
        val imdbText : TextView = view.movie_imdb
        val directorText : TextView = view.movie_director
        val posterImage : ImageView = view.movie_poster
        fun bindMovie(movie: Movie) {
            titleText.text = movie.Title
            imdbText.text = movie.imdbRating
            directorText.text = movie.Director
            Glide.with(this@MovieActivity)
                .load(movie.Images[0])
                .override(300)
                .into(posterImage)
        }
    }
}

//class Movie : ArrayList<MovieItem>()

data class Movie(
    val Actors: String,
    val Awards: String,
    val ComingSoon: Boolean,
    val Country: String,
    val Director: String,
    val Genre: String,
    val Images: List<String>,
    val Language: String,
    val Metascore: String,
    val Plot: String,
    val Poster: String,
    val Rated: String,
    val Released: String,
    val Response: String,
    val Runtime: String,
    val Title: String,
    val Type: String,
    val Writer: String,
    val Year: String,
    val imdbID: String,
    val imdbRating: String,
    val imdbVotes: String,
    val totalSeasons: String
)

interface MovieServie {
    @GET("y-P1VA")
    fun listMovies(): Call<List<Movie>>
}