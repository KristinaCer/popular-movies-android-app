package android.example.popularmovies.requests;

import android.example.popularmovies.AppExecutors;
import android.example.popularmovies.models.Movie;
import android.example.popularmovies.requests.responses.MovieResponse;
import android.example.popularmovies.utils.Constants;
import android.util.Log;;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


 /*The client class is initiated only once - if it has not been created yet, using Singleton pattern.
    If the object has been already created, the instance of the created object is returned.

    Since we create the movies DB API by using Retrofit instance withing the private MovieDBService constructor,
    it is also created only once. Some further info on why I decided to keep it as a singleton up here:
    https://stackoverflow.com/questions/36628399/should-i-use-retrofit-with-a-singleton*/

public class MovieDBClient {

    private static MovieDBClient instance;
    private MoviesDBApi moviesDBApi;
    private MutableLiveData<List<Movie>> mMovies;
    private static final String TAG = "MoviesDBService";

    private MovieDBClient() {
        mMovies = new MutableLiveData<List<Movie>>();

        Retrofit.Builder retrofitBuilder =
                new Retrofit.Builder()
                        .baseUrl(Constants.BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = retrofitBuilder.build();
        moviesDBApi = retrofit.create(MoviesDBApi.class);

        Log.d(TAG, "MovieDBApi created using " + Constants.BASE_URL);
    }

    public static MovieDBClient getInstance() {
        if (instance == null) {
            return instance = new MovieDBClient();
        } else {
            return instance;
        }
    }

    public LiveData<List<Movie>> getMovies() {
        return mMovies;
    }

    public MoviesDBApi getMoviesDBApi() {
        return moviesDBApi;
    }

    //Making a request to a movie DB api server:

    public void retrieveMoviesAPI() {
        final Future handler = AppExecutors.getInstance().getNetworkIO().submit()
        AppExecutors.getInstance().getNetworkIO().schedule(new Runnable() {
            @Override
            public void run() {
                handler.cancel(true);
            }
        }, Constants.NETWORK_TIMEOUT, TimeUnit.MILLISECONDS);
    }

    private class RetrieveMoviesRunnable implements Runnable {

        private String query;
        private int pageNo;
        private boolean cancelRequest;

        public RetrieveMoviesRunnable(String query, int pageNo) {
            this.query = query;
            this.pageNo = pageNo;
            this.cancelRequest = false;
        }

        @Override
        public void run() {
            try {
                if(cancelRequest){
                    return;
                }
                Response response = this.getMovies(this.query, this. pageNo).execute();
                //if everything is OK:
                if(response.code() == 200){

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private Call<MovieResponse> getMovies(String query, int pageNo){
            return moviesDBApi.getMoviesByCategory(
                    query,
                    Constants.API_KEY,
                    pageNo
                    );
        }

        private void cancelRequest(){
            Log.d(TAG, "Cancelling the search request.");
            cancelRequest = true;
        }
    }
}

