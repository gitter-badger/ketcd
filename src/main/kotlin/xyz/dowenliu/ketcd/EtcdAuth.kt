package xyz.dowenliu.ketcd

import com.google.common.util.concurrent.ListenableFuture
import xyz.dowenliu.ketcd.api.*

/**
 * Interface of auth talking to etcd
 *
 * create at 2017/4/10
 * @author liufl
 * @since 0.1.0
 */
interface EtcdAuth {
    /**
     * Enables authentication.
     *
     * @return [ListenableFuture] of [AuthEnableResponse]
     */
    fun authEnable(): ListenableFuture<AuthEnableResponse>

    /**
     * Disables authentication.
     *
     * @return [ListenableFuture] of [AuthDisableResponse]
     */
    fun authDisable(): ListenableFuture<AuthDisableResponse>

    /**
     * Gets a list of all users.
     *
     * @return [ListenableFuture] of [AuthUserListResponse]
     */
    fun userList(): ListenableFuture<AuthUserListResponse>

    /**
     * Gets detailed user information.
     *
     * @param username the username of the user to get.
     * @return [ListenableFuture] of [AuthUserGetResponse]
     */
    fun userGet(username: String): ListenableFuture<AuthUserGetResponse>

    /**
     * Adds a new user.
     *
     * @param usernamePassword The username and password info of the new user
     * @return [ListenableFuture] of [AuthUserAddResponse]
     */
    fun userAdd(usernamePassword: UsernamePassword): ListenableFuture<AuthUserAddResponse>

    /**
     * Changes the password of a specified user.
     *
     * @param usernamePassword The username of the user and the new password to change to
     * @return [ListenableFuture] of [AuthUserChangePasswordResponse]
     */
    fun userChangePassword(usernamePassword: UsernamePassword): ListenableFuture<AuthUserChangePasswordResponse>

    /**
     * Deletes a specified user.
     *
     * @param username the username of the user to delete
     * @return [ListenableFuture] of [AuthUserDeleteResponse]
     */
    fun userDelete(username: String): ListenableFuture<AuthUserDeleteResponse>

    /**
     * Gets a list of all roles.
     *
     * @return [ListenableFuture] of [AuthRoleListResponse]
     */
    fun roleList(): ListenableFuture<AuthRoleListResponse>

    /**
     * Gets detailed role information.
     *
     * @param role The name of the role to get.
     * @return [ListenableFuture] of [AuthRoleGetResponse]
     */
    fun roleGet(role: String): ListenableFuture<AuthRoleGetResponse>

    /**
     * Adds a new role.
     *
     * @param name The name of the new role.
     * @return [ListenableFuture] of [AuthRoleAddResponse]
     */
    fun roleAdd(name: String): ListenableFuture<AuthRoleAddResponse>

    /**
     * Grants a permission of a specified key or range to a specified role.
     *
     * @param role the name of the role to grant to.
     * @param permType the permission type to grant.
     * @param key the target key to grant on.
     * @param rangeEnd the target range to grant on to.
     * @return [ListenableFuture] of [AuthRoleGrantPermissionResponse]
     */
    // TEST_THIS test when rangeEnd is null
    fun roleGrantPermission(role: String,
                            permType: Permission.Type,
                            key: String,
                            rangeEnd: String? = null): ListenableFuture<AuthRoleGrantPermissionResponse>

    /**
     * Revokes a key or range permission of a specified role.
     *
     * @param role The name of the role to revoke.
     * @param key The key to revoke.
     * @param rangeEnd The rangeEnd to revoke.
     * @return [ListenableFuture] of [AuthRoleRevokePermissionResponse]
     */
    // TEST_THIS test when rangeEnd is null
    fun roleRevokePermission(role: String,
                             key: String,
                             rangeEnd: String? = null): ListenableFuture<AuthRoleRevokePermissionResponse>

    /**
     * Deletes a specified role.
     *
     * @param role The name of the role to delete.
     * @return [ListenableFuture] of [AuthRoleDeleteResponse]
     */
    fun roleDelete(role: String): ListenableFuture<AuthRoleDeleteResponse>

    /**
     * Grants a role to a specified user.
     *
     * @param username The name of the user to grant to.
     * @param role The name of the role to grant.
     * @return [ListenableFuture] of [AuthUserGrantRoleResponse]
     */
    fun userGrantRole(username: String, role: String): ListenableFuture<AuthUserGrantRoleResponse>

    /**
     * Revokes a role of specified user.
     *
     * @param username The name of the user to revoke.
     * @param role The name of the role to revoke.
     * @return [ListenableFuture] of [AuthUserRevokeRoleResponse]
     */
    fun userRevokeRole(username: String, role: String): ListenableFuture<AuthUserRevokeRoleResponse>
}