package com.teamapt.monnify.sdk

import com.teamapt.monnify.sdk.data.model.CardType
import org.junit.Assert.assertEquals
import org.junit.Test

class CardTypeTest {

    @Test
    fun testDetection() {
        assertEquals("Is Visa", CardType.VISA, CardType.detect("4000000000000000"))
        assertEquals("Is Visa", CardType.VISA, CardType.detect("4242424242424242"))
        assertEquals("Is Visa", CardType.VISA, CardType.detect("4111111111111111"))
        assertEquals("Is Visa", CardType.VISA, CardType.detect("4960855977233811"))
        assertEquals("Is Visa", CardType.VISA, CardType.detect("4727550101231267"))
        assertEquals("Is Visa", CardType.VISA, CardType.detect("4012888888881881"))
        assertEquals("Is Visa", CardType.VISA, CardType.detect("4386350951957199"))
        assertEquals("Is Visa", CardType.VISA, CardType.detect("4198829356102735"))
        assertEquals("Is Visa", CardType.VISA, CardType.detect("4605349702845677"))
        assertEquals("Is Visa", CardType.VISA, CardType.detect("4935815077908919"))
        assertEquals("Is Visa", CardType.VISA, CardType.detect("4307433016000438"))
        assertEquals("Is Visa", CardType.VISA, CardType.detect("4207780185039578"))
        assertEquals("Is Visa", CardType.VISA, CardType.detect("4672463452443611"))
        assertEquals("Is Visa", CardType.VISA, CardType.detect("4820208198143124"))
        assertEquals("Is Visa", CardType.VISA, CardType.detect("4386350951957199"))

        assertEquals("Is Mastercard", CardType.MASTERCARD, CardType.detect("5105105105105100"))
        assertEquals("Is Mastercard", CardType.MASTERCARD, CardType.detect("5200828282828210"))
        assertEquals("Is Mastercard", CardType.MASTERCARD, CardType.detect("5555555555554444"))
        assertEquals("Is Mastercard", CardType.MASTERCARD, CardType.detect("2345234524354235"))
        assertEquals("Is Mastercard", CardType.MASTERCARD, CardType.detect("5349044227835496"))
        assertEquals("Is Mastercard", CardType.MASTERCARD, CardType.detect("5227375859824647"))
        assertEquals("Is Mastercard", CardType.MASTERCARD, CardType.detect("5444092073746674"))
        assertEquals("Is Mastercard", CardType.MASTERCARD, CardType.detect("5561430419736858"))
        assertEquals("Is Mastercard", CardType.MASTERCARD, CardType.detect("5357329272218151"))
        assertEquals("Is Mastercard", CardType.MASTERCARD, CardType.detect("5227375859824647"))
        assertEquals("Is Mastercard", CardType.MASTERCARD, CardType.detect("5116602804101530"))
        assertEquals("Is Mastercard", CardType.MASTERCARD, CardType.detect("5397151641613966"))

        assertEquals("Is Verve", CardType.VERVE, CardType.detect("5061542039876449"))
        assertEquals("Is Verve", CardType.VERVE, CardType.detect("5061919405914943"))
        assertEquals("Is Verve", CardType.VERVE, CardType.detect("6500167714552619008"))
        assertEquals("Is Verve", CardType.VERVE, CardType.detect("6500047692167366329"))
        assertEquals("Is Verve", CardType.VERVE, CardType.detect("5061064798589832842"))
        assertEquals("Is Verve", CardType.VERVE, CardType.detect("6500146292275608956"))
        assertEquals("Is Verve", CardType.VERVE, CardType.detect("5061499134934558"))
        assertEquals("Is Verve", CardType.VERVE, CardType.detect("5061054358016077098"))
        assertEquals("Is Verve", CardType.VERVE, CardType.detect("6500093850178990011"))
        assertEquals("Is Verve", CardType.VERVE, CardType.detect("5061779349824698"))

        assertEquals("Is Discover", CardType.DISCOVER, CardType.detect("6500028991551683"))
        assertEquals("Is Discover", CardType.DISCOVER, CardType.detect("6500056261770208"))
        assertEquals("Is Discover", CardType.DISCOVER, CardType.detect("6500056261770208"))
        assertEquals("Is Discover", CardType.DISCOVER, CardType.detect("6500187957081910"))
        assertEquals("Is Discover", CardType.DISCOVER, CardType.detect("6500164109288506"))
        assertEquals("Is Discover", CardType.DISCOVER, CardType.detect("6011614120481743"))
        assertEquals("Is Discover", CardType.DISCOVER, CardType.detect("6525305226777089"))

        assertEquals("Is JCB bro", CardType.JCB, CardType.detect("3569124125352418"))
        assertEquals("Is JCB bro", CardType.JCB, CardType.detect("3542806605699977"))
        assertEquals("Is JCB bro", CardType.JCB, CardType.detect("3572978175885090"))
        assertEquals("Is JCB bro", CardType.JCB, CardType.detect("3582837173161828"))
        assertEquals("Is JCB bro", CardType.JCB, CardType.detect("3530111333300000"))

        assertEquals("American Express card", CardType.AMERICAN_EXPRESS, CardType.detect("341705466493500"))
        assertEquals("American Express card", CardType.AMERICAN_EXPRESS, CardType.detect("373279693775178"))
        assertEquals("American Express card", CardType.AMERICAN_EXPRESS, CardType.detect("349258576602006"))
        assertEquals("American Express card", CardType.AMERICAN_EXPRESS, CardType.detect("376945172702602"))

        assertEquals("Diner's club", CardType.DINERS_CLUB, CardType.detect("30995813497753"))
        assertEquals("Diner's club", CardType.DINERS_CLUB, CardType.detect("38301537665123"))
        assertEquals("Diner's club", CardType.DINERS_CLUB, CardType.detect("39896035375064"))
        assertEquals("Diner's club", CardType.DINERS_CLUB, CardType.detect("36811562031204"))

        assertEquals("Unknown card type", CardType.UNKNOWN, CardType.detect("0000000000000000"))
        assertEquals("Unknown card type", CardType.UNKNOWN, CardType.detect("7549263507356104"))
        assertEquals("Unknown card type", CardType.UNKNOWN, CardType.detect("0452734916309264"))
        assertEquals("Unknown card type", CardType.UNKNOWN, CardType.detect("2984614367409153"))
        assertEquals("Unknown card type", CardType.UNKNOWN, CardType.detect("9364051539874921"))
        assertEquals("Unknown card type", CardType.UNKNOWN, CardType.detect("43978"))
        assertEquals("Unknown card type", CardType.UNKNOWN, CardType.detect("234536"))
        assertEquals("Unknown card type", CardType.UNKNOWN, CardType.detect("26252622"))
        assertEquals("Unknown card type", CardType.UNKNOWN, CardType.detect("653228586328278926254325"))
        assertEquals("Unknown card type", CardType.UNKNOWN, CardType.detect("262"))

    }
}
