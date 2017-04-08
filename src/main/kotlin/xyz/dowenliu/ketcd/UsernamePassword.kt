package xyz.dowenliu.ketcd

import com.google.protobuf.ByteString

/**
 * Username&password info
 *
 * create at 2017/4/8
 * @author liufl
 * @since 0.1.0
 */
data class UsernamePassword internal constructor(val username: ByteString, val password: ByteString){
    companion object {
        fun of(username: String, password: String): UsernamePassword {
            require(username.isNotBlank(), { "username can not be empty."})
            require(password.isNotBlank(), { "password can not be empty" })
            return UsernamePassword(ByteString.copyFromUtf8(username.trim()), ByteString.copyFromUtf8(password.trim()))
        }
    }
}