package xyz.dowenliu.ketcd.operation

import xyz.dowenliu.ketcd.api.TxnRequest

/**
 * Build an etcd transaction.
 *
 * create at 2017/4/10
 * @author liufl
 * @since 0.1.0
 */
class TxnRequestPredicate private constructor(private val testList: List<ComparePredicate>,
                                              private val successOpList: List<RequestOpPredicate>,
                                              private val failureOpList: List<RequestOpPredicate>) {
    companion object {
        fun newBuilder(): Builder = Builder()
    }

    class Builder internal constructor() {
        private var testList: List<ComparePredicate> = emptyList()
        private var successOpList: List<RequestOpPredicate> = emptyList()
        private var failureOpList: List<RequestOpPredicate> = emptyList()

        fun test(vararg tests: ComparePredicate): Builder {
            testList = tests.toList()
            return this
        }

        fun successDo(vararg ops: RequestOpPredicate): Builder {
            successOpList = ops.toList()
            return this
        }

        fun failureDo(vararg ops: RequestOpPredicate): Builder {
            failureOpList = ops.toList()
            return this
        }

        fun build(): TxnRequestPredicate = TxnRequestPredicate(testList, successOpList, failureOpList)
    }

    fun toTxnRequest(): TxnRequest =
            TxnRequest.newBuilder()
                    .addAllCompare(testList.map(ComparePredicate::toCompare))
                    .addAllSuccess(successOpList.map(RequestOpPredicate::toRequestOp))
                    .addAllFailure(failureOpList.map(RequestOpPredicate::toRequestOp))
                    .build()
}