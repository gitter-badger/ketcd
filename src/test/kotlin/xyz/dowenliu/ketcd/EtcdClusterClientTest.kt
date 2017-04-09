package xyz.dowenliu.ketcd

import org.slf4j.LoggerFactory
import org.testng.annotations.Test
import org.testng.asserts.Assertion
import xyz.dowenliu.ketcd.api.Member
import java.util.concurrent.TimeUnit

/**
 * test etcd cluster client
 *
 * create at 2017/4/9
 * @author liufl
 * @since 0.1.0
 */
class EtcdClusterClientTest {
    private val assertion = Assertion()

    private var addedMember: Member? = null

    /**
     * test list custer function
     */
    @Test
    fun testListCluster() {
        val etcdClient = EtcdClient.newBuilder().addEndpoints(*endpoints).build()
        val clusterClient = etcdClient.getClusterClient()
        val response = clusterClient.listMember().get()
        assertion.assertEquals(response.membersCount, 3, "Members: ${response.membersCount}")
    }

    /**
     * test add cluster function, added member will be removed by testDeleteMember
     */
    @Test(dependsOnMethods = arrayOf("testListCluster"))
    fun testAddMember() {
        val etcdClient = EtcdClient.newBuilder().addEndpoint(endpoints[0]).addEndpoint(endpoints[1]).build()
        val clusterClient = etcdClient.getClusterClient()
        val response = clusterClient.listMember().get()
        assertion.assertEquals(response.membersCount, 3)
        val addResponse = clusterClient.addMember(*peerUrls.copyOfRange(2, 3).map { EtcdEndpoint.of(it) }.toTypedArray())
                .get(5, TimeUnit.SECONDS)
        val member = addResponse.member
        assertion.assertNotNull(member, "added member: ${member.id}")
        addedMember = member
    }

    /**
     * test update peer url for member
     */
    @Test(dependsOnMethods = arrayOf("testAddMember"))
    fun testUpdateMember() {
        var throwable: Throwable? = null
        try {
            val etcdClient = EtcdClient.newBuilder().addEndpoint(endpoints[1]).addEndpoint(endpoints[2]).build()
            val clusterClient = etcdClient.getClusterClient()
            val response = clusterClient.listMember().get()
            clusterClient.updateMember(response.getMembers(0).id, EtcdEndpoint.of("http://localhost:12380")).get()
        } catch (e: Exception) {
            LoggerFactory.getLogger(javaClass).error("", e)
            throwable = e
        }
        assertion.assertNull(throwable, "update for member")
    }

    fun testDeleteMember() {
        val etcdClient = EtcdClient.newBuilder().addEndpoint(endpoints[0]).addEndpoint(endpoints[1]).build()
        val clusterClient = etcdClient.getClusterClient()
        val member = addedMember ?: return
        clusterClient.removeMember(member.id).get()
        val newCount = clusterClient.listMember().get().membersCount
        assertion.assertEquals(newCount, 3, "delete added member (${member.id}), and left $newCount members.")
    }
}