# Binary Resources [![badge](https://jitpack.io/v/Col-E/binary-resources.svg)](https://jitpack.io/#Col-E/binary-resources/)

A fork of the upstream [binary-resources](https://android.googlesource.com/platform/tools/base/+/refs/heads/mirror-goog-studio-main/apkparser/binary-resources/) tool project.

## What's changed?

- No longer requires/depends-on the Android SDK artifacts
- Helper utilities for printing binary XML
- Obfuscation resilience, handling inputs that would otherwise crash the base project, but are valid at install-time

## Usage as a library

Here's a code sample of taking the `byte[]` of an `AndroidManifest.xml` and transforming it into a formatted `String`.
```java
// Create resource providers, which translate resource id values/keys into strings used in XML decoding
//  - Implementing this interface is your responsibility. 
//  - While this is a bit of work, it allows you to update what values are provided even if this project never updates.
//  - An example implementation is provided in this projects test module.
AndroidResourceProvider androidResources = ...
AndroidResourceProvider appResources = ...

// Create the binary resource (AndroidManifest.xml) reader and pass it to the XML decoder, 
//  yielding a decoded string representation of the file contents.
BinaryResourceFile binaryResource = new BinaryResourceFile(binaryXmlBytes);
String decoded = XmlDecoder.decode(binaryResource, androidResources, appResources);
```
The example implementation of `AndroidResourceProvider` can be found here:
 - [`AndroidResourceProviderImpl.java`](src/test/java/software/coley/androidres/AndroidResourceProviderImpl.java)
 - The data it pulls from can be found in [`src/test/resources/android`](src/test/resources/android)

You can use the project as a maven artifact via [JitPack](https://jitpack.io/#Col-E/binary-resources/)