package org.kss.example.jooq

import org.junit.jupiter.api.Test
import org.kss.example.jooq.helper.BaseTest
import org.kss.example.tables.references.PROFILE
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class Example1JooqGenerationWorks: BaseTest() {

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
		// Note: Set db to kss-local
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
