# jsonifier
[![Build Status](https://github.com/furplag/jsonifier/actions/workflows/maven.yml/badge.svg?branch=master)](https://github.com/furplag/jsonifier/actions/workflows/maven.yml)
[![CodeQL Status](https://github.com/furplag/jsonifier/actions/workflows/codeql.yml/badge.svg?branch=master&event=push)](https://github.com/furplag/jsonifier/actions/workflows/codeql.yml)
[![Coverage Status](https://coveralls.io/repos/github/furplag/jsonifier/badge.svg?branch=master)](https://coveralls.io/github/furplag/jsonifier?branch=master)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/8add3a6d45dc4871983e5a2d91497d87)](https://app.codacy.com/gh/furplag/jsonifier/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=furplag/jsonifier&amp;utm_campaign=Badge_Grade)
[![codebeat badge](https://codebeat.co/badges/342132b8-3920-476f-a5b7-5c5b32be1d6a)](https://codebeat.co/projects/github-com-furplag-jsonifier-master)
[![Maintainability](https://api.codeclimate.com/v1/badges/8d432f5ace747151d1ed/maintainability)](https://codeclimate.com/github/furplag/jsonifier/maintainability)

utilities for convert between Object and JSON .

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
    <groupId>jp.furplag.sandbox</groupId>
    <artifactId>jsonifier</artifactId>
    <version>4.0.1</version>
  </dependency>
</dependencies>
```

## Motivation
* do more easier duplicate object .
* against to many of warnings like that "too many iVars", on code .
* a brutal modifying on the immutable one .

## Dependencies
* [jakson](https://github.com/FasterXML/jackson)
* [relic](https://github.com/furplag/relic)

## License
Code is under the [Apache Licence v2](LICENCE).
