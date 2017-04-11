package xyz.dowenliu.ketcd.operation

import com.google.protobuf.ByteString
import xyz.dowenliu.ketcd.api.Compare

/**
 * ComparePredicate  target used in [TxnRequestPredicate]
 *
 * create at 2017/4/11
 * @author liufl
 * @since 0.1.0
 *
 * @param T The compare target value type of this compare.
 *
 * @property target The compare target used for this compare.
 * @property targetValue The compare target value of this compare.
 */
abstract class CmpTarget<out T>(val target: Compare.CompareTarget, val targetValue: T) {
    companion object {
        /**
         * ComparePredicate on a given _version_
         *
         * @param version version to compare.
         * @return the version compare target.
         */
        fun version(version: Long): VersionCmpTarget = VersionCmpTarget(version)

        /**
         * ComparePredicate on the create _revision_
         *
         * @param revision the create revision
         * @return the create revision compare target.
         */
        fun createVersion(revision: Long): CreateRevisionCmpTarget = CreateRevisionCmpTarget(revision)

        /**
         * ComparePredicate on the modification _revision_
         *
         * @param revision the modification revision
         * @return the modification revision compare target.
         */
        fun modRevision(revision: Long): ModRevisionCmpTarget = ModRevisionCmpTarget(revision)

        /**
         * ComparePredicate on the _value_
         *
         * @param value the value to compare.
         * @return the value compare target.
         */
        fun value(value: ByteString): ValueCmpTarget = ValueCmpTarget(value)
    }

    class VersionCmpTarget internal constructor(targetValue: Long) :
            CmpTarget<Long>(Compare.CompareTarget.VERSION, targetValue)

    class CreateRevisionCmpTarget internal constructor(targetValue: Long) :
            CmpTarget<Long>(Compare.CompareTarget.CREATE, targetValue)

    class ModRevisionCmpTarget internal constructor(targetValue: Long) :
            CmpTarget<Long>(Compare.CompareTarget.MOD, targetValue)

    class ValueCmpTarget internal constructor(targetValue: ByteString) :
            CmpTarget<ByteString>(Compare.CompareTarget.VALUE, targetValue)
}