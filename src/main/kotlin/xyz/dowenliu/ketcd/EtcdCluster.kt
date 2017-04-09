package xyz.dowenliu.ketcd

import com.google.common.util.concurrent.ListenableFuture
import xyz.dowenliu.ketcd.api.MemberAddResponse
import xyz.dowenliu.ketcd.api.MemberListResponse
import xyz.dowenliu.ketcd.api.MemberRemoveResponse
import xyz.dowenliu.ketcd.api.MemberUpdateResponse

/**
 * Interface of cluster client talking to etcd
 *
 * create at 2017/4/9
 * @author liufl
 * @since 0.1.0
 */
interface EtcdCluster {
    /**
     * lists the current cluster membership
     * @return [ListenableFuture] of [MemberListResponse]
     */
    fun listMember(): ListenableFuture<MemberListResponse>

    /**
     * add a new member into the cluster.
     *
     * @param endpoint the **peer** address of the new member
     * @return [ListenableFuture] of [MemberAddResponse]
     */
    fun addMember(endpoint: EtcdEndpoint): ListenableFuture<MemberAddResponse>

    /**
     * remove an existing member from the cluster.
     *
     * @param memberId the id of the member to remove
     * @return [ListenableFuture] of [MemberRemoveResponse]
     */
    fun removeMember(memberId: Long): ListenableFuture<MemberRemoveResponse>

    /**
     * update peer address of the member
     *
     * @param memberId the id of the member to update.
     * @param endpoint the new **peer** address of the member
     * @return [ListenableFuture] of [MemberUpdateResponse]
     */
    fun updateMember(memberId: Long, endpoint: EtcdEndpoint): ListenableFuture<MemberUpdateResponse>
}