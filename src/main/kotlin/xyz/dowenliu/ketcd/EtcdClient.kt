package xyz.dowenliu.ketcd

import com.google.common.base.Supplier
import com.google.common.base.Suppliers
import com.google.common.util.concurrent.ListenableFuture
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.NameResolver
import xyz.dowenliu.ketcd.api.AuthGrpc
import xyz.dowenliu.ketcd.api.AuthenticateRequest
import xyz.dowenliu.ketcd.api.AuthenticateResponse
import xyz.dowenliu.ketcd.exception.AuthFailedException
import xyz.dowenliu.ketcd.exception.ConnectException
import xyz.dowenliu.ketcd.resolver.AbstractEtcdNameResolverFactory
import java.io.Closeable
import java.util.*
import java.util.concurrent.ExecutionException
import java.util.stream.Collectors

/**
 * Etcd Client
 *
 * create at 2017/4/9
 * @author liufl
 * @since 0.1.0
 */
class EtcdClient private constructor(private val endpoints: List<EtcdEndpoint>?,
                                     private val channel: ManagedChannel,
                                     private val nameResolverFactory: NameResolver.Factory,
                                     private val clusterClient: Supplier<EtcdCluster>) : Closeable {
    companion object {
        /**
         * Creates new Builder instance
         */
        fun newBuilder(): Builder = Builder()

        @Throws(ConnectException::class, AuthFailedException::class)
        private fun getToken(channel: ManagedChannel, builder: Builder): String? {
            val usernamePassword = builder.usernamePassword ?: return null
            try {
                return authenticate(channel, usernamePassword).get().token
            } catch (e: InterruptedException) {
                throw ConnectException("connect to etcd failed.", e)
            } catch (e: ExecutionException) {
                throw AuthFailedException("auth failed as wrong username or password", e)
            }
        }

        private fun authenticate(channel: ManagedChannel,
                                 usernamePassword: UsernamePassword): ListenableFuture<AuthenticateResponse> =
                AuthGrpc.newFutureStub(channel).authenticate(
                        AuthenticateRequest.newBuilder()
                                .setNameBytes(usernamePassword.username)
                                .setPasswordBytes(usernamePassword.password)
                                .build()
                )
    }

    fun getClusterClient() = clusterClient.get()

    override fun close() {
        channel.shutdownNow()
    }

    class Builder internal constructor() {
        private val endpoints: MutableList<EtcdEndpoint> = mutableListOf()
        /**
         * Etcd auth username password info
         */
        var usernamePassword: UsernamePassword? = null
            private set
        /**
         * NameResolver factory for etcd client.
         */
        var nameResolverFactory: AbstractEtcdNameResolverFactory? = null
            private set
        /**
         * Channel builder
         */
        var channelBuilder: ManagedChannelBuilder<*>? = null
            private set

        /**
         * gets the endpoints for the builder.
         *
         * @return the list of endpoints configured for the builder
         */
        fun endpoints(): List<String> = endpoints.distinct().map { it.toString() }.toList()

        /**
         * add etcd server endpoint
         *
         * @param endpoint etcd server endpoint
         * @return this builder to train
         */
        fun addEndpoint(endpoint: EtcdEndpoint): Builder {
            endpoints.add(endpoint)
            return this
        }

        /**
         * add etcd server endpoint
         *
         * @param endpoint etcd server endpoint expression
         * @return this builder to train
         * @throws IllegalArgumentException If the expression given breaks the endpoint format rule.
         * @throws NullPointerException If the expression given do not contain an port part or it's not a valid integer.
         * @see EtcdEndpoint.of
         */
        fun addEndpoint(endpoint: String): Builder = addEndpoint(EtcdEndpoint.of(endpoint))

        /**
         * add etcd server endpoints
         *
         * @param endpoints etcd server endpoints
         * @return this builder to train
         */
        fun addEndpoints(vararg endpoints: EtcdEndpoint): Builder {
            this.endpoints.addAll(endpoints)
            return this
        }

        /**
         * add etcd server endpoints
         * @param endpoints etcd server endpoint expressions
         * @return this builder to train
         * @throws IllegalArgumentException If any expression given breaks the endpoint format rule.
         * @throws NullPointerException If any expression given do not contain an port part or it's not a valid integer.
         * @see EtcdEndpoint.of
         */
        fun addEndpoints(vararg endpoints: String): Builder {
            this.endpoints.addAll(Arrays.stream(endpoints).map { EtcdEndpoint.of(it) }.collect(Collectors.toList()))
            return this
        }

        /**
         * config etcd auth username password info.
         *
         * @param usernamePassword etcd auto username password info
         * @return this builder to train
         */
        fun setUsernamePassword(usernamePassword: UsernamePassword): Builder {
            this.usernamePassword = usernamePassword
            return this
        }

        /**
         * config name resolver factory for etcd client.
         *
         * @param nameResolverFactory NameResolverFactory instance to use.
         * @return this builder to train
         */
        fun setNameResolverFactory(nameResolverFactory: AbstractEtcdNameResolverFactory): Builder {
            this.nameResolverFactory = nameResolverFactory
            return this
        }

        /**
         * config channel builder
         *
         * @param channelBuilder ManagedChannelBuilder instance to use.
         * @return this builder to train
         */
        fun setChannelBuilder(channelBuilder: ManagedChannelBuilder<*>): Builder {
            this.channelBuilder = channelBuilder
            return this
        }

        fun build(): EtcdClient {
            var _nameResolverFactory: NameResolver.Factory? = nameResolverFactory
            val endpoints: List<EtcdEndpoint>?
            if (_nameResolverFactory != null)
                endpoints = null
            else {
                // no nameResolverFactory set, use SimpleEtcdNameResolver
                endpoints = this.endpoints
                _nameResolverFactory = simpleNameResolverFactory(endpoints)
            }
            val channel = channelBuilder?.build() ?: defaultChannelBuilder(_nameResolverFactory).build()
            val token = getToken(channel, this)
            return EtcdClient(endpoints, channel, _nameResolverFactory,
                    Suppliers.memoize { EtcdClusterImpl(channel, token) })
        }
    }
}