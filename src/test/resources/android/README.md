# Where is this data sourced from?

- `attrs.xml` and `attrs_manifest.xml`: Local Android SDK installation.
   - Path: `Android\Sdk\platforms\android-N\data\res\values`
- `res-map.txt`: Derived from Local Android SDK installation, converted using JADX tooling
   - Path: `Android\Sdk\platforms\android-N\android.jar:resources.arsc`
   - Tooling: https://github.com/skylot/jadx/blob/master/jadx-cli/src/main/java/jadx/cli/tools/ConvertArscFile.java