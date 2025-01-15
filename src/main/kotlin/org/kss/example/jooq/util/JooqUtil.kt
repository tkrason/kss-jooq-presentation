package org.kss.example.jooq.util

import org.jooq.Condition
import org.jooq.Record
import org.jooq.SelectConditionStep
import org.jooq.SelectJoinStep
import org.jooq.SelectLimitPercentStep
import org.jooq.TableField
import org.jooq.impl.DSL
import org.springframework.data.domain.Pageable

fun <R> SelectJoinStep<R>.page(pageable: Pageable) where R : Record =
	if (pageable.isPaged) this.offset(pageable.offset).limit(pageable.pageSize) else this

fun <R> SelectConditionStep<R>.page(pageable: Pageable) where R : Record =
	if (pageable.isPaged) this.offset(pageable.offset).limit(pageable.pageSize) else this


fun <R : Record, K : Comparable<K>> SelectJoinStep<R>.whereWithInfiniteScroll(
	conditions: List<Condition>,
	infiniteScroll: InfiniteScroll<K>,
	idFieldSelector: TableField<Record, K?>,
): SelectLimitPercentStep<R> {
	val lastId = infiniteScroll.lastId

	val idCondition = lastId?.let {
		when (infiniteScroll.getOrder()) {
			InfiniteScroll.Order.NEWEST_FIRST -> DSL.and(idFieldSelector.lt(lastId))
			InfiniteScroll.Order.OLDEST_FIRST -> DSL.and(idFieldSelector.gt(lastId))
		}
	} ?: DSL.noCondition()

	val sortField = when (infiniteScroll.getOrder()) {
		InfiniteScroll.Order.NEWEST_FIRST -> idFieldSelector.desc()
		InfiniteScroll.Order.OLDEST_FIRST -> idFieldSelector.asc()
	}

	return this
		.where(conditions)
		.and(idCondition)
		.orderBy(sortField)
		.limit(infiniteScroll.size)
}
