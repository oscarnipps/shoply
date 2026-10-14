package com.app.shoply.data.remote

import com.app.shoply.data.remote.api.ProductSearchService
import com.app.shoply.data.remote.interceptor.NoInternetException
import com.app.shoply.data.remote.mapper.ProductMapper
import com.app.shoply.domain.common.AppError
import com.app.shoply.domain.common.AppResult
import com.app.shoply.domain.ProductPage
import retrofit2.HttpException
import javax.inject.Inject

class ProductSearchDataSourceImpl @Inject constructor(
    private val service: ProductSearchService,
    private val mapper: ProductMapper
) : ProductSearchDataSource {

    override suspend fun getProducts(query: String, page: Int): AppResult<ProductPage> {
        return try {
            val response = service.searchProducts(query = query, page = page)

            AppResult.Success(mapper.map(response))

        } catch (e: NoInternetException) {
            AppResult.Error(AppError.NO_INTERNET, e.message)
        } catch (e: HttpException) {
            AppResult.Error(AppError.TECHNICAL, "HTTP ${e.code()}: ${e.message()}")
        } catch (e: Exception) {
            AppResult.Error(AppError.TECHNICAL, e.message)
        }
    }
}