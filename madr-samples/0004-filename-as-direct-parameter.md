# Filename as direct parameter

The command line interface ("CLI") has to be designed.

## Considered Alternatives

* Filename as last parameter.
  File always has to be given and file name at end: `[-d <directory>] [-i] [<input>]`. 
  Current directory is used if no `-d` is given.
  If `<input>` is missing: help is printed
* Directory as last parameter
  Directory always has to be given and directory name at end: `[-f <file>] [-i] <directory>`.
  If `-f` is not provided: Output to stdout without reading any index file
* No definitve last parameter

### Decision Outcome

* Chosen option: Filename as last parameter. It always has to be provided.
* Justification: Consistency to [markdown-toc](https://github.com/jonschlinkert/markdown-toc). See https://github.com/jonschlinkert/markdown-toc#cli.
