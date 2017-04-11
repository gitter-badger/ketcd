package xyz.dowenliu.ketcd.operation

import xyz.dowenliu.ketcd.api.TxnRequest

/**
 * Build an etcd transaction.
 *
 * create at 2017/4/10
 * @author liufl
 * @since 0.1.0
 */
class TxnRequestPredicate private constructor(private val testList: List<Cmp>,
                              private val successOpList: List<Op>,
                              private val failureOpList: List<Op>) {
    companion object {
        fun newBuilder(): Builder = Builder()
    }

    class Builder internal constructor() {
        private var testList: List<Cmp> = emptyList()
        private var successOpList: List<Op> = emptyList()
        private var failureOpList: List<Op> = emptyList()

        fun test(vararg tests: Cmp): Builder {
            testList = tests.toList()
            return this
        }

        fun successDo(vararg ops: Op): Builder {
            successOpList = ops.toList()
            return this
        }

        fun failureDo(vararg ops: Op): Builder {
            failureOpList = ops.toList()
            return this
        }

        fun build(): TxnRequestPredicate = TxnRequestPredicate(testList, successOpList, failureOpList)
    }

    fun toTxnRequest(): TxnRequest =
            TxnRequest.newBuilder()
                    .addAllCompare(testList.map(Cmp::toCompare))
                    .addAllSuccess(successOpList.map(Op::toRequestOp))
                    .addAllFailure(failureOpList.map(Op::toRequestOp))
                    .build()
}