package xyz.dowenliu.ketcd.operation

import com.google.protobuf.ByteString
import xyz.dowenliu.ketcd.api.DeleteRangeRequest
import xyz.dowenliu.ketcd.api.PutRequest
import xyz.dowenliu.ketcd.api.RangeRequest
import xyz.dowenliu.ketcd.api.RequestOp
import xyz.dowenliu.ketcd.operation.options.DeleteOptions
import xyz.dowenliu.ketcd.operation.options.GetOptions
import xyz.dowenliu.ketcd.operation.options.PutOptions

/**
 * Etcd Op
 *
 * create at 2017/4/10
 * @author liufl
 * @since 0.1.0
 */
abstract class Op internal constructor(protected val type: Type, protected val key: ByteString) {
    companion object {
        fun put(key: ByteString, value: ByteString, options: PutOptions): PutOp = PutOp(key, value, options)

        fun get(key: ByteString, options: GetOptions): GetOp = GetOp(key, options)

        fun delete(key: ByteString, options: DeleteOptions): DeleteOp = DeleteOp(key, options)
    }

    abstract fun toRequestOp(): RequestOp

    /**
     * Op type.
     */
    enum class Type {
        PUT, RANGE, DELETE_RANGE
    }

     class PutOp internal constructor(key: ByteString,
                                      private val value: ByteString,
                                      private val options: PutOptions) : Op(Type.PUT, key) {
        override fun toRequestOp(): RequestOp {
            val request = PutRequest.newBuilder()
                    .setKey(key)
                    .setValue(value)
                    .setLease(options.leaseId)
                    .setPrevKv(options.prevKV)
                    .build()
            return RequestOp.newBuilder().setRequestPut(request).build()
        }
    }

    class GetOp internal constructor(key: ByteString, private val options: GetOptions) : Op(Type.RANGE, key) {
        override fun toRequestOp(): RequestOp {
            val builder = RangeRequest.newBuilder()
                    .setKey(key)
                    .setCountOnly(options.countOnly)
                    .setLimit(options.limit)
                    .setRevision(options.revision)
                    .setKeysOnly(options.keysOnly)
                    .setSerializable(options.serializable)
                    .setSortOrder(options.sortOrder)
                    .setSortTarget(options.sortTarget)
            options.endKey?.let { builder.rangeEnd = it }
            return RequestOp.newBuilder().setRequestRange(builder).build()
        }
    }

    class DeleteOp internal constructor(key: ByteString, private val options: DeleteOptions) : Op(Type.DELETE_RANGE, key) {
        override fun toRequestOp(): RequestOp {
            val builder = DeleteRangeRequest.newBuilder()
                    .setKey(key)
                    .setPrevKv(options.prevKV)
            options.endKey?.let { builder.rangeEnd = it }
            return RequestOp.newBuilder().setRequestDeleteRange(builder).build()
        }
    }
}