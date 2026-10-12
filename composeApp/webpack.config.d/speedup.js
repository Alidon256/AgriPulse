// Optimize Webpack build speed for Compose Multiplatform Kotlin/JS
// Disables heavy Terser minification and source maps which cause Webpack to hang/take over 30 minutes on large Kotlin IR bundles.

if (config.optimization) {
    config.optimization.minimize = false;
}
config.devtool = false;
