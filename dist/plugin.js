var capacitorDatecsPrinter = (function (exports, core) {
    'use strict';

    const DatecsPrinter = core.registerPlugin('DatecsPrinter', {
        web: () => Promise.resolve().then(function () { return web; }).then((m) => new m.DatecsPrinterWeb()),
    });

    class DatecsPrinterWeb extends core.WebPlugin {
        // async isSupported(): Promise<{ supported: boolean }> {
        //   return { supported: false };
        // }
        async listBluetoothDevices() {
            throw this.unimplemented('Not supported on web.');
        }
        async connect(_options) {
            throw this.unimplemented('Not supported on web.');
        }
        async disconnect() {
            throw this.unimplemented('Not supported on web.');
        }
        async feedPaper(_options) {
            throw this.unimplemented('Not supported on web.');
        }
        async printText(_options) {
            throw this.unimplemented('Not supported on web.');
        }
        async printSelfTest() {
            throw this.unimplemented('Not supported on web.');
        }
        async getStatus() {
            throw this.unimplemented('Not supported on web.');
        }
        async getTemperature() {
            throw this.unimplemented('Not supported on web.');
        }
        async setBarcode(_options) {
            throw this.unimplemented('Not supported on web.');
        }
        async printBarcode(_options) {
            throw this.unimplemented('Not supported on web.');
        }
        async printQRCode(_options) {
            throw this.unimplemented('Not supported on web.');
        }
        async printImage(_options) {
            throw this.unimplemented('Not supported on web.');
        }
        async drawPageRectangle(_options) {
            throw this.unimplemented('Not supported on web.');
        }
        async drawPageFrame(_options) {
            throw this.unimplemented('Not supported on web.');
        }
        async selectPageMode() {
            throw this.unimplemented('Not supported on web.');
        }
        async selectStandardMode() {
            throw this.unimplemented('Not supported on web.');
        }
        async setPageRegion(_options) {
            throw this.unimplemented('Not supported on web.');
        }
        async printPage() {
            throw this.unimplemented('Not supported on web.');
        }
        async write(_options) {
            throw this.unimplemented('Not supported on web.');
        }
        async writeHex(_options) {
            throw this.unimplemented('Not supported on web.');
        }
    }

    var web = /*#__PURE__*/Object.freeze({
        __proto__: null,
        DatecsPrinterWeb: DatecsPrinterWeb
    });

    exports.DatecsPrinter = DatecsPrinter;

    return exports;

})({}, capacitorExports);
//# sourceMappingURL=plugin.js.map
