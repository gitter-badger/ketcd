package xyz.dowenliu.ketcd.operation.options

/**
 * The options for put operation
 *
 * create at 2017/4/10
 * @author liufl
 * @since 0.1.0
 *
 * @param leaseId The lease id bind to.
 * @param prevKV If the response will contains previous key-value pair.
 */
// TODO ignore-value
// TODO ignore-lease
class PutOptions private constructor(val leaseId: Long, val prevKV: Boolean) {
    companion object {
        val DEFAULT = newBuilder().build()

        fun newBuilder(): Builder = Builder()
    }

    class Builder internal constructor() {
        private var leaseId = 0L
        private var prevKV = false

        /**
         * Assign a _leaseId_ for a put operation. Zero means no lease.
         *
         * @param leaseId lease id to apply to a put operation
         * @return this builder to train
         * @throws IllegalArgumentException if lease is less than zero.
         */
        fun withLeaseId(leaseId: Long): Builder {
            require(leaseId >= 0, { "leaseId should greater than or equal to zero: leaseId=$leaseId" })
            this.leaseId = leaseId
            return this
        }

        /**
         * Set if response contains previous key-value pair.
         *
         * @param prevKV response will contains previous key-value pair if true.
         * @return this builder to train
         */
        fun prevKV(prevKV: Boolean = true): Builder {
            this.prevKV = prevKV
            return this
        }

        fun build(): PutOptions = PutOptions(leaseId, prevKV)
    }
}