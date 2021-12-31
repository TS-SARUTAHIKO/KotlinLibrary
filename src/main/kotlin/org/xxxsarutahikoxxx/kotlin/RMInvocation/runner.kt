package org.xxxsarutahikoxxx.kotlin.RMInvocation

import org.xxxsarutahikoxxx.kotlin.IORunner.*
import java.io.Serializable
import javax.bluetooth.RemoteDevice


open class RMIHostWebRunner(
    port : Int,
    isAutoReconnect : Boolean = true,
    isAutoReader: Boolean = true
) : HostTCPRunner(port, isAutoReconnect, isAutoReader), RMIHostReactor {
    override var registry: RMIHostRegistry? = null

    override fun exportRetMessage(message: RetMessage) {
        writeObject(message)
    }
    override fun onAccept(obj: Serializable) {
        when( obj ){
            is RMInvocation -> onAccept(obj)
        }
    }
}
open class RMIClientWebRunner(
    address : String = "localhost",
    port : Int,
    isAutoReconnect : Boolean = true,
    isAutoReader: Boolean = true
) : ClientTCPRunner(address, port, isAutoReconnect, isAutoReader), RMIClientReactor {
    override var onAccept: ((RetMessage) -> Unit)? = null

    override fun exportRMI(rmi: RMInvocation) {
        writeObject(rmi)
    }
    override fun onAccept(obj: Serializable) {
        when( obj ){
            is RetMessage -> onAccept?.invoke(obj)
        }
    }
}

open class RMIHostBluetoothRunner(
    uuid : String = BLUETOOTH_RUNNER_TEST_UUID,
    isAutoReconnect : Boolean = true,
    isAutoReader: Boolean = true
) : HostBluetoothRunner(uuid, isAutoReconnect, isAutoReader), RMIHostReactor {
    override var registry: RMIHostRegistry? = null

    override fun exportRetMessage(message: RetMessage) {
        writeObject(message)
    }
    override fun onAccept(obj: Serializable) {
        when( obj ){
            is RMInvocation -> onAccept(obj)
        }
    }
}
open class RMIClientBluetoothRunner(
    device : RemoteDevice,
    uuid : String = BLUETOOTH_RUNNER_TEST_UUID,
    isAutoReconnect : Boolean = true,
    isAutoReader: Boolean = true
) : ClientBluetoothRunner(device, uuid, isAutoReconnect, isAutoReader), RMIClientReactor {
    override var onAccept: ((RetMessage) -> Unit)? = null

    override fun exportRMI(rmi: RMInvocation) {
        writeObject(rmi)
    }
    override fun onAccept(obj: Serializable) {
        when( obj ){
            is RetMessage -> onAccept?.invoke(obj)
        }
    }
}


fun RMIHostRegistry.openWebPort(port : Int, isAutoReconnect : Boolean = true){
    RMIHostWebRunner(port, isAutoReconnect).let {
        addReactor(it)
        it.open()
    }
}
fun RMIClientRegistry.connectWebPort(address : String = "localhost", port : Int, isAutoReconnect : Boolean = true){
    RMIClientWebRunner(address, port, isAutoReconnect).let {
        setReactor(it)
        it.connect()
    }
}

fun RMIHostRegistry.openBluetoothPort(uuid : String = BluetoothRunner.BLUETOOTH_RUNNER_TEST_UUID, isAutoReconnect : Boolean = true){
    RMIHostBluetoothRunner(uuid, isAutoReconnect).let {
        addReactor(it)
        it.open()
    }
}
fun RMIClientRegistry.connectBluetoothPort(device : RemoteDevice, uuid : String = BluetoothRunner.BLUETOOTH_RUNNER_TEST_UUID, isAutoReconnect : Boolean = true){
    RMIClientBluetoothRunner(device, uuid, isAutoReconnect).let {
        setReactor(it)
        it.connect()
    }
}