# jsonifier

[![Build Status](https://travis-ci.org/furplag/jsonifier.svg?branch=master)](https://travis-ci.org/furplag/jsonifier)
[![Coverage Status](https://coveralls.io/repos/github/furplag/jsonifier/badge.svg?branch=master)](https://coveralls.io/github/furplag/jsonifier?branch=master)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/c187120b8db34e6db8a773330b899c0c)](https://www.codacy.com/app/furplag/jsonifier?utm_source=github.com&utm_medium=referral&utm_content=furplag/jsonifier&utm_campaign=badger)
[![codebeat badge](https://codebeat.co/badges/342132b8-3920-476f-a5b7-5c5b32be1d6a)](https://codebeat.co/projects/github-com-furplag-jsonifier-master)
[![Maintainability](https://api.codeclimate.com/v1/badges/8d432f5ace747151d1ed/maintainability)](https://codeclimate.com/github/furplag/jsonifier/maintainability)

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
