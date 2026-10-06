package com.example.data

import com.example.model.BannerSlide
import com.example.model.Category
import com.example.model.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface FarmeraRepository {
    fun getProducts(): Flow<List<Product>>
    fun getProductById(id: String): Flow<Product?>
    fun getCategories(): Flow<List<Category>>
    fun getHeroSlides(): Flow<List<BannerSlide>>
    fun searchProducts(query: String): Flow<List<Product>>
    fun getFreshFromFarms(): Flow<List<Product>>
    fun getHarvestingThisWeek(): Flow<List<Product>>
    fun getBulkBuys(): Flow<List<Product>>
}

class MockFarmeraRepository : FarmeraRepository {

    private val categories = listOf(
        Category("cat_tubers", "Tubers & Roots", "tubers", "yam", 0xFFFFF1E6),
        Category("cat_fruits", "Fruits", "fruits", "orange", 0xFFFFFBEB),
        Category("cat_vegetables", "Vegetables", "vegetables", "pepper", 0xFFECFDF5),
        Category("cat_grains", "Grains & Beans", "grains", "beans", 0xFFFEF3C7),
        Category("cat_oils", "Oils", "oils", "palmoil", 0xFFFFF7ED),
        Category("cat_pantry", "Pantry Staples", "pantry", "garri", 0xFFFDF4FF),
        Category("cat_bulk", "Bulk Produce", "bulk", "crate", 0xFFF0FDF4)
    )

    private val heroSlides = listOf(
        BannerSlide(
            id = "slide_1",
            headline = "Fresh food. Fewer layers.",
            subtitle = "Shop produce sourced from farms and verified producers across Nigeria.",
            ctaText = "Shop Now",
            promoTag = "DIRECT SOURCING"
        ),
        BannerSlide(
            id = "slide_2",
            headline = "Buy more. Pay better.",
            subtitle = "Bulk pricing for households, restaurants, caterers and food businesses.",
            ctaText = "Explore Bulk",
            promoTag = "WHOLESALE SAVINGS"
        ),
        BannerSlide(
            id = "slide_3",
            headline = "Harvesting this week",
            subtitle = "Discover fresh tubers, peppers and citrus picked within the last 48 hours.",
            ctaText = "See Harvest",
            promoTag = "FRESH HARVEST"
        )
    )

    private val products = listOf(
        Product(
            id = "prod_yam_1",
            slug = "benue-puna-yam",
            name = "Benue Puna Yam",
            category = "Tubers & Roots",
            categorySlug = "tubers",
            sourceLocation = "Gboko, Benue State",
            farmName = "Emmanuel Farms",
            isVerifiedPartner = true,
            unit = "10 tubers",
            price = 18500,
            bulkPrice = 16200,
            rating = 4.9,
            reviewsCount = 312,
            availability = "Available — harvested this week",
            isHarvestingThisWeek = true,
            isBulkAvailable = true,
            shortDescription = "Dry, sweet and floury white puna yam harvested straight from the yam belt in Gboko, Benue State. Perfect for boiling, pounding or roasting.",
            harvestInfo = "Harvested 2 days ago from Emmanuel Farms. Inspected for firm flesh, zero blemish, and optimal dry-matter content.",
            storageGuidance = "Store in a cool, well-ventilated dry place off bare concrete floors. Do not store in plastic bags.",
            badgeText = "Best Seller",
            iconKey = "yam"
        ),
        Product(
            id = "prod_plantain_2",
            slug = "ogun-plantain",
            name = "Ogun Semi-Ripe Plantain",
            category = "Fruits",
            categorySlug = "fruits",
            sourceLocation = "Ijebu-Ode, Ogun State",
            farmName = "Adeleke Plantain Grooves",
            isVerifiedPartner = true,
            unit = "1 bunch (12 fingers)",
            price = 4500,
            bulkPrice = 3800,
            rating = 4.8,
            reviewsCount = 189,
            availability = "Available now",
            isHarvestingThisWeek = true,
            isBulkAvailable = true,
            shortDescription = "Plump, naturally ripened plantains from rainforest soil in Ijebu-Ode. High starch, rich sweetness, perfect for dodo or boiling.",
            harvestInfo = "Cut at optimal maturity stage to ensure even, natural ripening without carbide or chemical accelerators.",
            storageGuidance = "Keep at ambient room temperature. Move to ventilated fruit basket as yellow spots appear.",
            badgeText = "Fresh Harvest",
            iconKey = "plantain"
        ),
        Product(
            id = "prod_tomatoes_3",
            slug = "kano-tomatoes",
            name = "Kano Roma Tomatoes",
            category = "Vegetables",
            categorySlug = "vegetables",
            sourceLocation = "Kura, Kano State",
            farmName = "Danbatta Agro Ventures",
            isVerifiedPartner = true,
            unit = "1 medium basket (approx 8kg)",
            price = 12500,
            bulkPrice = 11000,
            rating = 4.8,
            reviewsCount = 274,
            availability = "Available — chilled transit",
            isHarvestingThisWeek = true,
            isBulkAvailable = true,
            shortDescription = "Firm, deep-red plum tomatoes from irrigated northern fields. Thick flesh and low water content make them rich for stews and pastes.",
            harvestInfo = "Picked early morning in Kura and trucked directly to Lagos hub via temperature-controlled transport.",
            storageGuidance = "Keep stem-side down at room temperature away from direct sunlight for sweetest flavor.",
            badgeText = "Staple Choice",
            iconKey = "tomatoes"
        ),
        Product(
            id = "prod_pepper_4",
            slug = "fresh-habanero-pepper",
            name = "Fresh Red Atarodo Pepper",
            category = "Vegetables",
            categorySlug = "vegetables",
            sourceLocation = "Sagamu, Ogun State",
            farmName = "Oluwaferanmi Farms",
            isVerifiedPartner = true,
            unit = "1 basket (approx 4kg)",
            price = 8500,
            bulkPrice = 7500,
            rating = 4.9,
            reviewsCount = 196,
            availability = "Available now",
            isHarvestingThisWeek = true,
            isBulkAvailable = true,
            shortDescription = "Aromatic, intensely spicy Nigerian scotch bonnet (atarodo) with shiny skin and pungent capsicum heat.",
            harvestInfo = "Hand-picked weekly by verified outgrowers in Ogun state with zero pesticide residue.",
            storageGuidance = "De-stem and store in an airtight container lined with paper towels in the refrigerator.",
            badgeText = "Hot & Fresh",
            iconKey = "pepper"
        ),
        Product(
            id = "prod_onions_5",
            slug = "kano-red-onions",
            name = "Kano Red Violet Onions",
            category = "Vegetables",
            categorySlug = "vegetables",
            sourceLocation = "Wudil, Kano State",
            farmName = "Sahelian Harvest",
            isVerifiedPartner = true,
            unit = "Half bag (approx 25kg)",
            price = 15000,
            bulkPrice = 13500,
            rating = 4.7,
            reviewsCount = 143,
            availability = "Available now",
            isHarvestingThisWeek = false,
            isBulkAvailable = true,
            shortDescription = "Crisp, deeply aromatic northern red onions with tight skins and pungent flavor profile for all Nigerian cooking.",
            harvestInfo = "Naturally cured in traditional shaded cribs for extended kitchen shelf-life.",
            storageGuidance = "Keep in an open mesh bag in a dry, dark, well-aerated pantry cupboard.",
            badgeText = "Bulk Value",
            iconKey = "onions"
        ),
        Product(
            id = "prod_palmoil_6",
            slug = "abia-red-palm-oil",
            name = "Abia Traditional Red Palm Oil",
            category = "Oils",
            categorySlug = "oils",
            sourceLocation = "Umuahia, Abia State",
            farmName = "Ngwa Traditional Oil Mills",
            isVerifiedPartner = true,
            unit = "5L food-grade container",
            price = 11500,
            bulkPrice = 10000,
            rating = 5.0,
            reviewsCount = 420,
            availability = "Available now",
            isHarvestingThisWeek = false,
            isBulkAvailable = true,
            shortDescription = "100% unadulterated, rich virgin red palm oil processed from fresh tenera palm fruit bunches. Pure natural aroma without additives.",
            harvestInfo = "First-press extraction, unbleached, sediment-filtered and packed into food-safe canisters.",
            storageGuidance = "Store at room temperature away from moisture. Closes tightly after use.",
            badgeText = "Pure & Unrefined",
            iconKey = "palmoil"
        ),
        Product(
            id = "prod_yellowgarri_7",
            slug = "edo-yellow-garri",
            name = "Edo Crispy Yellow Garri",
            category = "Pantry Staples",
            categorySlug = "pantry",
            sourceLocation = "Igarra, Edo State",
            farmName = "Afemai Food Processors",
            isVerifiedPartner = true,
            unit = "10kg sealed bag",
            price = 9500,
            bulkPrice = 8200,
            rating = 4.9,
            reviewsCount = 388,
            availability = "Available now",
            isHarvestingThisWeek = false,
            isBulkAvailable = true,
            shortDescription = "Golden fried cassava flakes with palm oil infusion, perfectly sour and crunchy for drinking or making eba.",
            harvestInfo = "Fermented for 4 days before hydraulic pressing and traditional wood-fire pan roasting.",
            storageGuidance = "Store in an airtight drum or sealed container away from damp walls.",
            badgeText = "Crisp & Clean",
            iconKey = "garri"
        ),
        Product(
            id = "prod_whitegarri_8",
            slug = "enugu-white-garri",
            name = "Enugu Nsukka White Garri",
            category = "Pantry Staples",
            categorySlug = "pantry",
            sourceLocation = "Nsukka, Enugu State",
            farmName = "Adada Cooperative Farmers",
            isVerifiedPartner = true,
            unit = "10kg sealed bag",
            price = 8800,
            bulkPrice = 7600,
            rating = 4.8,
            reviewsCount = 210,
            availability = "Available now",
            isHarvestingThisWeek = false,
            isBulkAvailable = true,
            shortDescription = "Fine-grained, clean white cassava grain processed by rural women cooperatives in Nsukka. High swell ratio and smooth texture.",
            harvestInfo = "Made with bio-fortified high-yield cassava roots from verified farm clusters.",
            storageGuidance = "Keep in cool dry cupboard, sealed against humidity.",
            badgeText = "Traditional",
            iconKey = "garri"
        ),
        Product(
            id = "prod_beans_9",
            slug = "niger-brown-beans",
            name = "Niger Honey Oloyin Beans",
            category = "Grains & Beans",
            categorySlug = "grains",
            sourceLocation = "Bida, Niger State",
            farmName = "Niger Valley Farmers",
            isVerifiedPartner = true,
            unit = "10kg canvas bag",
            price = 16500,
            bulkPrice = 14800,
            rating = 4.9,
            reviewsCount = 180,
            availability = "Available now",
            isHarvestingThisWeek = false,
            isBulkAvailable = true,
            shortDescription = "Sweet, clean, weevil-free honey beans (oloyin) that cook quickly into soft, creamy porridge with rich naturally sweet broth.",
            harvestInfo = "Cleaned by mechanical sorters to remove stones and chaff; zero hazardous chemical storage fumigation.",
            storageGuidance = "Keep in an airtight container with dry bay leaves or dried chili pods.",
            badgeText = "Weevil Free",
            iconKey = "beans"
        ),
        Product(
            id = "prod_maize_10",
            slug = "kaduna-maize",
            name = "Kaduna Clean White Maize",
            category = "Grains & Beans",
            categorySlug = "grains",
            sourceLocation = "Zaria, Kaduna State",
            farmName = "Queen Amina Agro",
            isVerifiedPartner = true,
            unit = "25kg bag",
            price = 19000,
            bulkPrice = 17500,
            rating = 4.7,
            reviewsCount = 95,
            availability = "Available now",
            isHarvestingThisWeek = false,
            isBulkAvailable = true,
            shortDescription = "Sun-dried whole corn grain, ideal for milling into ogi (pap), tuwo shinkafa blends, and animal feeds.",
            harvestInfo = "Moisture checked at under 12% to prevent mold and guarantee long storage integrity.",
            storageGuidance = "Elevate bag from ground level in dry, pest-sealed storage room.",
            badgeText = "Bulk Staple",
            iconKey = "maize"
        ),
        Product(
            id = "prod_oranges_11",
            slug = "benue-sweet-oranges",
            name = "Benue Sweet Valencia Oranges",
            category = "Fruits",
            categorySlug = "fruits",
            sourceLocation = "Gboko, Benue State",
            farmName = "Tivland Orchards",
            isVerifiedPartner = true,
            unit = "1 basket (approx 40 pieces)",
            price = 6800,
            bulkPrice = 5900,
            rating = 4.9,
            reviewsCount = 165,
            availability = "Available — harvested this week",
            isHarvestingThisWeek = true,
            isBulkAvailable = true,
            shortDescription = "Ultra-juicy, sun-kissed citrus from Benue orchards. Packed with vitamin C, fragrant oils and refreshing natural sweetness.",
            harvestInfo = "Clipped with care directly from citrus groves to prevent bruising or split rinds.",
            storageGuidance = "Store at room temperature for up to a week or in fruit crisper drawer for longer freshness.",
            badgeText = "Juicy & Fresh",
            iconKey = "orange"
        ),
        Product(
            id = "prod_avocado_12",
            slug = "south-east-avocado",
            name = "Imo Butter Avocado Pear",
            category = "Fruits",
            categorySlug = "fruits",
            sourceLocation = "Owerri, Imo State",
            farmName = "Imo Highland Farms",
            isVerifiedPartner = true,
            unit = "1 basket (10 large pcs)",
            price = 5400,
            bulkPrice = 4600,
            rating = 4.8,
            reviewsCount = 112,
            availability = "Available now",
            isHarvestingThisWeek = true,
            isBulkAvailable = false,
            shortDescription = "Rich, buttery, nutty green pears with small seed cavities and velvety texture. Delicious eaten fresh, with toast, or in salads.",
            harvestInfo = "Selected at mature firmness so they soften smoothly at your kitchen counter.",
            storageGuidance = "Keep firm pears at room temperature; refrigerate once tender to yield.",
            badgeText = "Rich Butter",
            iconKey = "avocado"
        )
    )

    fun getStaticProducts(): List<Product> = products

    override fun getProducts(): Flow<List<Product>> = flow {
        emit(products)
    }

    override fun getProductById(id: String): Flow<Product?> = flow {
        emit(products.find { it.id == id || it.slug == id })
    }

    override fun getCategories(): Flow<List<Category>> = flow {
        emit(categories)
    }

    override fun getHeroSlides(): Flow<List<BannerSlide>> = flow {
        emit(heroSlides)
    }

    override fun searchProducts(query: String): Flow<List<Product>> = flow {
        if (query.isBlank()) {
            emit(products)
        } else {
            val q = query.trim().lowercase()
            val filtered = products.filter {
                it.name.lowercase().contains(q) ||
                it.category.lowercase().contains(q) ||
                it.sourceLocation.lowercase().contains(q) ||
                it.farmName.lowercase().contains(q) ||
                it.shortDescription.lowercase().contains(q)
            }
            emit(filtered)
        }
    }

    override fun getFreshFromFarms(): Flow<List<Product>> = flow {
        emit(products.take(6))
    }

    override fun getHarvestingThisWeek(): Flow<List<Product>> = flow {
        emit(products.filter { it.isHarvestingThisWeek })
    }

    override fun getBulkBuys(): Flow<List<Product>> = flow {
        emit(products.filter { it.isBulkAvailable })
    }
}
