package xyz.dowenliu.ketcd.operation.options

import com.google.protobuf.ByteString
import xyz.dowenliu.ketcd.version.EtcdVersion
import xyz.dowenliu.ketcd.version.ForEtcdVersion

/**
 * The options for delete operation.
 *
 * create at 2017/4/10
 * @author liufl
 * @since 0.1.0
 *
 * @property endKey The end key of the delete request. If it is set, the delete request will delete the keys
 * from _key_ to _endKey_ (exclusive).
 *
 * If end key is '\u0000', the range is all keys >= key. (--from-key)
 *
 * If the end key is one bit larger than the given key, then it deletes all keys with the prefix
 * (the given key). (--prefix)
 *
 * If both key and end key are '\u0000', it deletes all keys.
 * @property prevKV Flag to get previous key/value pairs before deleting them.
 */
class DeleteOptions private constructor(val endKey: ByteString?,
                                        @ForEtcdVersion(EtcdVersion.V3_0_11) val prevKV: Boolean) {
    companion object {
        val DEFAULT = newBuilder().build()

        /**
         * Create a builder to construct options for get operation.
         *
         * @return builder
         */
        fun newBuilder(): Builder = Builder()
    }

    class Builder internal constructor() {
        private var endKey: ByteString? = null
        private var prevKV = false

        /**
         * Set the end key of the delete request. If it is set, the delete request will delete the keys
         * from _key_ to _endKey_ (exclusive).
         *
         * If end key is '\u0000', the range is all keys >= key. (--from-key)
         *
         * If the end key is one bit larger than the given key, then it deletes all keys with the prefix
         * (the given key). (--prefix)
         *
         * If both key and end key are '\u0000', it deletes all keys.
         *
         * @param endKey end key
         * @return this builder to train.
         */
        fun withRange(endKey: ByteString?): Builder {
            this.endKey = endKey
            return this
        }

        /**
         * Get the previous key/value pairs before deleting them.
         *
         * @param prevKV flag to get previous key/value pairs before deleting them.
         * @return this builder to train.
         */
        @ForEtcdVersion(EtcdVersion.V3_0_11)
        fun withPrevKV(prevKV: Boolean): Builder {
            this.prevKV = prevKV
            return this
        }

        /**
         * Build the delete options
         *
         * @return the delete options
         */
        fun build(): DeleteOptions = DeleteOptions(endKey, prevKV)
    }
}