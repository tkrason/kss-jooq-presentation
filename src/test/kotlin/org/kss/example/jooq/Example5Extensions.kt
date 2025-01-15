package org.kss.example.jooq

import org.jooq.Record
import org.jooq.TableField
import org.jooq.impl.DSL
import org.jooq.kotlin.get
import org.jooq.kotlin.mapping
import org.junit.jupiter.api.Test
import org.kss.example.jooq.helper.BaseTest
import org.kss.example.jooq.util.InfiniteScroll
import org.kss.example.jooq.util.InfiniteScrollAsc
import org.kss.example.jooq.util.InfiniteScrollSlice
import org.kss.example.jooq.util.page
import org.kss.example.jooq.util.toInfiniteScrollSlice
import org.kss.example.jooq.util.whereWithInfiniteScroll
import org.kss.example.tables.references.PROFILE
import org.kss.example.tables.references.PROFILE_INTEREST
import org.kss.example.tables.references.PROFILE_PICTURE
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.domain.SliceImpl
import java.util.UUID
import kotlin.time.measureTime

data class Result(
	val id: UUID,
	val name: String,
)

class Example5Extensions : BaseTest() {

	/**
	 * ``` sql
	 * select "public"."profile"."id",
	 *        "public"."profile"."name",
	 *        "public"."profile"."description",
	 *        "public"."profile"."birth_date",
	 *        "public"."profile"."address_id"
	 * from "public"."profile"
	 * offset 20 rows fetch next 20 rows only
	 * ```
	 */
	@Test
	fun `jooq with extensions`() {

		val pageable = PageRequest.of(1, 20)

		dslContext
			.select()
			.from(PROFILE)
			.page(pageable)
			.fetch()
	}

	@Test
	fun `jooq with infinite scroll`() {
		val infiniteScroll = InfiniteScrollAsc.UUID(size = 20)
		var result = fetchWithInfiniteScroll(infiniteScroll)

		while(result.hasMore) {
			var time = measureTime {
				result = fetchWithInfiniteScroll(InfiniteScrollAsc.UUID(size = 20, lastId = result.lastId))
			}

			println("Took: $time")
		}
	}

	fun fetchWithInfiniteScroll(infiniteScroll: InfiniteScroll<UUID>) : InfiniteScrollSlice<Result, UUID> {

		val profilePictures = DSL.multiset(
			DSL.select(
				PROFILE_PICTURE.FILE_URL.notNullable(),
				PROFILE_PICTURE.UPLOADED_AT.notNullable()
			)
				.from(PROFILE_PICTURE)
				.where(PROFILE_PICTURE.PROFILE_ID.eq(PROFILE.ID))
				.orderBy(PROFILE_PICTURE.UPLOADED_AT)
		).mapping(::ProfilePicture)

		val interests = DSL.multiset(
			DSL.select(
				PROFILE_INTEREST.interest.NAME.notNullable()
			)
				.from(PROFILE_INTEREST)
				.where(PROFILE_INTEREST.PROFILE_ID.eq(PROFILE.ID))
		).mapping(::Interest)

		val conditions = listOf(
			PROFILE.NAME.like("%2%")
		)

		return dslContext
			.select(
				PROFILE.ID,
				PROFILE.NAME,
				profilePictures,
				interests
			)
			.from(PROFILE)
			.whereWithInfiniteScroll(
				conditions = conditions,
				infiniteScroll = infiniteScroll,
				idFieldSelector = PROFILE.ID
			)
			.fetch()
			.map {
				Result(
					id = it[PROFILE.ID]!!,
					name = it[PROFILE.NAME]!!
				)
			}
			.toInfiniteScrollSlice(
				infiniteScroll = infiniteScroll,
				idSelector = { id }
			)
	}

	private fun <T> TableField<Record, T?>.notNullable() = this.convertFrom { it!! }

	@Test
	fun `the same but with pageable`() {
		var pageable = PageRequest.of(0, 20)
		var result = fetchWithPageableNoOptimization(pageable)

		while(result.hasNext()) {
			pageable = pageable.next()
			var time = measureTime {
				result = fetchWithPageableNoOptimization(pageable)
			}
			println("Took: $time")
		}
	}

	fun fetchWithPageableNoOptimization(pageable: Pageable) : Slice<Result> {

		val profilePictures = DSL.multiset(
			DSL.select(
				PROFILE_PICTURE.FILE_URL.notNullable(),
				PROFILE_PICTURE.UPLOADED_AT.notNullable()
			)
				.from(PROFILE_PICTURE)
				.where(PROFILE_PICTURE.PROFILE_ID.eq(PROFILE.ID))
				.orderBy(PROFILE_PICTURE.UPLOADED_AT)
		).mapping(::ProfilePicture)

		val interests = DSL.multiset(
			DSL.select(
				PROFILE_INTEREST.interest.NAME.notNullable()
			)
				.from(PROFILE_INTEREST)
				.where(PROFILE_INTEREST.PROFILE_ID.eq(PROFILE.ID))
		).mapping(::Interest)

		val conditions = listOf(
			PROFILE.NAME.like("%2%")
		)

		return dslContext
			.select(
				PROFILE.ID,
				PROFILE.NAME,
				profilePictures,
				interests
			)
			.from(PROFILE)
			.where(conditions)
			.page(pageable)
			.fetch()
			.map {
				Result(
					id = it[PROFILE.ID]!!,
					name = it[PROFILE.NAME]!!
				)
			}
			.let { SliceImpl(it, pageable, it.isNotEmpty()) }
	}

	@Test
	fun `slightly optimize pageable`() {
		var pageable = PageRequest.of(0, 20)
		var result = fetchWithPageableOptimize(pageable)

		while(result.hasNext()) {
			pageable = pageable.next()
			var time = measureTime {
				result = fetchWithPageableOptimize(pageable)
			}
			println("Took: $time")
		}
	}

	fun fetchWithPageableOptimize(pageable: Pageable) : Slice<Result> {

		val profilePictures = DSL.multiset(
			DSL.select(
				PROFILE_PICTURE.FILE_URL.notNullable(),
				PROFILE_PICTURE.UPLOADED_AT.notNullable()
			)
				.from(PROFILE_PICTURE)
				.where(PROFILE_PICTURE.PROFILE_ID.eq(PROFILE.ID))
				.orderBy(PROFILE_PICTURE.UPLOADED_AT)
		).mapping(::ProfilePicture)

		val interests = DSL.multiset(
			DSL.select(
				PROFILE_INTEREST.interest.NAME.notNullable()
			)
				.from(PROFILE_INTEREST)
				.where(PROFILE_INTEREST.PROFILE_ID.eq(PROFILE.ID))
		).mapping(::Interest)

		val conditions = listOf(
			PROFILE.NAME.like("%2%")
		)

		val subTable = DSL.select(PROFILE.ID)
			.from(PROFILE)
			.where(conditions)
			.page(pageable)

		return dslContext
			.select(
				PROFILE.ID,
				PROFILE.NAME,
				profilePictures,
				interests
			)
			.from(subTable)
			.join(PROFILE).on(PROFILE.ID.eq(subTable[PROFILE.ID]))
			.fetch()
			.map {
				Result(
					id = it[PROFILE.ID]!!,
					name = it[PROFILE.NAME]!!
				)
			}
			.let { SliceImpl(it, pageable, it.isNotEmpty()) }
	}
}
