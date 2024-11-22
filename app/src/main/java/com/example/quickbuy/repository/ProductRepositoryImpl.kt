package com.example.quickbuy.repository

import android.util.Log
import com.example.quickbuy.model.CartItem
import com.example.quickbuy.model.ProductsItem
import com.example.quickbuy.model.UserProfile
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(private val firestoreDb: FirebaseFirestore) :
    ProductRepository {

    override suspend fun addUser(userProfile: UserProfile, userId: String) {
        safeFireStoreCall("Failed to add user") {
            firestoreDb.collection(USERS_PATH).document(userId).set(userProfile).await()
        }
    }

    override suspend fun getUserData(userId: String): UserProfile {
        return safeFireStoreCall("Failed to get user data") {
            val snapshot = firestoreDb.collection(USERS_PATH).document(userId).get().await()
            snapshot.toObject(UserProfile::class.java) ?: UserProfile()
        }
    }

    override suspend fun getCategories(): List<String> {
        return emptyList()
    }

    override suspend fun getAllProduct(): List<ProductsItem> {
        return safeFireStoreCall("Failed to get all product") {
            val snapshot = firestoreDb.collection(PRODUCT_PATH).get().await()
            snapshot.documents.mapNotNull { it ->
                it.toObject(ProductsItem::class.java)
            }
        }
    }

    override suspend fun getProductById(productId: String): ProductsItem {
        return safeFireStoreCall(errorMessage = "Failed to fetch product by ID: $productId") {
            val snapshot = firestoreDb.collection(PRODUCT_PATH)
                .document(productId)
                .get()
                .await()

            if (snapshot.exists()) {
                snapshot.toObject(ProductsItem::class.java)?.also {
                    Log.d("FirebaseError", "Fetched product: $it")
                } ?: ProductsItem().also {
                    Log.d("FirebaseError", "Failed to deserialize product with ID: $productId")
                }
            } else {
                Log.d("FirebaseError", "No document found with ID: $productId")
                ProductsItem()
            }
        }
    }


    override suspend fun getAllLikedProduct(userId: String): List<ProductsItem> {
        return safeFireStoreCall("failed to get all liked product") {
            val snapshot =
                firestoreDb.collection(USERS_PATH).document(userId).collection(LIKED_PATH).get()
                    .await()
            snapshot.documents.mapNotNull {
                getProductById(it.id)
            }
        }
    }

    override suspend fun addLikedProduct(userId: String, productId: String) {
        safeFireStoreCall("Failed to add product to liked") {
            val likedItem = hashMapOf(
                "productId" to productId
            )
            firestoreDb.collection(USERS_PATH).document(userId).collection(LIKED_PATH)
                .document(productId).set(likedItem).await()
        }
    }

    override suspend fun removeLikedProduct(userId: String, productId: String) {
        safeFireStoreCall("Failed to remove liked product") {
            firestoreDb.collection(USERS_PATH).document(userId).collection(LIKED_PATH)
                .document(productId).delete().await()
        }
    }

    override suspend fun isLiked(userId: String, productId: String): Boolean {
        if (productId.isBlank()) return false

        return safeFireStoreCall("Failed to fetch isLiked") {
            firestoreDb.collection(USERS_PATH)
                .document(userId)
                .collection(LIKED_PATH)
                .document(productId)
                .get()
                .await()
                .exists()
        } ?: false
    }

    override suspend fun getAllCartProduct(userId: String): List<ProductsItem> {
        return safeFireStoreCall("Failed to get all cart product") {
            val snapshot =
                firestoreDb.collection(USERS_PATH).document(userId).collection(CART_PATH).get()
                    .await()
            snapshot.documents.mapNotNull {
                getProductById(it.id)
            }
        }
    }

    override suspend fun isProductInCart(userId: String, productId: String): Boolean {
        if (productId.isBlank()) return false

        return safeFireStoreCall("Failed to fetch isProductInCart") {
            firestoreDb.collection(USERS_PATH)
                .document(userId)
                .collection(CART_PATH)
                .document(productId)
                .get()
                .await()
                .exists()
        } ?: false
    }

    override suspend fun addToCart(userId: String, productId: String) {
        safeFireStoreCall(errorMessage = "Failed to add product to cart") {
            val cartItem = CartItem(productId = productId)
            firestoreDb.collection(USERS_PATH).document(userId).collection(CART_PATH)
                .document(productId).set(cartItem).await()
        }
    }

    override suspend fun removeFromCart(userId: String, productId: String) {
        safeFireStoreCall(errorMessage = "Failed to remove product from cart") {
            firestoreDb.collection(USERS_PATH).document(userId).collection(CART_PATH)
                .document(productId).delete().await()
        }
    }

    override suspend fun getCartProductQuantity(userId: String): List<CartItem> {
        return safeFireStoreCall("Failed to fetch cart product quantit") {
            val snapshot =
                firestoreDb.collection(USERS_PATH).document(userId).collection(CART_PATH).get()
                    .await()
            snapshot.documents.mapNotNull {
                it.toObject(CartItem::class.java)
            }
        }
    }

    override suspend fun incrementProductQuantity(userId: String, updatedCartItem: CartItem) {
        return safeFireStoreCall("Failed to increment product quantity") {
            firestoreDb.collection(USERS_PATH).document(userId).collection(CART_PATH)
                .document(updatedCartItem.productId).set(updatedCartItem).await()
        }
    }

    override suspend fun decrementProductQuantity(userId: String, updatedCartItem: CartItem) {
        return safeFireStoreCall("Failed to decrement product quantity") {
            firestoreDb.collection(USERS_PATH).document(userId).collection(CART_PATH)
                .document(updatedCartItem.productId).set(updatedCartItem).await()
        }
    }
}

suspend fun <T> safeFireStoreCall(
    errorMessage: String = "",
    action: suspend () -> T,
): T {
    return try {
        action()
    } catch (e: Exception) {
        Log.d("FirebaseError", "$errorMessage: ${e.message}")
        throw e
    }
}
