# Violation Comments to Stash Plugin

Changelog of Violation Comments to Stash Plugin

# 1.4
* Finding all violations for files. Even if same file is reported twice (with absolute and relative path).

## 1.3
* Changed file in Stash may also end with reported file, not only the opposite. Findbugs reports Java-files as package path, this makes sure those files are matched to changed files in Stash.

## 1.2
* Can, optionally, add comments to individual commits. And/Or to pull requests.

## 1.1
* Adding limit parameter to changes request in Stash Client. So that all files in PR gets commented.

## 1.0
* Initial Release
 * Alot of use cases are untested and any user should expect to find issues. But since I do not have time for testing everything, I'll release 1.0 "as is"! Please report any issues you find =)
