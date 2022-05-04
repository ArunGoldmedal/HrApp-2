package com.goldmedal.hrapp.di

import android.content.Context
import com.goldmedal.hrapp.data.db.AppDatabase
import com.goldmedal.hrapp.data.network.MyApi
import com.goldmedal.hrapp.data.network.NetworkConnectionInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    @Provides
    fun provideMyApi(networkConnectionInterceptor: NetworkConnectionInterceptor) : MyApi{
        return MyApi.invoke(networkConnectionInterceptor)
    }


    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context) : AppDatabase {
        return AppDatabase.invoke(context)
    }
}