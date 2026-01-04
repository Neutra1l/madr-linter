# Extend markdown-toc

adr-log needs to be implemented in a quick and efficient way.

## Considered Alternatives

* Modify [markdown-toc](https://github.com/jonschlinkert/markdown-toc)
* Extend [github-markdown-toc](https://github.com/ekalinin/github-markdown-toc) 
* Extend [adr-j](https://github.com/adoble/adr-j)
* Implement from scratch

## Decision Outcome

* Chosen Alternative: Reuse markdown-toc
* markdown-toc already parses markdown files and modifies them.
  Adding directory crawling seems to be easy to implement using JavaScript.
  Using an npm-package makes it easy for users to install and use the tool.
  github-markdown-toc is a bash script and adr-j requires java.
  The installation of the former is uncommon and the latter is not that straight-forward to install.
