# Binary Resources

A fork of the upstream [binary-resources](https://android.googlesource.com/platform/tools/base/+/refs/heads/mirror-goog-studio-main/apkparser/binary-resources/) tool project.

## What's changed?

- No longer requires/depends-on the Android SDK artifacts
- Helper utilities for printing binary XML
- Obfuscation resilience, handling inputs that would otherwise crash the base project, but are valid at install-time