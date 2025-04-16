import { WebPlugin } from '@capacitor/core';
import type { DatecsPrinterPlugin } from './definitions';
export declare class DatecsPrinterWeb extends WebPlugin implements DatecsPrinterPlugin {
    listBluetoothDevices(): Promise<{
        devices: {
            name: string;
            address: string;
            aliasName: string;
            type: number;
        }[];
    }>;
    connect(_options: {
        address: string;
    }): Promise<void>;
    disconnect(): Promise<void>;
    feedPaper(_options: {
        lines: number;
    }): Promise<void>;
    printText(_options: {
        text: string;
        charset: string;
    }): Promise<void>;
    printSelfTest(): Promise<void>;
    getStatus(): Promise<{
        status: number;
    }>;
    getTemperature(): Promise<{
        temperature: number;
    }>;
    setBarcode(_options: {
        align: number;
        small: boolean;
        scale: number;
        hri: number;
        height: number;
    }): Promise<void>;
    printBarcode(_options: {
        type: number;
        data: string;
    }): Promise<void>;
    printQRCode(_options: {
        size: number;
        eccLv: number;
        data: string;
    }): Promise<void>;
    printImage(_options: {
        image: string;
        width: number;
        height: number;
        align: number;
    }): Promise<void>;
    drawPageRectangle(_options: {
        x: number;
        y: number;
        width: number;
        height: number;
        fillMode: number;
    }): Promise<void>;
    drawPageFrame(_options: {
        x: number;
        y: number;
        width: number;
        height: number;
        fillMode: number;
        thickness: number;
    }): Promise<void>;
    selectPageMode(): Promise<void>;
    selectStandardMode(): Promise<void>;
    setPageRegion(_options: {
        x: number;
        y: number;
        width: number;
        height: number;
        direction: number;
    }): Promise<void>;
    printPage(): Promise<void>;
    write(_options: {
        bytes: string;
    }): Promise<void>;
    writeHex(_options: {
        hex: string;
    }): Promise<void>;
}
