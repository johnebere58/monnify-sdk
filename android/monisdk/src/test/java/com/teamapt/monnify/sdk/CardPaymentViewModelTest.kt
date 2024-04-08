package com.teamapt.monnify.sdk

import android.os.Bundle
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.teamapt.monnify.sdk.module.CommonUIFunctions
import com.teamapt.monnify.sdk.module.IAppNavigator
import com.teamapt.monnify.sdk.module.LocalMerchantKeyProvider
import com.teamapt.monnify.sdk.module.vm.CardPaymentViewModel
import com.teamapt.monnify.sdk.service.ApplicationMode
import com.teamapt.monnify.sdk.testutil.TestUtils
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations

/**
 * This class contains tests for [CardPaymentViewModel]
 */
@RunWith(AndroidJUnit4::class)
class CardPaymentViewModelTest {

    private var cardPaymentViewModel: CardPaymentViewModel? = null

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

        cardPaymentViewModel = CardPaymentViewModel()
        cardPaymentViewModel!!.init(Bundle(), localMerchantKeyProvider!!, navigator!!, commonUIFunctions!!)
        cardPaymentViewModel!!.transactionReference = TestUtils.getRandomAlphaNumericStringOfLength(7)

        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
    }

    @Test
    fun `card should be validated when all details are valid, invalid when details are invalid`() {
        enterValidCardDetails()
        assertTrue(cardPaymentViewModel!!.liveCardValidation.value!!)

        enterInvalidCardDetails()
        assertFalse(cardPaymentViewModel!!.liveCardValidation.value!!)

        useInvalidDate1()
        assertFalse(cardPaymentViewModel!!.liveCardValidation.value!!)

        useInvalidDate2()
        assertFalse(cardPaymentViewModel!!.liveCardValidation.value!!)

        useInvalidDate3()
        assertFalse(cardPaymentViewModel!!.liveCardValidation.value!!)
    }

    @Test
    fun `user should be able to pay when all details are valid, unable to pay when details are incomplete`() {
        enterValidCardDetails()
        cardPaymentViewModel!!.verifyCardAndInitializePayOnSuccess()
        assertTrue(cardPaymentViewModel!!.liveCardValidation.value!!)

        enterPartialCardDetails()
        cardPaymentViewModel!!.verifyCardAndInitializePayOnSuccess()
        assertFalse(cardPaymentViewModel!!.liveCardValidation.value!!)
    }

    @Test
    fun `otp should be validated when up to six characters, else invalid`() {
        enterValidOtp()
        assertTrue(cardPaymentViewModel!!.liveOtpValidation.value!!)

        enterInvalidOtp()
        assertFalse(cardPaymentViewModel!!.liveOtpValidation.value!!)
    }

    private fun enterValidCardDetails() {
        cardPaymentViewModel!!.setCardNumberAndVerify("000000000")
        cardPaymentViewModel!!.setCardExpiryAndVerify("12/21")
        cardPaymentViewModel!!.setCardCvvAndVerify(TestUtils.getRandomNumericStringOfLength(3))
        cardPaymentViewModel!!.setCardPinAndVerify(TestUtils.getRandomNumericStringOfLength(4))
    }

    private fun enterInvalidCardDetails() {
        cardPaymentViewModel!!.setCardNumberAndVerify("726363332")
        cardPaymentViewModel!!.setCardExpiryAndVerify("12/21")
        cardPaymentViewModel!!.setCardCvvAndVerify(TestUtils.getRandomNumericStringOfLength(3))
        cardPaymentViewModel!!.setCardPinAndVerify(TestUtils.getRandomNumericStringOfLength(4))
    }

    private fun enterPartialCardDetails() {
        cardPaymentViewModel!!.setCardNumberAndVerify(null)
        cardPaymentViewModel!!.setCardCvvAndVerify(TestUtils.getRandomNumericStringOfLength(3))
        cardPaymentViewModel!!.setCardPinAndVerify(null)
    }

    private fun useInvalidDate1() {
        cardPaymentViewModel!!.setCardNumberAndVerify("000000000")
        cardPaymentViewModel!!.setCardExpiryAndVerify(null)
        cardPaymentViewModel!!.setCardCvvAndVerify(TestUtils.getRandomNumericStringOfLength(3))
        cardPaymentViewModel!!.setCardPinAndVerify(TestUtils.getRandomNumericStringOfLength(4))
    }

    private fun useInvalidDate2() {
        cardPaymentViewModel!!.setCardNumberAndVerify("000000000")
        cardPaymentViewModel!!.setCardExpiryAndVerify("34/09")
        cardPaymentViewModel!!.setCardCvvAndVerify(TestUtils.getRandomNumericStringOfLength(3))
        cardPaymentViewModel!!.setCardPinAndVerify(TestUtils.getRandomNumericStringOfLength(4))
    }

    private fun useInvalidDate3() {
        cardPaymentViewModel!!.setCardNumberAndVerify("000000000")
        cardPaymentViewModel!!.setCardExpiryAndVerify("23")
        cardPaymentViewModel!!.setCardCvvAndVerify(TestUtils.getRandomNumericStringOfLength(3))
        cardPaymentViewModel!!.setCardPinAndVerify(TestUtils.getRandomNumericStringOfLength(4))
    }

    private fun enterValidOtp() {
        cardPaymentViewModel!!.setOtpAndVerify(TestUtils.getRandomNumericStringOfLength(6))
    }

    private fun enterInvalidOtp() {
        cardPaymentViewModel!!.setOtpAndVerify(TestUtils.getRandomNumericStringOfLength(4))
    }
}
