package org.kss.example.jooq

import io.kotest.matchers.collections.shouldContainExactly
import org.jooq.impl.DSL
import org.junit.jupiter.api.Test
import org.kss.example.jooq.helper.BaseTest
import org.kss.example.tables.references.PROFILE
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate
import java.time.Month

@Suppress("KotlinConstantConditions")
@SpringBootTest
class Example2BasicSqlSelect : BaseTest() {

	/**
	 *
	 * ``` sql
	 * select "public"."profile"."id"
	 * from "public"."profile"
	 * where (
	 *   "public"."profile"."name" like ?
	 *   and "public"."profile"."description" ilike ?
	 *   and "public"."profile"."birth_date" > cast(? as date)
	 * )
	 * ```
	 *
	 * ``` sql
	 * select "public"."profile"."id"
	 * from "public"."profile"
	 * where (
	 *   "public"."profile"."name" like 'Adam'
	 *   and "public"."profile"."description" ilike '%cOoL%'
	 *   and "public"."profile"."birth_date" > date '2000-01-01'
	 * )
	 *
	 * ```
	 */
	@Test
	fun `jooq with conditions`() {
		// given
		val peter = testHelper.getProfile(
			name = "Peter",
			description = "Hello Im cool match me",
			birthDate = LocalDate.of(1990, Month.APRIL, 20)
		)
		val adam = testHelper.getProfile(
			name = "Adam",
			description = "Im cool",
			birthDate = LocalDate.of(2001, Month.NOVEMBER, 20)
		)

		// when
		val conditions = listOf(
			PROFILE.NAME.like("Adam"),
			PROFILE.DESCRIPTION.likeIgnoreCase("%cOoL%"),
			PROFILE.BIRTH_DATE.gt(LocalDate.of(2000, Month.JANUARY, 1)),
			// PROFILE.BIRTH_DATE.gt(LocalDateTime.now())  // <-- This would error as types not match!
		)

		// can be also user with OR...
		listOf(
			DSL.or(
				PROFILE.NAME.like("Adam"),
				PROFILE.DESCRIPTION.likeIgnoreCase("%cOoL%"),
			),
			PROFILE.BIRTH_DATE.gt(LocalDate.of(2000, Month.JANUARY, 1)),
		)

		val ids = dslContext
			.select(PROFILE.ID)
			.from(PROFILE)
			.where(conditions)
			.fetch()
			.map { it[PROFILE.ID]!! }

		// then
		ids shouldContainExactly listOf(adam.id)
	}

	@Test
	fun `multiple ad-hoc conditions`() {
		// given
		val peter = testHelper.getProfile(
			name = "Peter", description = "Hello Im cool match me", birthDate = LocalDate.of(1990, Month.APRIL, 20)
		)
		val adam = testHelper.getProfile(
			name = "Adam", description = "Im cool", birthDate = LocalDate.of(2001, Month.NOVEMBER, 20)
		)

		val nameFilter: String? = null
		val descriptionFilter: String? = null
		val birthDayFilter: LocalDate? = LocalDate.of(2000, Month.JANUARY, 1)
		//val birthDayFilter: LocalDate? = null

		// when
		val conditions = listOfNotNull(
			nameFilter?.let { PROFILE.NAME.eq(it) },
			descriptionFilter?.let { PROFILE.DESCRIPTION.likeIgnoreCase("%$it%") },
			birthDayFilter?.let { PROFILE.BIRTH_DATE.gt(it) }
		)

		dslContext
			.select(PROFILE.ID)
			.from(PROFILE)
			.where(conditions)
			.fetch()
	}
}
