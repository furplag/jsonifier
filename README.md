# jsonifier

[![Build Status](https://travis-ci.org/furplag/jsonifier.svg?branch=master)](https://travis-ci.org/furplag/jsonifier)
[![codebeat badge](https://codebeat.co/badges/342132b8-3920-476f-a5b7-5c5b32be1d6a)](https://codebeat.co/projects/github-com-furplag-jsonifier-master)

Utilities for convert between Object and JSON.

## Getting Start
Add the following snippet to any project's pom that depends on your project
```xml
<repositories>
  ...
  <repository>
    <id>jsonifier</id>
    <url>https://raw.github.com/furplag/jsonifier/mvn-repo/</url>
    <snapshots>
      <enabled>true</enabled>
      <updatePolicy>always</updatePolicy>
    </snapshots>
  </repository>
</repositories>
...
<dependencies>
  ...
  <dependency>
    <groupId>jp.furplag.sandbox.java.util</groupId>
    <artifactId>jsonifier</artifactId>
    <version>[1.0,)</version>
  </dependency>
</dependencies>
```

## License
Code is under the [Apache Licence v2](LICENCE).
