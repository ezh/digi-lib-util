digi-lib-util [![Build Status](https://travis-ci.org/ezh/digi-lib-util.png?branch=master)](https://travis-ci.org/ezh/digi-lib-util) [![Coverage Status](https://coveralls.io/repos/ezh/digi-lib-util/badge.png?branch=master)](https://coveralls.io/r/ezh/digi-lib-util?branch=master)
=============

Utility module of all Digi applications and libraries, containing various common routines

If you want improve it, please send request to _ezh_ at _ezh.msk.ru_. You will be given the permissions to the repository. Please, feel free to add yourself to authors.

DOCUMENTATION
-------------

### Setup

Add Maven or Ivy repository:

```scala
resolvers += "digimead-maven" at "http://storage.googleapis.com/maven.repository.digimead.org/"
```

```scala
resolvers += Resolver.url("digimead-ivy", url("http://storage.googleapis.com/ivy.repository.digimead.org/"))(Resolver.defaultIvyPatterns)
```

Add dependency:

```scala
libraryDependencies += "org.digimead" %% "digi-lib-util" % "VERSION"
```

### [API (latest version)](http://ezh.github.com/digi-lib-util/api/)

## Target platform

* Scala 2.10.1 (request for more if needed)
* JVM 1.6+
* The library dependencies are [Digi-Lib](https://github.com/ezh/digi-lib) and [SLF4J](http://www.slf4j.org/)

## Participate in the development ##

Branches:

* origin/master reflects a production-ready state
* origin/release-* support preparation of a new production release. Allow for last-minute dotting of i’s and crossing t’s
* origin/hotfix-* support preparation of a new unplanned production release
* origin/develop reflects a state with the latest delivered development changes for the next release (nightly builds)
* origin/feature-* new features for the upcoming or a distant future release

Structure of branches follow strategy of http://nvie.com/posts/a-successful-git-branching-model/

If you will create new origin/feature-* please open feature request for yourself.

* Anyone may comment you feature here.
* We will have a history for feature and ground for documentation
* If week passed and there wasn't any activity + all tests passed = release a new version ;-)

AUTHORS
-------

* Alexey Aksenov

LICENSE
-------

The Digi-Lib-Util Project is licensed to you under the terms of
the Apache License, version 2.0, a copy of which has been
included in the LICENSE file.
Please check the individual source files for details.

Copyright
---------

Copyright ©  2011-2013 Alexey B. Aksenov/Ezh. All rights reserved.
