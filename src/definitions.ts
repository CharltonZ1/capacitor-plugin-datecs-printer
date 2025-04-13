import type { Plugin } from '@capacitor/core';

export interface DatecsPrinterPlugin extends Plugin {
  // isSupported(): Promise<{ supported: boolean }>;
  listBluetoothDevices(): Promise<{ devices: { name: string; address: string; aliasName: string; type: number }[] }>;
  connect(options: { address: string }): Promise<void>;
  disconnect(): Promise<void>;
  feedPaper(options: { lines: number }): Promise<void>;
  printText(options: { text: string; charset: string }): Promise<void>;
  printSelfTest(): Promise<void>;
  getStatus(): Promise<{ status: number }>;
  getTemperature(): Promise<{ temperature: number }>;
  setBarcode(options: { align: number; small: boolean; scale: number; hri: number; height: number }): Promise<void>;
  printBarcode(options: { type: number; data: string }): Promise<void>;
  printQRCode(options: { size: number; eccLv: number; data: string }): Promise<void>;
  printImage(options: { image: string; width: number; height: number; align: number }): Promise<void>;
  drawPageRectangle(options: { x: number; y: number; width: number; height: number; fillMode: number }): Promise<void>;
  drawPageFrame(options: { x: number; y: number; width: number; height: number; fillMode: number; thickness: number }): Promise<void>;
  selectPageMode(): Promise<void>;
  selectStandardMode(): Promise<void>;
  setPageRegion(options: { x: number; y: number; width: number; height: number; direction: number }): Promise<void>;
  printPage(): Promise<void>;
  write(options: { bytes: string }): Promise<void>;
  writeHex(options: { hex: string }): Promise<void>;
}