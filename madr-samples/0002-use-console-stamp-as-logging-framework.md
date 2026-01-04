# Use console-stamp as logging framework

The tool should provide debug logging functionality.

## Considered Alternatives

* [console-stamp](https://www.npmjs.com/package/console-stamp)
* `conssole.log`
* [console-log-level](https://github.com/watson/console-log-level)
* [winston](https://www.npmjs.com/package/winston)
* [beautylog](https://www.npmjs.com/package/beautylog)
* [chalk](https://www.npmjs.com/package/chalk)
* [other awesome logging libraries](https://github.com/sindresorhus/awesome-nodejs#logging)

## Decision Outcome

* Chosen Alternative: console-stamp
* Users can still use `console.log`, `console.info`, ...
* console-log-level does not print log level before the log output
