@file:JvmName("EtcdClientUtil")

package xyz.dowenliu.ketcd

import com.google.protobuf.ByteString
import io.grpc.ManagedChannelBuilder
import io.grpc.Metadata
import io.grpc.NameResolver
import io.grpc.stub.AbstractStub
import xyz.dowenliu.ketcd.resolver.SimpleEtcdNameResolverFactory
import java.net.URI
import java.util.stream.Collectors

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

internal fun simpleNameResolverFactory(endpoints: List<EtcdEndpoint>): NameResolver.Factory =
        SimpleEtcdNameResolverFactory(endpoints.stream().map { URI(it.toString()) }.collect(Collectors.toList()))

internal fun defaultChannelBuilder(factory: NameResolver.Factory): ManagedChannelBuilder<*> =
        ManagedChannelBuilder.forTarget("etcd").nameResolverFactory(factory).usePlaintext(true)