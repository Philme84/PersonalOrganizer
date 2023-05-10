package com.pascal.personalorganizer.di

import android.content.Context
import com.pascal.personalorganizer.data.remote.WeatherApiService
import com.pascal.personalorganizer.util.Constants.BASE_URL
import com.pascal.personalorganizer.util.exception.ResultCallAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun providesRetrofit(client: OkHttpClient): Retrofit{
        return Retrofit.Builder()
            .client(client)
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // Gson converted to map the json response to our data classes
            .addCallAdapterFactory(ResultCallAdapterFactory()) // This adapter comes from the utils and will let us know if the call is success or failure
            .build()
    }

    @Provides
    @Singleton
    fun provideClient(@ApplicationContext context: Context): OkHttpClient{
        return OkHttpClient().newBuilder()
            .addInterceptor {
                val request = it.request().newBuilder()
                    .addHeader("key", "4675ba94cb75425db83115833230504") // We pass the key using headers
                it.proceed(request.build())
            }
            .build()
    }

    @Provides
    @Singleton
    fun provideWeatherApiClient(retrofit: Retrofit): WeatherApiService {   //We are providing our WeatherApiService with a retrofit instance
        return retrofit.create(WeatherApiService::class.java)
    }


}