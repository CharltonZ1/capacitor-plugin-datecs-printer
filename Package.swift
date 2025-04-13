// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "CapacitorPluginDatecsPrinter",
    platforms: [.iOS(.v14)],
    products: [
        .library(
            name: "CapacitorPluginDatecsPrinter",
            targets: ["DatecsPrinterPlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor-swift-pm.git", from: "7.0.0")
    ],
    targets: [
        .target(
            name: "DatecsPrinterPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor-swift-pm"),
                .product(name: "Cordova", package: "capacitor-swift-pm")
            ],
            path: "ios/Sources/DatecsPrinterPlugin"),
        .testTarget(
            name: "DatecsPrinterPluginTests",
            dependencies: ["DatecsPrinterPlugin"],
            path: "ios/Tests/DatecsPrinterPluginTests")
    ]
)