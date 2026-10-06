package com.example

import com.example.data.MockFarmeraRepository
import com.example.model.CartItem
import com.example.model.Product
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun repository_loadsNigerianProduce() = runBlocking {
        val repo = MockFarmeraRepository()
        val products = repo.getProducts().first()
        assertTrue("Products should not be empty", products.isNotEmpty())
        assertTrue("Contains Benue Puna Yam", products.any { it.name.contains("Yam") })
        assertTrue("Contains Palm Oil", products.any { it.name.contains("Palm Oil") })
        assertTrue("Contains Garri", products.any { it.name.contains("Garri") })

        val yam = repo.getProductById("prod_yam_1").first()
        assertNotNull(yam)
        assertEquals(18500, yam?.price)
        assertEquals("₦18,500", yam?.formattedPrice)
    }

    @Test
    fun cartCalculation_isAccurate() {
        val testProduct = Product(
            id = "test_yam",
            slug = "test-yam",
            name = "Test Yam",
            category = "Tubers & Roots",
            categorySlug = "tubers",
            sourceLocation = "Gboko, Benue State",
            farmName = "Emmanuel Farms",
            unit = "10 tubers",
            price = 18500,
            bulkPrice = 16000,
            shortDescription = "Test description",
            harvestInfo = "Harvested 2 days ago",
            storageGuidance = "Cool dry place"
        )

        val item1 = CartItem(product = testProduct, quantity = 2, isBulk = false)
        assertEquals(37000, item1.lineTotal)

        val itemBulk = CartItem(product = testProduct, quantity = 2, isBulk = true)
        assertEquals(32000, itemBulk.lineTotal)
    }
}
