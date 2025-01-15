package org.kss.example.jooq.util

sealed interface InfiniteScroll<K : Comparable<K>> {
	val size: Int
	val lastId: K?
	fun getOrder(): Order

	enum class Order {
		NEWEST_FIRST,
		OLDEST_FIRST,
	}
}

sealed class InfiniteScrollDesc<K : Comparable<K>> : InfiniteScroll<K> {
	override fun getOrder() = InfiniteScroll.Order.NEWEST_FIRST

	data class UUID(override val size: Int = 20, override val lastId: java.util.UUID? = null) :
		InfiniteScrollDesc<java.util.UUID>()

	data class BigDecimal(
		override val size: Int = 20,
		override val lastId: java.math.BigDecimal? = null,
	) : InfiniteScrollDesc<java.math.BigDecimal>()
}

sealed class InfiniteScrollAsc<K : Comparable<K>> : InfiniteScroll<K> {
	override fun getOrder() = InfiniteScroll.Order.OLDEST_FIRST

	data class UUID(
		override val size: Int = 20,
		override val lastId: java.util.UUID? = null,
	) : InfiniteScrollAsc<java.util.UUID>()
}

data class InfiniteScrollSlice<T, K : Comparable<K>>(
	val content: List<T>,
	val lastId: K?,
	val hasMore: Boolean,
) {
	companion object {
		fun <T, K : Comparable<K>> empty() = InfiniteScrollSlice<T, K>(
			content = emptyList(),
			lastId = null,
			hasMore = false,
		)
	}
}

fun <T, K : Comparable<K>> List<T>.toInfiniteScrollSlice(infiniteScroll: InfiniteScroll<K>, idSelector: T.() -> K) =
	InfiniteScrollSlice(
		content = this,
		lastId = this.lastOrNull()?.idSelector(),
		// this is not EXACTLY correct, as on last slice it would return true
		// the content size is exactly equal to slice size
		// but subsequent call will return false, so for now it's okay...
		hasMore = this.size == infiniteScroll.size,
	)
