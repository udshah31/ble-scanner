package com.udaysah.blescanner.android.viewmodel

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import assertk.assertions.isEqualTo
import com.udaysah.blescanner.android.fakes.FakeBleScanner
import com.udaysah.blescanner.model.BleDevice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BleViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var scanner: FakeBleScanner
    private lateinit var viewModel: BleViewModel

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        scanner = FakeBleScanner()
        viewModel = BleViewModel(scanner)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state has no devices, not scanning, no permissions`() = runTest {
        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.devices).isEmpty()
            assertThat(state.isScanning).isFalse()
            assertThat(state.permissionsGranted).isFalse()
            assertThat(state.permissionsDenied).isFalse()
        }
    }

    @Test
    fun `permissions granted updates state and starts scan`() = runTest {
        viewModel.state.test {
            awaitItem() // initial state

            viewModel.onAction(BleScannerAction.OnPermissionsResult(
                mapOf("android.permission.BLUETOOTH_SCAN" to true, "android.permission.BLUETOOTH_CONNECT" to true)
            ))

            val state = awaitItem()
            assertThat(state.permissionsGranted).isTrue()
            assertThat(state.permissionsDenied).isFalse()
            assertThat(scanner.startScanCallCount).isEqualTo(1)
        }
    }

    @Test
    fun `permissions denied updates state and does not start scan`() = runTest {
        viewModel.state.test {
            awaitItem() // initial state

            viewModel.onAction(BleScannerAction.OnPermissionsResult(
                mapOf("android.permission.BLUETOOTH_SCAN" to false, "android.permission.BLUETOOTH_CONNECT" to true)
            ))

            val state = awaitItem()
            assertThat(state.permissionsGranted).isFalse()
            assertThat(state.permissionsDenied).isTrue()
            assertThat(scanner.startScanCallCount).isEqualTo(0)
        }
    }

    @Test
    fun `empty permissions map is treated as denied`() = runTest {
        viewModel.state.test {
            awaitItem() // initial state

            viewModel.onAction(BleScannerAction.OnPermissionsResult(emptyMap()))

            val state = awaitItem()
            assertThat(state.permissionsGranted).isFalse()
            assertThat(state.permissionsDenied).isTrue()
            assertThat(scanner.startScanCallCount).isEqualTo(0)
        }
    }

    @Test
    fun `OnToggleScan starts scan when not scanning`() = runTest {
        viewModel.onAction(BleScannerAction.OnToggleScan)
        assertThat(scanner.startScanCallCount).isEqualTo(1)
        assertThat(scanner.stopScanCallCount).isEqualTo(0)
    }

    @Test
    fun `OnToggleScan stops scan when already scanning`() = runTest {
        scanner.startScan() // put scanner in scanning state
        viewModel.state.test {
            awaitItem() // wait for isScanning = true to propagate

            viewModel.onAction(BleScannerAction.OnToggleScan)

            assertThat(scanner.stopScanCallCount).isEqualTo(1)
        }
    }

    @Test
    fun `scanner device updates flow into state`() = runTest {
        val devices = listOf(
            BleDevice("Headphones", "AA:BB:CC:DD:EE:FF", -60),
            BleDevice(null, "11:22:33:44:55:66", -85)
        )

        viewModel.state.test {
            awaitItem() // initial empty state

            scanner.emitDevices(devices)

            val state = awaitItem()
            assertThat(state.devices).isEqualTo(devices)
        }
    }

    @Test
    fun `onCleared stops the scanner`() = runTest {
        scanner.startScan()
        viewModel.onCleared()
        assertThat(scanner.stopScanCallCount).isEqualTo(1)
    }
}
