package org.kss.example.jooq

import org.jooq.DSLContext
import org.junit.jupiter.api.Test
import org.kss.example.tables.references.PROFILE
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class Example1JooqGenerationWorks(
	@Autowired private val dslContext: DSLContext
) {

	/**
	 * ``` sql
	 *
	 *  select
	 *    "public"."profile"."id",
	 *    "public"."profile"."name",
	 *    "public"."profile"."description",
	 *    "public"."profile"."birth_date"
	 *  from "public"."profile"
	 *
	 * ```
	 */
	@Test
	fun `the jooq classes are properly generated and ready to be used`() {
		dslContext
			.select()
			.from(PROFILE)
			.fetch()
	}

	/**
	 * ``` sql
	 *
	 *  select
	 *    "public"."profile"."id"
	 *  from "public"."profile"
	 *
	 * ```
	 */
	@Test
	fun `selecting only id`() {
		dslContext
			.select(PROFILE.ID)
			.from(PROFILE)
			.fetch()
	}
}
