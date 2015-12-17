# Violation Comments to Stash Plugin

Changelog of Violation Comments to Stash Plugin

# 1.10
* Making it applicable for all projects, not just free-style.

# 1.9
* Adding header "X-Atlassian-Token: no-check" to be compatible with Stash 4.0 #12

# 1.8
* Accepting cookies sent from Stash server. To prevent infinite redirect if Stash requires authentication.

# 1.7
* Using Violations 0.8.0-alpha-1
 * Adds new parsers: PyFlakes, ReSharper, XMLLint, ZPTLint
 * Adds messages to Findbugs
 * Bug fixes in parsers, see Violations Plugin changelog
 
# 1.6
* Replacing back-slashes (Windows style file paths) from reports with forward-slashes. So that they match file paths reported in Stash Rest API.
* Using password field for password in configuration GUI

# 1.5
* Less logging in build log

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
