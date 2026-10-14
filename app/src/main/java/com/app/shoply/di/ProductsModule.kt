package com.app.shoply.di

import com.app.shoply.data.remote.ProductSearchDataSource
import com.app.shoply.data.remote.ProductSearchDataSourceImpl
import com.app.shoply.data.remote.api.ProductSearchService
import com.app.shoply.data.remote.mapper.ProductMapper
import com.app.shoply.data.repo.ProductRepository
import com.app.shoply.data.repo.ProductRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProductsModule {

    @Provides
    @Singleton
    fun provideProductSearchDataSource(
        service: ProductSearchService,
        mapper: ProductMapper,
    ): ProductSearchDataSource {
        return ProductSearchDataSourceImpl(service, mapper)
    }

    @Provides
    @Singleton
    fun provideProductSearchService(retrofit: Retrofit): ProductSearchService {
        return retrofit.create(ProductSearchService::class.java)
    }

    @Provides
    @Singleton
    fun provideProductRepository(
        productDataSource : ProductSearchDataSource
    ): ProductRepository {
        return ProductRepositoryImpl(productDataSource)
    }
}
