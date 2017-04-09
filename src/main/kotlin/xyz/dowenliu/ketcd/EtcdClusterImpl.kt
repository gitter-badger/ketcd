package xyz.dowenliu.ketcd

import com.google.common.util.concurrent.ListenableFuture
import io.grpc.ManagedChannel
import xyz.dowenliu.ketcd.api.*
import java.util.*
import java.util.stream.Collectors

/**
 * Implementation of cluster client
 *
 * create at 2017/4/9
 * @author liufl
 * @since 0.1.0
 */
class EtcdClusterImpl(channel: ManagedChannel, token: String?) : EtcdCluster {
    private val stub: ClusterGrpc.ClusterFutureStub = configureStub(ClusterGrpc.newFutureStub(channel), token)

    override fun listMember(): ListenableFuture<MemberListResponse> =
            stub.memberList(MemberListRequest.getDefaultInstance())

    override fun addMember(vararg endpoint: EtcdEndpoint): ListenableFuture<MemberAddResponse> {
        require(endpoint.isNotEmpty(), {"Peer address for a member should not be empty."})
        val request = MemberAddRequest.newBuilder()
                .addAllPeerURLs(Arrays.stream(endpoint).map { it.toString() }.collect(Collectors.toList()))
                .build()
        return stub.memberAdd(request)
    }

    override fun removeMember(memberId: Long): ListenableFuture<MemberRemoveResponse> {
        val request = MemberRemoveRequest.newBuilder().setID(memberId).build()
        return stub.memberRemove(request)
    }

    override fun updateMember(memberId: Long, vararg endpoint: EtcdEndpoint): ListenableFuture<MemberUpdateResponse> {
        require(endpoint.isNotEmpty(), {"Peer address for a member should not be empty."})
        val request = MemberUpdateRequest.newBuilder()
                .addAllPeerURLs(Arrays.stream(endpoint).map { it.toString() }.collect(Collectors.toList()))
                .setID(memberId)
                .build()
        return stub.memberUpdate(request)
    }
}