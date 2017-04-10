package xyz.dowenliu.ketcd

import com.google.common.util.concurrent.ListenableFuture
import com.google.protobuf.ByteString
import io.grpc.ManagedChannel
import xyz.dowenliu.ketcd.api.*

/**
 * Implementation of etcd auth client.
 *
 * create at 2017/4/10
 * @author liufl
 * @since 0.1.0
 */
class EtcdAuthImpl(channel: ManagedChannel, token: String?) : EtcdAuth {
    private val stub = configureStub(AuthGrpc.newFutureStub(channel), token)

    override fun authEnable(): ListenableFuture<AuthEnableResponse> =
            stub.authEnable(AuthEnableRequest.getDefaultInstance())

    override fun authDisable(): ListenableFuture<AuthDisableResponse> =
            stub.authDisable(AuthDisableRequest.getDefaultInstance())

    override fun userList(): ListenableFuture<AuthUserListResponse> =
            stub.userList(AuthUserListRequest.getDefaultInstance())

    override fun userGet(username: String): ListenableFuture<AuthUserGetResponse> {
        val request = AuthUserGetRequest.newBuilder()
                .setNameBytes(username.toByteString())
                .build()
        return stub.userGet(request)
    }

    override fun userAdd(usernamePassword: UsernamePassword): ListenableFuture<AuthUserAddResponse> {
        val addRequest = AuthUserAddRequest.newBuilder()
                .setNameBytes(usernamePassword.username)
                .setPasswordBytes(usernamePassword.password)
                .build()
        return stub.userAdd(addRequest)
    }

    override fun userChangePassword(usernamePassword: UsernamePassword):
            ListenableFuture<AuthUserChangePasswordResponse> {
        val request = AuthUserChangePasswordRequest.newBuilder()
                .setNameBytes(usernamePassword.username)
                .setPasswordBytes(usernamePassword.password)
                .build()
        return stub.userChangePassword(request)
    }

    override fun userDelete(username: String): ListenableFuture<AuthUserDeleteResponse> =
            stub.userDelete(AuthUserDeleteRequest.newBuilder().setNameBytes(username.toByteString()).build())

    override fun roleList(): ListenableFuture<AuthRoleListResponse> =
            stub.roleList(AuthRoleListRequest.getDefaultInstance())

    override fun roleGet(role: String): ListenableFuture<AuthRoleGetResponse> =
            stub.roleGet(AuthRoleGetRequest.newBuilder().setRoleBytes(role.toByteString()).build())

    override fun roleAdd(name: String): ListenableFuture<AuthRoleAddResponse> =
            stub.roleAdd(AuthRoleAddRequest.newBuilder().setNameBytes(name.toByteString()).build())

    override fun roleGrantPermission(role: String,
                                     permType: Permission.Type,
                                     key: String,
                                     rangeEnd: String?): ListenableFuture<AuthRoleGrantPermissionResponse> {
        val permission = Permission.newBuilder()
                .setKey(key.toByteString())
                .setRangeEnd(rangeEnd?.toByteString() ?: ByteString.EMPTY)
                .setPermType(permType)
                .build()
        val request = AuthRoleGrantPermissionRequest.newBuilder()
                .setNameBytes(role.toByteString())
                .setPerm(permission)
                .build()
        return stub.roleGrantPermission(request)
    }

    override fun roleRevokePermission(role: String, key: String, rangeEnd: String?): ListenableFuture<AuthRoleRevokePermissionResponse> {
        val request = AuthRoleRevokePermissionRequest.newBuilder()
                .setRoleBytes(role.toByteString())
                .setKeyBytes(key.toByteString())
                .setRoleBytes(rangeEnd?.toByteString() ?: ByteString.EMPTY)
                .build()
        return stub.roleRevokePermission(request)
    }

    override fun roleDelete(role: String): ListenableFuture<AuthRoleDeleteResponse> =
            stub.roleDelete(AuthRoleDeleteRequest.newBuilder().setRoleBytes(role.toByteString()).build())

    override fun userGrantRole(username: String, role: String): ListenableFuture<AuthUserGrantRoleResponse> {
        val request = AuthUserGrantRoleRequest.newBuilder()
                .setUserBytes(username.toByteString())
                .setRoleBytes(role.toByteString())
                .build()
        return stub.userGrantRole(request)
    }

    override fun userRevokeRole(username: String, role: String): ListenableFuture<AuthUserRevokeRoleResponse> {
        val request = AuthUserRevokeRoleRequest.newBuilder()
                .setNameBytes(username.toByteString())
                .setRoleBytes(role.toByteString())
                .build()
        return stub.userRevokeRole(request)
    }
}