@file:JvmName("EtcdClientUtil")

package xyz.dowenliu.ketcd

import com.google.protobuf.ByteString
import io.grpc.Metadata
import io.grpc.stub.AbstractStub

/**
 * create at 2017/4/9
 * @author liufl
 * @since 0.1.0
 */

internal const val TOKEN = "token"
val NULL_KEY = ByteString.copyFrom(byteArrayOf('\u0000'.toByte()))

internal fun <T: AbstractStub<T>> configureStub(stub: T, token: String?): T {
    token ?: return stub
    val metadata = Metadata()
    metadata.put(Metadata.Key.of(TOKEN, Metadata.ASCII_STRING_MARSHALLER), token)
    return stub.withCallCredentials { _, _, _, applier -> applier.apply(metadata) }
}