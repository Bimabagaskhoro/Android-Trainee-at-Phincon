package com.bimabagaskhoro.taskappphincon

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.paging.*
import androidx.recyclerview.widget.ListUpdateCallback
import app.cash.turbine.test
import com.bimabagaskhoro.phincon.core.data.source.remote.network.ApiService
import com.bimabagaskhoro.phincon.core.data.source.remote.response.DataStockItem
import com.bimabagaskhoro.phincon.core.data.source.remote.response.RequestRating
import com.bimabagaskhoro.phincon.core.data.source.remote.response.RequestStock
import com.bimabagaskhoro.phincon.core.data.source.remote.response.ResponseError
import com.bimabagaskhoro.phincon.core.data.source.remote.response.product.DataItemProduct
import com.bimabagaskhoro.phincon.core.data.source.repository.remote.RemoteRepositoryImpl
import com.bimabagaskhoro.phincon.core.utils.Resource
import com.bimabagaskhoro.taskappphincon.utils.getOrAwaitValue
import com.bimabagaskhoro.phincon.core.adapter.paging.ProductAdapter
import com.bimabagaskhoro.taskappphincon.utils.CoroutinesTestRule
import com.bimabagaskhoro.taskappphincon.utils.DataDummy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.HttpException
import retrofit2.Response

@ExperimentalPagingApi
@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner.Silent::class)
class RemoteRepositoryImplTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var coroutinesTestRule = CoroutinesTestRule()

    @Mock
    private lateinit var apiService: ApiService


    private lateinit var remoteRepository: RemoteRepositoryImpl

    @Before
    fun setUp() = runTest {
        remoteRepository = RemoteRepositoryImpl(apiService)
    }

    @Test
    fun `Get Paging Data`(): Unit = runTest {
        val data = ProductPagingSource.snapshot(DataDummy.generateDummyPaging())
        val expectedProduct = MutableLiveData<PagingData<DataItemProduct>>()
        expectedProduct.value = data

        CoroutineScope(Dispatchers.IO).launch {
            val actualData: PagingData<DataItemProduct> =
                remoteRepository.getDataProduct(null).asLiveData().getOrAwaitValue()
            val differ = AsyncPagingDataDiffer(
                diffCallback = ProductAdapter.DIFF_CALLBACK,
                updateCallback = noopListUpdateCallback,
                workerDispatcher = Dispatchers.Main
            )

            differ.submitData(actualData)

//            Assert.assertNotNull(differ.snapshot())
            Assert.assertEquals(DataDummy.generateDummyPaging().size, differ.snapshot().size)
            Assert.assertEquals(DataDummy.generateDummyPaging(), differ.snapshot())
        }
    }

    class ProductPagingSource : PagingSource<Int, LiveData<List<DataItemProduct>>>() {
        companion object {
            fun snapshot(items: List<DataItemProduct>): PagingData<DataItemProduct> {
                return PagingData.from(items)
            }
        }
        override fun getRefreshKey(state: PagingState<Int, LiveData<List<DataItemProduct>>>): Int? {
            return 0
        }

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<DataItemProduct>>> {
            return PagingSource.LoadResult.Page(emptyList(), 0, 1)
        }

    }

    private val noopListUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }

    @Test
    fun `Login User`(): Unit = runTest {
        val dataDummy = DataDummy.generateDummyLogin()
        val emailDummy = "emailDummy@gmail.com"
        val passwordDummy = "passwordDummy"
        val tokenFcmDummy = "tokenFcmDummy"

        Mockito.`when`(apiService.login(emailDummy, passwordDummy, tokenFcmDummy))
            .thenReturn(dataDummy)

        val resultFlow = remoteRepository.login(emailDummy, passwordDummy, tokenFcmDummy)
        resultFlow.test {
            Assert.assertTrue(awaitItem() is Resource.Loading)

            val dataActual = awaitItem() as Resource.Success
            Assert.assertEquals(dataDummy, dataActual.data)
            awaitComplete()
        }
    }

    @Test
    fun `Login User Error`(): Unit = runTest {
        val response = Response.error<ResponseError>(400, "".toResponseBody(null))
        val emailDummy = "emailDummy@gmail.com"
        val passwordDummy = "passwordDummy"
        val tokenFcmDummy = "tokenFcmDummy"

        Mockito.`when`(apiService.login(emailDummy, passwordDummy, tokenFcmDummy))
            .thenThrow(HttpException(response))

        val resultFlow = remoteRepository.login(emailDummy, passwordDummy, tokenFcmDummy)
        resultFlow.test {
            Assert.assertTrue(awaitItem() is Resource.Loading)
            Assert.assertTrue(awaitItem() is Resource.Error)
            awaitComplete()
        }
    }

    @Test
    fun `Login User Error Other 400`(): Unit = runTest {
        val response = Response.error<ResponseError>(500, "".toResponseBody(null))
        val emailDummy = "emailDummy@gmail.com"
        val passwordDummy = "passwordDummy"
        val tokenFcmDummy = "tokenFcmDummy"

        Mockito.`when`(apiService.login(emailDummy, passwordDummy, tokenFcmDummy))
            .thenThrow(HttpException(response))

        val resultFlow = remoteRepository.login(emailDummy, passwordDummy, tokenFcmDummy)
        resultFlow.test {
            Assert.assertTrue(awaitItem() is Resource.Loading)
            Assert.assertTrue(awaitItem() is Resource.Error)
            awaitComplete()
        }
    }

    @Test
    fun `Login User Error Exception`(): Unit = runTest {
        val emailDummy = "emailDummy@gmail.com"
        val passwordDummy = "passwordDummy"
        val tokenFcmDummy = "tokenFcmDummy"

        Mockito.`when`(apiService.login(emailDummy, passwordDummy, tokenFcmDummy))
            .thenThrow(RuntimeException())

        val resultFlow = remoteRepository.login(emailDummy, passwordDummy, tokenFcmDummy)
        resultFlow.test {
            Assert.assertTrue(awaitItem() is Resource.Loading)
            Assert.assertTrue(awaitItem() is Resource.Error)
            awaitComplete()
        }
    }

    @Test
    fun `Register User`(): Unit = runTest {
        val dataDummy = DataDummy.generateDummyRegister()
        val imageDummy = MultipartBody.Part.create("text".toRequestBody())
        val emailDummy = "emailDummy@gmail.com".toRequestBody()
        val passwordDummy = "passwordDummy".toRequestBody()
        val nameDummy = "nameDummy".toRequestBody()
        val phoneDummy = "phoneDummy".toRequestBody()
        val genderDummy = 0

        Mockito.`when`(
            apiService.register(
                imageDummy,
                emailDummy,
                passwordDummy,
                nameDummy,
                phoneDummy,
                genderDummy
            )
        ).thenReturn(dataDummy)

        remoteRepository.register(
            imageDummy,
            emailDummy,
            passwordDummy,
            nameDummy,
            phoneDummy,
            genderDummy
        ).test {
            Assert.assertTrue(awaitItem() is Resource.Loading)
            val data = awaitItem() as Resource.Success
            Assert.assertEquals(dataDummy, data.data)
            awaitComplete()
        }
    }

    @Test
    fun `Register User Error`(): Unit = runTest {
        val response = Response.error<ResponseError>(400, "".toResponseBody(null))

        val imageDummy = MultipartBody.Part.create("text".toRequestBody())
        val emailDummy = "emailDummy@gmail.com".toRequestBody()
        val passwordDummy = "passwordDummy".toRequestBody()
        val nameDummy = "nameDummy".toRequestBody()
        val phoneDummy = "phoneDummy".toRequestBody()
        val genderDummy = 0

        Mockito.`when`(
            apiService.register(
                imageDummy,
                emailDummy,
                passwordDummy,
                nameDummy,
                phoneDummy,
                genderDummy
            )
        ).thenThrow(HttpException(response))

        remoteRepository.register(
            imageDummy,
            emailDummy,
            passwordDummy,
            nameDummy,
            phoneDummy,
            genderDummy
        ).test {
            Assert.assertTrue(awaitItem() is Resource.Loading)
            Assert.assertTrue(awaitItem() is Resource.Error)
            awaitComplete()
        }
    }

    @Test
    fun `Register User Error Other 400`(): Unit = runTest {
        val response = Response.error<ResponseError>(500, "".toResponseBody(null))
        val imageDummy = MultipartBody.Part.create("text".toRequestBody())
        val emailDummy = "emailDummy@gmail.com".toRequestBody()
        val passwordDummy = "passwordDummy".toRequestBody()
        val nameDummy = "nameDummy".toRequestBody()
        val phoneDummy = "phoneDummy".toRequestBody()
        val genderDummy = 0

        Mockito.`when`(
            apiService.register(
                imageDummy,
                emailDummy,
                passwordDummy,
                nameDummy,
                phoneDummy,
                genderDummy
            )
        ).thenThrow(HttpException(response))

        remoteRepository.register(
            imageDummy,
            emailDummy,
            passwordDummy,
            nameDummy,
            phoneDummy,
            genderDummy
        ).test {
            Assert.assertTrue(awaitItem() is Resource.Loading)
            Assert.assertTrue(awaitItem() is Resource.Error)
            awaitComplete()
        }
    }

    @Test
    fun `Register User Error Exception`(): Unit = runTest {
        val imageDummy = MultipartBody.Part.create("text".toRequestBody())
        val emailDummy = "emailDummy@gmail.com".toRequestBody()
        val passwordDummy = "passwordDummy".toRequestBody()
        val nameDummy = "nameDummy".toRequestBody()
        val phoneDummy = "phoneDummy".toRequestBody()
        val genderDummy = 0

        Mockito.`when`(
            apiService.register(
                imageDummy,
                emailDummy,
                passwordDummy,
                nameDummy,
                phoneDummy,
                genderDummy
            )
        ).thenThrow(RuntimeException())

        remoteRepository.register(
            imageDummy,
            emailDummy,
            passwordDummy,
            nameDummy,
            phoneDummy,
            genderDummy
        ).test {
            Assert.assertTrue(awaitItem() is Resource.Loading)
            Assert.assertTrue(awaitItem() is Resource.Error)
            awaitComplete()
        }
    }

    @Test
    fun `Change Password User`(): Unit = runTest {
        val dataDummy = DataDummy.generateDummyChangePassword()

        val idDummy = 0
        val passwordDummy = "passwordDummy"
        val newPasswordDummy = "newPasswordDummy"
        val confirmPasswordDummy = "confirmPasswordDummy"

        Mockito.`when`(
            apiService.changePassword(
                idDummy,
                passwordDummy,
                newPasswordDummy,
                confirmPasswordDummy
            )
        ).thenReturn(dataDummy)

        remoteRepository.changePassword(
            idDummy,
            passwordDummy,
            newPasswordDummy,
            confirmPasswordDummy
        ).test {
            Assert.assertTrue(awaitItem() is Resource.Loading)
            val data = awaitItem() as Resource.Success
            Assert.assertEquals(dataDummy, data.data)
            awaitComplete()
        }
    }

    @Test
    fun `Change Password User Error`(): Unit = runTest {
        val response = Response.error<ResponseError>(400, "".toResponseBody(null))
        val idDummy = 0
        val passwordDummy = "passwordDummy"
        val newPasswordDummy = "newPasswordDummy"
        val confirmPasswordDummy = "confirmPasswordDummy"

        Mockito.`when`(
            apiService.changePassword(
                idDummy,
                passwordDummy,
                newPasswordDummy,
                confirmPasswordDummy
            )
        ).thenThrow(HttpException(response))

        remoteRepository.changePassword(
            idDummy,
            passwordDummy,
            newPasswordDummy,
            confirmPasswordDummy
        ).test {
            Assert.assertTrue(awaitItem() is Resource.Loading)
            Assert.assertTrue(awaitItem() is Resource.Error)
            awaitComplete()
        }
    }

    @Test
    fun `Change Password User Error Other 400`(): Unit = runTest {
        val response = Response.error<ResponseError>(500, "".toResponseBody(null))
        val idDummy = 0
        val passwordDummy = "passwordDummy"
        val newPasswordDummy = "newPasswordDummy"
        val confirmPasswordDummy = "confirmPasswordDummy"

        Mockito.`when`(
            apiService.changePassword(
                idDummy,
                passwordDummy,
                newPasswordDummy,
                confirmPasswordDummy
            )
        ).thenThrow(HttpException(response))

        remoteRepository.changePassword(
            idDummy,
            passwordDummy,
            newPasswordDummy,
            confirmPasswordDummy
        ).test {
            Assert.assertTrue(awaitItem() is Resource.Loading)
            Assert.assertTrue(awaitItem() is Resource.Error)
            awaitComplete()
        }
    }

    @Test
    fun `Change Password User Error Exception`(): Unit = runTest {
        val idDummy = 0
        val passwordDummy = "passwordDummy"
        val newPasswordDummy = "newPasswordDummy"
        val confirmPasswordDummy = "confirmPasswordDummy"

        Mockito.`when`(
            apiService.changePassword(
                idDummy,
                passwordDummy,
                newPasswordDummy,
                confirmPasswordDummy
            )
        ).thenThrow(RuntimeException())

        remoteRepository.changePassword(
            idDummy,
            passwordDummy,
            newPasswordDummy,
            confirmPasswordDummy
        ).test {
            Assert.assertTrue(awaitItem() is Resource.Loading)
            Assert.assertTrue(awaitItem() is Resource.Error)
            awaitComplete()
        }
    }

    @Test
    fun `Change Image User`(): Unit = runTest {
        val dataDummy = DataDummy.generateDummyChangeImage()
        val idDummy = 0
        val imageDummy = MultipartBody.Part.create("text".toRequestBody())
        Mockito.`when`(
            apiService.changeImage(
                idDummy,
                imageDummy
            )
        ).thenReturn(dataDummy)

        remoteRepository.changeImage(
            idDummy,
            imageDummy
        ).test {
            Assert.assertTrue(awaitItem() is Resource.Loading)
//            Assert.assertTrue(awaitItem() is Resource.Success)
            val data = awaitItem() as Resource.Success
            Assert.assertEquals(dataDummy, data.data)
            awaitComplete()
        }
    }

    @Test
    fun `Change Image User Error`(): Unit = runTest {
        val response = Response.error<ResponseError>(400, "".toResponseBody(null))
        val idDummy = 0
        val imageDummy = MultipartBody.Part.create("text".toRequestBody())
        Mockito.`when`(
            apiService.changeImage(
                idDummy,
                imageDummy
            )
        ).thenThrow(HttpException(response))

        remoteRepository.changeImage(
            idDummy,
            imageDummy
        ).test {
            Assert.assertTrue(awaitItem() is Resource.Loading)
            Assert.assertTrue(awaitItem() is Resource.Error)
            awaitComplete()
        }
    }

    @Test
    fun `Change Image User Error Other 400`(): Unit = runTest {
        val response = Response.error<ResponseError>(500, "".toResponseBody(null))
        val idDummy = 0
        val imageDummy = MultipartBody.Part.create("text".toRequestBody())
        Mockito.`when`(
            apiService.changeImage(
                idDummy,
                imageDummy
            )
        ).thenThrow(HttpException(response))

        remoteRepository.changeImage(
            idDummy,
            imageDummy
        ).test {
            Assert.assertTrue(awaitItem() is Resource.Loading)
            Assert.assertTrue(awaitItem() is Resource.Error)
            awaitComplete()
        }
    }

    @Test
    fun `Change Image User Error Exception`(): Unit = runTest {
        val idDummy = 0
        val imageDummy = MultipartBody.Part.create("text".toRequestBody())
        Mockito.`when`(
            apiService.changeImage(
                idDummy,
                imageDummy
            )
        ).thenThrow(RuntimeException())

        remoteRepository.changeImage(
            idDummy,
            imageDummy
        ).test {
            Assert.assertTrue(awaitItem() is Resource.Loading)
            Assert.assertTrue(awaitItem() is Resource.Error)
            awaitComplete()
        }
    }

    @Test
    fun `Add Favorite Product`(): Unit = runTest {
        val dataDummy = DataDummy.generateDummyAddFavorite()
        val userIdDummy = 0
        val idDummy = 0

        Mockito.`when`(
            apiService.addFavorite(
                userIdDummy,
                idDummy
            )
        ).thenReturn(dataDummy)

        remoteRepository.addFavorite(
            userIdDummy,
            idDummy
        ).test {
            Assert.assertTrue(awaitItem() is Resource.Loading)
//            Assert.assertTrue(awaitItem() is Resource.Success)
            val data = awaitItem() as Resource.Success
            Assert.assertEquals(dataDummy, data.data)
            awaitComplete()
        }
    }

    @Test
    fun `Add Favorite Product Error`(): Unit = runTest {
        val response = Response.error<ResponseError>(400, "".toResponseBody(null))
        val userIdDummy = 0
        val idDummy = 0

        Mockito.`when`(
            apiService.addFavorite(
                userIdDummy,
                idDummy
            )
        ).thenThrow(HttpException(response))
        remoteRepository.addFavorite(
            userIdDummy,
            idDummy
        ).test {
            Assert.assertTrue(awaitItem() is Resource.Loading)
            Assert.assertTrue(awaitItem() is Resource.Error)
            awaitComplete()
        }
    }

    @Test
    fun `Add Favorite Product Error Other 400`(): Unit = runTest {
        val response = Response.error<ResponseError>(500, "".toResponseBody(null))
        val userIdDummy = 0
        val idDummy = 0

        Mockito.`when`(
            apiService.addFavorite(
                userIdDummy,
                idDummy
            )
        ).thenThrow(HttpException(response))
        remoteRepository.addFavorite(
            userIdDummy,
            idDummy
        ).test {
            Assert.assertTrue(awaitItem() is Resource.Loading)
            Assert.assertTrue(awaitItem() is Resource.Error)
            awaitComplete()
        }
    }

    @Test
    fun `Add Favorite Product Error Exception`(): Unit = runTest {
        val userIdDummy = 0
        val idDummy = 0

        Mockito.`when`(
            apiService.addFavorite(
                userIdDummy,
                idDummy
            )
        ).thenThrow(RuntimeException())
        remoteRepository.addFavorite(
            userIdDummy,
            idDummy
        ).test {
            Assert.assertTrue(awaitItem() is Resource.Loading)
            Assert.assertTrue(awaitItem() is Resource.Error)
            awaitComplete()
        }
    }

    @Test
    fun `Get Detail Product`(): Unit = runTest {
        val dataDummy = DataDummy.generateDummyGetDetailProduct()
        val userIdDummy = 0
        val idDummy = 0

        Mockito.`when`(
            apiService.getDetail(
                userIdDummy,
                idDummy
            )
        ).thenReturn(dataDummy)

        remoteRepository.getDetailProduct(
            userIdDummy,
            idDummy
        ).test {
            Assert.assertTrue(awaitItem() is Resource.Loading)
            val data = awaitItem() as Resource.Success
            Assert.assertEquals(dataDummy, data.data)
            awaitComplete()
        }
    }

    @Test
    fun `Get Detail Product Error`(): Unit = runTest {
        val response = Response.error<ResponseError>(400, "".toResponseBody(null))
        val userIdDummy = 0
        val idDummy = 0

        Mockito.`when`(
            apiService.getDetail(
                userIdDummy,
                idDummy
            )
        ).thenThrow(HttpException(response))

        remoteRepository.getDetailProduct(
            userIdDummy,
            idDummy
        ).test {
            Assert.assertTrue(awaitItem() is Resource.Loading)
            Assert.assertTrue(awaitItem() is Resource.Error)
            awaitComplete()
        }
    }

    @Test
    fun `Get Detail Product Error Other 400`(): Unit = runTest {
        val response = Response.error<ResponseError>(500, "".toResponseBody(null))
        val userIdDummy = 0
        val idDummy = 0

        Mockito.`when`(
            apiService.getDetail(
                userIdDummy,
                idDummy
            )
        ).thenThrow(HttpException(response))
        remoteRepository.getDetailProduct(
            userIdDummy,
            idDummy
        ).test {
            Assert.assertTrue(awaitItem() is Resource.Loading)
            Assert.assertTrue(awaitItem() is Resource.Error)
            awaitComplete()
        }
    }

    @Test
    fun `Get Detail Product Error Exception`(): Unit = runTest {
        val userIdDummy = 0
        val idDummy = 0

        Mockito.`when`(
            apiService.getDetail(
                userIdDummy,
                idDummy
            )
        ).thenThrow(RuntimeException())
        remoteRepository.getDetailProduct(
            userIdDummy,
            idDummy
        ).test {
            Assert.assertTrue(awaitItem() is Resource.Loading)
            Assert.assertTrue(awaitItem() is Resource.Error)
            awaitComplete()
        }
    }

    @Test
    fun `Update Stock Product`(): Unit = runTest {
        val dataDummy = DataDummy.generateDummyUpdateStock()
        val userIdDummy = "0"
        val dataStock = DataStockItem()
        val data = RequestStock(userIdDummy, listOf(dataStock))

        Mockito.`when`(
            apiService.updateStock(
                data
            )
        ).thenReturn(dataDummy)

        remoteRepository.updateStock(
            data
        ).test {
            Assert.assertTrue(awaitItem() is Resource.Loading)
            val dataActual = awaitItem() as Resource.Success
            Assert.assertEquals(dataDummy, dataActual.data)
            awaitComplete()
        }
    }

    @Test
    fun `Update Stock Product Error`(): Unit = runTest {
        val response = Response.error<ResponseError>(400, "".toResponseBody(null))
        val userIdDummy = "0"
        val dataStock = DataStockItem()
        val data = RequestStock(userIdDummy, listOf(dataStock))
        Mockito.`when`(
            apiService.updateStock(
                data
            )
        ).thenThrow(HttpException(response))

        remoteRepository.updateStock(
            data
        ).test {
            Assert.assertTrue(awaitItem() is Resource.Loading)
            Assert.assertTrue(awaitItem() is Resource.Error)
            awaitComplete()
        }
    }

    @Test
    fun `Update Stock Product Error Other 400`(): Unit = runTest {
        val response = Response.error<ResponseError>(500, "".toResponseBody(null))
        val userIdDummy = "0"
        val dataStock = DataStockItem()
        val data = RequestStock(userIdDummy, listOf(dataStock))
        Mockito.`when`(
            apiService.updateStock(
                data
            )
        ).thenThrow(HttpException(response))

        remoteRepository.updateStock(
            data
        ).test {
            Assert.assertTrue(awaitItem() is Resource.Loading)
            Assert.assertTrue(awaitItem() is Resource.Error)
            awaitComplete()
        }
    }

    @Test
    fun `Update Stock Product Error Exception`(): Unit = runTest {
        val userIdDummy = "0"
        val dataStock = DataStockItem()
        val data = RequestStock(userIdDummy, listOf(dataStock))
        Mockito.`when`(
            apiService.updateStock(
                data
            )
        ).thenThrow(RuntimeException())

        remoteRepository.updateStock(
            data
        ).test {
            Assert.assertTrue(awaitItem() is Resource.Loading)
            Assert.assertTrue(awaitItem() is Resource.Error)
            awaitComplete()
        }
    }

    @Test
    fun `Un Favorite Product`(): Unit = runTest {
        val dataDummy = DataDummy.generateDummyUnFavorite()
        val userIdDummy = 0
        val idDummy = 0

        Mockito.`when`(
            apiService.unFavorite(
                userIdDummy,
                idDummy
            )
        ).thenReturn(dataDummy)
        remoteRepository.unFavorite(
            userIdDummy,
            idDummy
        ).test {
            Assert.assertTrue(awaitItem() is Resource.Loading)

            val dataActual = awaitItem() as Resource.Success
            Assert.assertEquals(dataDummy, dataActual.data)
            awaitComplete()
        }
    }

    @Test
    fun `Un Favorite Product Error`(): Unit = runTest {
        val response = Response.error<ResponseError>(400, "".toResponseBody(null))
        val userIdDummy = 0
        val idDummy = 0

        Mockito.`when`(
            apiService.unFavorite(
                userIdDummy,
                idDummy
            )
        ).thenThrow(HttpException(response))
        remoteRepository.unFavorite(
            userIdDummy,
            idDummy
        ).test {
            Assert.assertTrue(awaitItem() is Resource.Loading)
            Assert.assertTrue(awaitItem() is Resource.Error)
            awaitComplete()
        }
    }

    @Test
    fun `Un Favorite Product Error Other 400`(): Unit = runTest {
        val response = Response.error<ResponseError>(500, "".toResponseBody(null))
        val userIdDummy = 0
        val idDummy = 0

        Mockito.`when`(
            apiService.unFavorite(
                userIdDummy,
                idDummy
            )
        ).thenThrow(HttpException(response))
        remoteRepository.unFavorite(
            userIdDummy,
            idDummy
        ).test {
            Assert.assertTrue(awaitItem() is Resource.Loading)
            Assert.assertTrue(awaitItem() is Resource.Error)
            awaitComplete()
        }
    }

    @Test
    fun `Un Favorite Product Error Exception`(): Unit = runTest {
        val userIdDummy = 0
        val idDummy = 0

        Mockito.`when`(
            apiService.unFavorite(
                userIdDummy,
                idDummy
            )
        ).thenThrow(RuntimeException())
        remoteRepository.unFavorite(
            userIdDummy,
            idDummy
        ).test {
            Assert.assertTrue(awaitItem() is Resource.Loading)
            Assert.assertTrue(awaitItem() is Resource.Error)
            awaitComplete()
        }
    }

    @Test
    fun `Update Rate Product`(): Unit = runTest {
        val dataDummy = DataDummy.generateDummyUpdateRate()
        val userIdDummy = 0
        val rate = RequestRating()

        Mockito.`when`(
            apiService.updateRating(
                userIdDummy,
                rate
            )
        ).thenReturn(dataDummy)

        remoteRepository.updateRate(
            userIdDummy,
            rate
        ).test {
            Assert.assertTrue(awaitItem() is Resource.Loading)
            val dataActual = awaitItem() as Resource.Success
            Assert.assertEquals(dataDummy, dataActual.data)
            awaitComplete()
        }
    }

    @Test
    fun `Update Rate Product Error`(): Unit = runTest {
        val response = Response.error<ResponseError>(400, "".toResponseBody(null))
        val userIdDummy = 0
        val rate = RequestRating()

        Mockito.`when`(
            apiService.updateRating(
                userIdDummy,
                rate
            )
        ).thenThrow(HttpException(response))

        remoteRepository.updateRate(
            userIdDummy,
            rate
        ).test {
            Assert.assertTrue(awaitItem() is Resource.Loading)
            Assert.assertTrue(awaitItem() is Resource.Error)
            awaitComplete()
        }
    }

    @Test
    fun `Update Rate Product Error Other 400`(): Unit = runTest {
        val response = Response.error<ResponseError>(500, "".toResponseBody(null))
        val userIdDummy = 0
        val rate = RequestRating()

        Mockito.`when`(
            apiService.updateRating(
                userIdDummy,
                rate
            )
        ).thenThrow(HttpException(response))

        remoteRepository.updateRate(
            userIdDummy,
            rate
        ).test {
            Assert.assertTrue(awaitItem() is Resource.Loading)
            Assert.assertTrue(awaitItem() is Resource.Error)
            awaitComplete()
        }
    }

    @Test
    fun `Update Rate Product Error Exception`(): Unit = runTest {
        val userIdDummy = 0
        val rate = RequestRating()

        Mockito.`when`(
            apiService.updateRating(
                userIdDummy,
                rate
            )
        ).thenThrow(RuntimeException())

        remoteRepository.updateRate(
            userIdDummy,
            rate
        ).test {
            Assert.assertTrue(awaitItem() is Resource.Loading)
            Assert.assertTrue(awaitItem() is Resource.Error)
            awaitComplete()
        }
    }

    @Test
    fun `Get Data Favorite Product`(): Unit = runTest {
        val dataDummy = DataDummy.generateDummyDataFavorite()
        val userIdDummy = 0
        val searchDummy = null

        Mockito.`when`(
            apiService.getListFav(
                userIdDummy,
                searchDummy
            )
        ).thenReturn(dataDummy)
        remoteRepository.getDataFavProduct(
            userIdDummy,
            searchDummy
        ).test {
            Assert.assertTrue(awaitItem() is Resource.Loading)
            val dataActual = awaitItem() as Resource.Success
            Assert.assertEquals(dataDummy, dataActual.data)
            awaitComplete()
        }
    }

    @Test
    fun `Get Data Favorite Product Error`(): Unit = runTest {
        val response = Response.error<ResponseError>(400, "".toResponseBody(null))
        val userIdDummy = 0
        val searchDummy = "searchDummy"

        Mockito.`when`(
            apiService.getListFav(
                userIdDummy,
                searchDummy
            )
        ).thenThrow(HttpException(response))
        remoteRepository.getDataFavProduct(
            userIdDummy,
            searchDummy
        ).test {
            Assert.assertTrue(awaitItem() is Resource.Loading)
            Assert.assertTrue(awaitItem() is Resource.Error)
            awaitComplete()
        }
    }

    @Test
    fun `Get Data Favorite Product Error Other 400`(): Unit = runTest {
        val response = Response.error<ResponseError>(500, "".toResponseBody(null))
        val userIdDummy = 0
        val searchDummy = "searchDummy"

        Mockito.`when`(
            apiService.getListFav(
                userIdDummy,
                searchDummy
            )
        ).thenThrow(HttpException(response))
        remoteRepository.getDataFavProduct(
            userIdDummy,
            searchDummy
        ).test {
            Assert.assertTrue(awaitItem() is Resource.Loading)
            Assert.assertTrue(awaitItem() is Resource.Error)
            awaitComplete()
        }
    }

    @Test
    fun `Get Data Favorite Product Error Exception`(): Unit = runTest {
        val userIdDummy = 0
        val searchDummy = "searchDummy"

        Mockito.`when`(
            apiService.getListFav(
                userIdDummy,
                searchDummy
            )
        ).thenThrow(RuntimeException())
        remoteRepository.getDataFavProduct(
            userIdDummy,
            searchDummy
        ).test {
            Assert.assertTrue(awaitItem() is Resource.Loading)
            Assert.assertTrue(awaitItem() is Resource.Error)
            awaitComplete()
        }
    }

    @Test
    fun `Get Data Other Product`(): Unit = runTest {
        val dataDummy = DataDummy.generateDummyOtherProduct()
        val userIdDummy = 0
        Mockito.`when`(
            apiService.getOtherProduct(
                userIdDummy
            )
        ).thenReturn(dataDummy)

        remoteRepository.getOtherProduct(
            userIdDummy
        ).test {
            Assert.assertTrue(awaitItem() is Resource.Loading)
            val dataActual = awaitItem() as Resource.Success
            Assert.assertEquals(dataDummy, dataActual.data)
            awaitComplete()
        }
    }

    @Test
    fun `Get Data Other Product Error`(): Unit = runTest {
        val response = Response.error<ResponseError>(400, "".toResponseBody(null))
        val userIdDummy = 0
        Mockito.`when`(
            apiService.getOtherProduct(
                userIdDummy
            )
        ).thenThrow(HttpException(response))
        remoteRepository.getOtherProduct(
            userIdDummy
        ).test {
            Assert.assertTrue(awaitItem() is Resource.Loading)
            Assert.assertTrue(awaitItem() is Resource.Error)
            awaitComplete()
        }
    }

    @Test
    fun `Get Data Other Product Error Other 400`(): Unit = runTest {
        val response = Response.error<ResponseError>(500, "".toResponseBody(null))
        val userIdDummy = 0
        Mockito.`when`(
            apiService.getOtherProduct(
                userIdDummy
            )
        ).thenThrow(HttpException(response))
        remoteRepository.getOtherProduct(
            userIdDummy
        ).test {
            Assert.assertTrue(awaitItem() is Resource.Loading)
            Assert.assertTrue(awaitItem() is Resource.Error)
            awaitComplete()
        }
    }

    @Test
    fun `Get Data Other Product Error Exception`(): Unit = runTest {
        val userIdDummy = 0
        Mockito.`when`(
            apiService.getOtherProduct(
                userIdDummy
            )
        ).thenThrow(RuntimeException())
        remoteRepository.getOtherProduct(
            userIdDummy
        ).test {
            Assert.assertTrue(awaitItem() is Resource.Loading)
            Assert.assertTrue(awaitItem() is Resource.Error)
            awaitComplete()
        }
    }

    @Test
    fun `Get Data History Product`(): Unit = runTest {
        val dataDummy = DataDummy.generateDummyHistoryProduct()
        val userIdDummy = 0
        Mockito.`when`(
            apiService.getHistoryProduct(
                userIdDummy
            )
        ).thenReturn(dataDummy)
        remoteRepository.getHistoryProduct(
            userIdDummy
        ).test {
            Assert.assertTrue(awaitItem() is Resource.Loading)
            val dataActual = awaitItem() as Resource.Success
            Assert.assertEquals(dataDummy, dataActual.data)
            awaitComplete()
        }
    }

    @Test
    fun `Get Data History Product Error`(): Unit = runTest {
        val response = Response.error<ResponseError>(400, "".toResponseBody(null))
        val userIdDummy = 0
        Mockito.`when`(
            apiService.getHistoryProduct(
                userIdDummy
            )
        ).thenThrow(HttpException(response))
        remoteRepository.getHistoryProduct(
            userIdDummy
        ).test {
            Assert.assertTrue(awaitItem() is Resource.Loading)
            Assert.assertTrue(awaitItem() is Resource.Error)
            awaitComplete()
        }
    }

    @Test
    fun `Get Data History Product Error Other 400`(): Unit = runTest {
        val response = Response.error<ResponseError>(500, "".toResponseBody(null))
        val userIdDummy = 0
        Mockito.`when`(
            apiService.getHistoryProduct(
                userIdDummy
            )
        ).thenThrow(HttpException(response))
        remoteRepository.getHistoryProduct(
            userIdDummy
        ).test {
            Assert.assertTrue(awaitItem() is Resource.Loading)
            Assert.assertTrue(awaitItem() is Resource.Error)
            awaitComplete()
        }
    }

    @Test
    fun `Get Data History Product Error Exception`(): Unit = runTest {
        val userIdDummy = 0
        Mockito.`when`(
            apiService.getHistoryProduct(
                userIdDummy
            )
        ).thenThrow(RuntimeException())
        remoteRepository.getHistoryProduct(
            userIdDummy
        ).test {
            Assert.assertTrue(awaitItem() is Resource.Loading)
            Assert.assertTrue(awaitItem() is Resource.Error)
            awaitComplete()
        }
    }

}