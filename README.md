# capacitor-plugin-datecs-printer

Capacitor port of [cordova-plugin-datecs-printer](https://github.com/giorgiofellipe/cordova-plugin-datecs-printer/tree/master)

## Install

```bash
npm install capacitor-plugin-datecs-printer
npx cap sync
```

## API

<docgen-index>

* [`listBluetoothDevices()`](#listbluetoothdevices)
* [`connect(...)`](#connect)
* [`disconnect()`](#disconnect)
* [`feedPaper(...)`](#feedpaper)
* [`printText(...)`](#printtext)
* [`printSelfTest()`](#printselftest)
* [`getStatus()`](#getstatus)
* [`getTemperature()`](#gettemperature)
* [`setBarcode(...)`](#setbarcode)
* [`printBarcode(...)`](#printbarcode)
* [`printQRCode(...)`](#printqrcode)
* [`printImage(...)`](#printimage)
* [`drawPageRectangle(...)`](#drawpagerectangle)
* [`drawPageFrame(...)`](#drawpageframe)
* [`selectPageMode()`](#selectpagemode)
* [`selectStandardMode()`](#selectstandardmode)
* [`setPageRegion(...)`](#setpageregion)
* [`printPage()`](#printpage)
* [`write(...)`](#write)
* [`writeHex(...)`](#writehex)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### listBluetoothDevices()

```typescript
listBluetoothDevices() => Promise<{ devices: { name: string; address: string; aliasName: string; type: number; }[]; }>
```

**Returns:** <code>Promise&lt;{ devices: { name: string; address: string; aliasName: string; type: number; }[]; }&gt;</code>

--------------------


### connect(...)

```typescript
connect(options: { address: string; }) => Promise<void>
```

| Param         | Type                              |
| ------------- | --------------------------------- |
| **`options`** | <code>{ address: string; }</code> |

--------------------


### disconnect()

```typescript
disconnect() => Promise<void>
```

--------------------


### feedPaper(...)

```typescript
feedPaper(options: { lines: number; }) => Promise<void>
```

| Param         | Type                            |
| ------------- | ------------------------------- |
| **`options`** | <code>{ lines: number; }</code> |

--------------------


### printText(...)

```typescript
printText(options: { text: string; charset: string; }) => Promise<void>
```

| Param         | Type                                            |
| ------------- | ----------------------------------------------- |
| **`options`** | <code>{ text: string; charset: string; }</code> |

--------------------


### printSelfTest()

```typescript
printSelfTest() => Promise<void>
```

--------------------


### getStatus()

```typescript
getStatus() => Promise<{ status: number; }>
```

**Returns:** <code>Promise&lt;{ status: number; }&gt;</code>

--------------------


### getTemperature()

```typescript
getTemperature() => Promise<{ temperature: number; }>
```

**Returns:** <code>Promise&lt;{ temperature: number; }&gt;</code>

--------------------


### setBarcode(...)

```typescript
setBarcode(options: { align: number; small: boolean; scale: number; hri: number; height: number; }) => Promise<void>
```

| Param         | Type                                                                                        |
| ------------- | ------------------------------------------------------------------------------------------- |
| **`options`** | <code>{ align: number; small: boolean; scale: number; hri: number; height: number; }</code> |

--------------------


### printBarcode(...)

```typescript
printBarcode(options: { type: number; data: string; }) => Promise<void>
```

| Param         | Type                                         |
| ------------- | -------------------------------------------- |
| **`options`** | <code>{ type: number; data: string; }</code> |

--------------------


### printQRCode(...)

```typescript
printQRCode(options: { size: number; eccLv: number; data: string; }) => Promise<void>
```

| Param         | Type                                                        |
| ------------- | ----------------------------------------------------------- |
| **`options`** | <code>{ size: number; eccLv: number; data: string; }</code> |

--------------------


### printImage(...)

```typescript
printImage(options: { image: string; width: number; height: number; align: number; }) => Promise<void>
```

| Param         | Type                                                                          |
| ------------- | ----------------------------------------------------------------------------- |
| **`options`** | <code>{ image: string; width: number; height: number; align: number; }</code> |

--------------------


### drawPageRectangle(...)

```typescript
drawPageRectangle(options: { x: number; y: number; width: number; height: number; fillMode: number; }) => Promise<void>
```

| Param         | Type                                                                                    |
| ------------- | --------------------------------------------------------------------------------------- |
| **`options`** | <code>{ x: number; y: number; width: number; height: number; fillMode: number; }</code> |

--------------------


### drawPageFrame(...)

```typescript
drawPageFrame(options: { x: number; y: number; width: number; height: number; fillMode: number; thickness: number; }) => Promise<void>
```

| Param         | Type                                                                                                       |
| ------------- | ---------------------------------------------------------------------------------------------------------- |
| **`options`** | <code>{ x: number; y: number; width: number; height: number; fillMode: number; thickness: number; }</code> |

--------------------


### selectPageMode()

```typescript
selectPageMode() => Promise<void>
```

--------------------


### selectStandardMode()

```typescript
selectStandardMode() => Promise<void>
```

--------------------


### setPageRegion(...)

```typescript
setPageRegion(options: { x: number; y: number; width: number; height: number; direction: number; }) => Promise<void>
```

| Param         | Type                                                                                     |
| ------------- | ---------------------------------------------------------------------------------------- |
| **`options`** | <code>{ x: number; y: number; width: number; height: number; direction: number; }</code> |

--------------------


### printPage()

```typescript
printPage() => Promise<void>
```

--------------------


### write(...)

```typescript
write(options: { bytes: string; }) => Promise<void>
```

| Param         | Type                            |
| ------------- | ------------------------------- |
| **`options`** | <code>{ bytes: string; }</code> |

--------------------


### writeHex(...)

```typescript
writeHex(options: { hex: string; }) => Promise<void>
```

| Param         | Type                          |
| ------------- | ----------------------------- |
| **`options`** | <code>{ hex: string; }</code> |

--------------------

</docgen-api>
