package id.andra.foreblack.di

import androidx.lifecycle.LifecycleOwner
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import id.andra.foreblack.feature_main.util.ServiceLifecycleOwner
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideServiceLifeCycleOwner(): LifecycleOwner {
        return ServiceLifecycleOwner()
    }

}