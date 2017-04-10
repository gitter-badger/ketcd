package xyz.dowenliu.ketcd.version

import kotlin.annotation.AnnotationRetention.*
import kotlin.annotation.AnnotationTarget.*

/**
 * Annotate which etcd version the function, field or type faced to.
 *
 * create at 2017/4/10
 * @author liufl
 * @since 0.1.0
 */
@MustBeDocumented
@Retention(BINARY)
@Target(CLASS, FUNCTION, FIELD)
annotation class ForEtcdVersion(val since: EtcdVersion, val until: EtcdVersion = EtcdVersion.V3_1_5)