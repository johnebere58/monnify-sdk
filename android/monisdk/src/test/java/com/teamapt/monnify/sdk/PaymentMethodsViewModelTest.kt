package com.teamapt.monnify.sdk

import android.os.Bundle
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.teamapt.monnify.sdk.data.model.TransactionDetails
import com.teamapt.monnify.sdk.module.CommonUIFunctions
import com.teamapt.monnify.sdk.module.IAppNavigator
import com.teamapt.monnify.sdk.module.LocalMerchantKeyProvider
import com.teamapt.monnify.sdk.module.vm.PaymentMethodsViewModel
import com.teamapt.monnify.sdk.service.ApplicationMode
import com.teamapt.monnify.sdk.testutil.TestUtils.getRandomAlphaNumericStringOfLength
import com.teamapt.monnify.sdk.testutil.TestUtils.getRandomAmount
import com.teamapt.monnify.sdk.testutil.TestUtils.getRandomName
import com.teamapt.monnify.sdk.util.Constants
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.math.BigDecimal

/**
 * This class contains tests for [PaymentMethodsViewModel]
 */
@RunWith(AndroidJUnit4::class)
class PaymentMethodsViewModelTest {

    private var paymentMethodsViewModel: PaymentMethodsViewModel? = null

    @Mock
    private val localMerchantKeyProvider: LocalMerchantKeyProvider? = null
    @Mock
    private val navigator: IAppNavigator? = null
    @Mock
    private val commonUIFunctions: CommonUIFunctions? = null

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        Monnify.instance.setApplicationMode(ApplicationMode.TEST)

        paymentMethodsViewModel = PaymentMethodsViewModel()
        val transactionDetails = TransactionDetails.Builder()
                .amount(BigDecimal(getRandomAmount(50000, 2)))
                .currencyCode("NGN")
                .customerName(getRandomName())
                .customerEmail("cnwagu@teamapt.com")
                .paymentReference(getRandomAlphaNumericStringOfLength(7))
                .paymentDescription("Random payment from mobile sdk in development")
                .build()
        val bundle = Bundle()
        bundle.putParcelable(Constants.ARG_TRANSACTION_DETAILS, transactionDetails)

        paymentMethodsViewModel!!.init(bundle, localMerchantKeyProvider!!, navigator!!, commonUIFunctions!!)

        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }

    }

    @Test
    fun testAddPaymentMethodsToListMethod() {
        // paymentMethodsViewModel!!.addPaymentMethodsToList()
    }

}
