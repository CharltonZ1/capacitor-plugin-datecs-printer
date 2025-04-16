import { registerPlugin } from '@capacitor/core';
const DatecsPrinter = registerPlugin('DatecsPrinter', {
    web: () => import('./web').then((m) => new m.DatecsPrinterWeb()),
});
export * from './definitions';
export { DatecsPrinter };
//# sourceMappingURL=index.js.map