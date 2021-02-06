package com.xxxsarutahikoxxx.kotlin.SocketRunner

import com.xxxsarutahikoxxx.kotlin.Utilitys.out
import java.util.UUID
import javax.bluetooth.*


fun main(args: Array<String>) {
    //
    // [Device : d-01J] に対して [TEST_UUID] でクライアントとして接続する
    //

    val deviceName = "d-01J"

    ClientBluetoothRunner(
        BluetoothRunner.device { it.getFriendlyName(true) == deviceName },
        BluetoothRunner.BLUETOOTH_RUNNER_TEST_UUID
    ).apply {
        connect()
    }
}