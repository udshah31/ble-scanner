package com.udaysah.blescanner.android.di

import com.udaysah.blescanner.android.viewmodel.BleViewModel
import com.udaysah.blescanner.scanner.AndroidBleScanner
import com.udaysah.blescanner.scanner.BleScanner
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val bleModule = module {
    single { AndroidBleScanner(androidContext()) } bind BleScanner::class
    viewModelOf(::BleViewModel)
}
