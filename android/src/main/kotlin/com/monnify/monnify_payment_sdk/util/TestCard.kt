package com.monnify.monnify_payment_sdk.util

data class TestCard(
    val title: String,
    var number: String? = null,
    var expiryMonth: Int? = null,
    var expiryYear: Int? = null,
    var expiryDate: String? = null,
    var cvv: String? = null,
    var pin: String? = null
) {


    override fun toString(): String {
        return title
    }

    companion object {

        fun testCards(): List<TestCard> {

            val testCards = ArrayList<TestCard>()

            val testCard1 = TestCard(
                title = "Successful Transaction (**1111)", number = "4111111111111111",
                expiryMonth = 12, expiryYear = 2022, expiryDate = "12/22", cvv = "123"
            )

            val testCard2 = TestCard(
                title = "Declined Transaction (**0007)", number = "5200000000000007",
                expiryMonth = 12, expiryYear = 2022, expiryDate = "12/22", cvv = "123"
            )

            val testCard3 = TestCard(
                title = "OTP Auth. Transaction (**3712)", number = "4145881303163712",
                expiryMonth = 9, expiryYear = 2022, expiryDate = "09/22", cvv = "123"
            )

            val testCard4 = TestCard(
                title = "3DSecure Auth. Transaction (**0002)", number = "4000000000000002",
                expiryMonth = 12, expiryYear = 2022, expiryDate = "12/22", cvv = "123"
            )

            testCards.add(testCard1)
            testCards.add(testCard2)
            testCards.add(testCard3)
            testCards.add(testCard4)

            return testCards
        }

        fun testCardDropdownHint(): TestCard {
            return TestCard(title = "Choose a card")
        }
    }

}