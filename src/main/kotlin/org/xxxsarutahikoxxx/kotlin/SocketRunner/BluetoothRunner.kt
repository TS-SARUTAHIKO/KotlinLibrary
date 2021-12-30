package org.xxxsarutahikoxxx.kotlin.SocketRunner

import org.xxxsarutahikoxxx.kotlin.Utilitys.out
import javax.bluetooth.*
import javax.microedition.io.Connector
import javax.microedition.io.StreamConnection
import javax.microedition.io.StreamConnectionNotifier


abstract class BluetoothRunner(
    val device : RemoteDevice?,
    val UUID : String,

    hostMode : Boolean,
    isAutoReconnect : Boolean,
    isAutoReader : Boolean
) : SocketRunner(hostMode, isAutoReconnect, isAutoReader) {

    var connection : StreamConnection? = null

    override fun open() {
        out = "Accept Connection..."

        val server : StreamConnectionNotifier = Connector.open(
            "btspp://localhost:" + UUID.replace("-", ""),
            Connector.READ_WRITE,
            true
        ) as StreamConnectionNotifier

//        val record = LocalDevice.getLocalDevice().getRecord(server)
//        LocalDevice.getLocalDevice().updateRecord(record)

        connection = server.acceptAndOpen()
        server.close()

        out = "Opened : $connection"
        onOpened(connection!!.openInputStream(), connection!!.openOutputStream())
    }
    override fun connect() {
        out = "Connecting..."

        val localDevice = LocalDevice.getLocalDevice()
        val agent = localDevice.discoveryAgent

        val lock = Object()
        var connectionURL : String? = null

        agent.searchServices(null, arrayOf(UUID(UUID.replace("-", ""), false)), device,
            object : DiscoveryListener {
                override fun deviceDiscovered(btDevice: RemoteDevice, cod: DeviceClass?) {}
                override fun inquiryCompleted(discType: Int) {}

                override fun servicesDiscovered(transID: Int, servRecord: Array<out ServiceRecord>) {
                    connectionURL = servRecord[0].getConnectionURL(0, false)
                }
                override fun serviceSearchCompleted(transID: Int, respCode: Int) {
                    synchronized(lock){ lock.notify() }
                }
            }
        )

        try {
            synchronized(lock) { lock.wait() }
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        connection = Connector.open(connectionURL) as StreamConnection

        out = "Connected : $connection"
        onConnected(connection!!.openInputStream(), connection!!.openOutputStream())
    }

    override fun close() {
        super.close()

        connection?.close()
    }


    companion object {
        internal const val BLUETOOTH_RUNNER_TEST_UUID = "3f9a0034-77f0-4c02-a3aa-8613ce431352"

        /** ホストに登録されたデバイスを取得します。初期化時にかなりの時間がかかるので注意。 */
        val devices by lazy { inquiryDevices() }
        /** ホストにおけるデバイスの検索関数。かなり時間がかかる。参照だけなら[devices]を用いること。 */
        fun inquiryDevices() : MutableList<RemoteDevice> {
            val lock = Object()

            val localDevice = LocalDevice.getLocalDevice()
            val agent = localDevice.discoveryAgent

            val devices : MutableList<RemoteDevice> = mutableListOf()

            agent.startInquiry(DiscoveryAgent.GIAC, object : DiscoveryListener{
                override fun servicesDiscovered(transID: Int, servRecord: Array<out ServiceRecord>?) {}
                override fun serviceSearchCompleted(transID: Int, respCode: Int) {}

                override fun deviceDiscovered(btDevice: RemoteDevice, cod: DeviceClass) {
                    btDevice.let { devices.add(it) }
                }
                override fun inquiryCompleted(discType: Int) {
                    synchronized(lock){ lock.notify() }
                }
            })

            try {
                synchronized(lock) { lock.wait() }
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

            return devices
        }

        /** 初回起動時に非常に時間がかかる。デバイスが追加された場合は[BluetoothRunner.Companion.devices]を更新しないと反映されない。 */
        fun device( filter : (RemoteDevice)->(Boolean) ) : RemoteDevice = devices.first(filter)
    }
}

open class HostBluetoothRunner(
    uuid : String = BLUETOOTH_RUNNER_TEST_UUID,
    isAutoReconnect : Boolean = true,
    isAutoReader: Boolean = true
) : BluetoothRunner(null, uuid, true, isAutoReconnect, isAutoReader) {
    @Deprecated("HostRunner can open, can't connect")
    override fun connect() = throw RuntimeException("HostWebRunner can open, can't connect")

    companion object {
        /** 初回起動時に非常に時間がかかる。デバイスが追加された場合は[BluetoothRunner.Companion.devices]を更新しないと反映されない。 */
        fun device( filter : (RemoteDevice)->(Boolean) ) : RemoteDevice = devices.first(filter)
    }
}

open class ClientBluetoothRunner(
    device : RemoteDevice,
    uuid : String = BLUETOOTH_RUNNER_TEST_UUID,
    isAutoReconnect : Boolean = true,
    isAutoReader: Boolean = true
) : BluetoothRunner(device, uuid, false, isAutoReconnect, isAutoReader) {
    @Deprecated("ClientRunner can connect, can't open")
    override fun open() = throw RuntimeException("ClientWebRunner can connect, can't open")

    companion object {
        /** 初回起動時に非常に時間がかかる。デバイスが追加された場合は[BluetoothRunner.Companion.devices]を更新しないと反映されない。 */
        fun device( filter : (RemoteDevice)->(Boolean) ) : RemoteDevice = devices.first(filter)
    }
}