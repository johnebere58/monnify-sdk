package com.teamapt.monnify.sdk

import android.app.Activity
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.teamapt.monnify.sdk.data.model.TransactionDetails
import com.teamapt.monnify.sdk.testutil.TestUtils.getRandomAlphaNumericStringOfLength
import com.teamapt.monnify.sdk.testutil.TestUtils.getRandomAmount
import com.teamapt.monnify.sdk.testutil.TestUtils.getRandomName
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.math.BigDecimal

/**
 * This class contains tests for [Monnify]
 */
@RunWith(AndroidJUnit4::class)
class SdkIntegrationProcessTest {

    private lateinit var monnify: Monnify

    @Mock
    private val merchantMainActivity: Activity? = null

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
    }

    @Test
    fun `test integration process`() {
        monnify = Monnify.instance
        setupMonnifyPaymentGateway()
        doOpenSDK()
    }

    private fun setupMonnifyPaymentGateway() {
        monnify.setApiKey("MK_TEST_32MACUZP67")
        monnify.setContractCode("3558384994")
    }

    private fun doOpenSDK() {
        val transactionDetails = TransactionDetails.Builder()
                .amount(BigDecimal(getRandomAmount(100000000, 2)))
                .currencyCode("NGN")
                .customerName(getRandomName())
                .customerEmail("cnwagu@teamapt.com")
                .paymentReference(getRandomAlphaNumericStringOfLength(7))
                .paymentDescription("Random payment from mobile sdk in development")
                .build()

        monnify.initializePayment(merchantMainActivity!!, transactionDetails, 3, "RANDOM_KEY")
    }

}
