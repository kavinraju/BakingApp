package skr_developer.bakingapp.Retrofit;

import android.content.Context;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 *  We use Retrofit to make a network call Asynchronously
 */
public class ApiClient {

    private static Retrofit retrofit = null;
    private static OkHttpClient okHttpClient;

    // This method get Context as parameter and return Retrofit Instance.
    //public static Retrofit getClient(Context context, final SimpleIdlingResource simpleIdlingResource){
    public static Retrofit getClient(Context context){

        if (okHttpClient == null){
            initializeOkhttp();
        }

        if (retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://d17h27t6h515a5.cloudfront.net/")
                    .client(okHttpClient)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

        }
        return retrofit;
    }

    // This method initializes OkHttpClient
    private static void initializeOkhttp() {

        OkHttpClient.Builder okHttpBuilder= new OkHttpClient().newBuilder();

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        okHttpBuilder.addInterceptor(interceptor);

        okHttpBuilder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {

                return chain.proceed(
                        chain
                        .request()
                        .newBuilder().build());
            }
        });

        okHttpClient = okHttpBuilder.build();
    }
}
