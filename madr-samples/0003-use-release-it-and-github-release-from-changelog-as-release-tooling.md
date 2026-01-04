# Use release-it and github-release-from-changelog for releasing

The release process should be automated.
`CHANGELOG.md` should be automatically put as content in the GitHub releases, because [Sibbell](https://about.sibbell.com/) puts that content in emails announcing a new release.

## Considered Alternatives

* Combination between release-it and github-release-from-changelog
* [release-it](https://webpro.github.io/release-it/)
* [github-release-from-changelog](https://github.com/MoOx/github-release-from-changelog)
* [npmpub](https://github.com/MoOx/npmpub)
* [np](https://github.com/sindresorhus/np)
* [npm-github-release](https://github.com/bradyholt/npm-github-release)
* [semantic-release](https://github.com/semantic-release/semantic-release)

## Decision Outcome

* Chosen Alternative: Combination between release-it (run first) and github-release-from-changelog (run second), because all other tools do not work.
* npmpub has issues on Windows: https://github.com/MoOx/npmpub/issues/19
* ng has issues on Windows: https://github.com/sindresorhus/np/issues/204
* npm-github-release does not read `CHANGELOG.md`, but demands text
* semantic-release seems to be over-engieered
