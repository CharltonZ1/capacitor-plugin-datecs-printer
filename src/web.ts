import { WebPlugin } from '@capacitor/core';

import type { DatecsPrinterPlugin } from './definitions';

export class DatecsPrinterWeb extends WebPlugin implements DatecsPrinterPlugin {
  
  // async isSupported(): Promise<{ supported: boolean }> {
  //   return { supported: false };
  // }

  async listBluetoothDevices(): Promise<{ devices: { name: string; address: string; aliasName: string; type: number }[] }> {
    throw this.unimplemented('Not supported on web.');
  }

  async connect(_options: { address: string }): Promise<void> {
    throw this.unimplemented('Not supported on web.');
  }

  async disconnect(): Promise<void> {
    throw this.unimplemented('Not supported on web.');
  }

  async feedPaper(_options: { lines: number }): Promise<void> {
    throw this.unimplemented('Not supported on web.');
  }

  async printText(_options: { text: string; charset: string }): Promise<void> {
    throw this.unimplemented('Not supported on web.');
  }

  async printSelfTest(): Promise<void> {
    throw this.unimplemented('Not supported on web.');
  }

  async getStatus(): Promise<{ status: number }> {
    throw this.unimplemented('Not supported on web.');
  }

  async getTemperature(): Promise<{ temperature: number }> {
    throw this.unimplemented('Not supported on web.');
  }

  async setBarcode(_options: { align: number; small: boolean; scale: number; hri: number; height: number }): Promise<void> {
    throw this.unimplemented('Not supported on web.');
  }

  async printBarcode(_options: { type: number; data: string }): Promise<void> {
    throw this.unimplemented('Not supported on web.');
  }

  async printQRCode(_options: { size: number; eccLv: number; data: string }): Promise<void> {
    throw this.unimplemented('Not supported on web.');
  }

  async printImage(_options: { image: string; width: number; height: number; align: number }): Promise<void> {
    throw this.unimplemented('Not supported on web.');
  }

  async drawPageRectangle(_options: { x: number; y: number; width: number; height: number; fillMode: number }): Promise<void> {
    throw this.unimplemented('Not supported on web.');
  }

  async drawPageFrame(_options: { x: number; y: number; width: number; height: number; fillMode: number; thickness: number }): Promise<void> {
    throw this.unimplemented('Not supported on web.');
  }

  async selectPageMode(): Promise<void> {
    throw this.unimplemented('Not supported on web.');
  }

  async selectStandardMode(): Promise<void> {
    throw this.unimplemented('Not supported on web.');
  }

  async setPageRegion(_options: { x: number; y: number; width: number; height: number; direction: number }): Promise<void> {
    throw this.unimplemented('Not supported on web.');
  }

  async printPage(): Promise<void> {
    throw this.unimplemented('Not supported on web.');
  }

  async write(_options: { bytes: string }): Promise<void> {
    throw this.unimplemented('Not supported on web.');
  }

  async writeHex(_options: { hex: string }): Promise<void> {
    throw this.unimplemented('Not supported on web.');
  }
}