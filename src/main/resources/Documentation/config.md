Graphite Metrics Reporter Configuration
=======================================

File `@PLUGIN@.config`
-------------------------

The optional file `$site_path/etc/@PLUGIN@.config` is a Git-style
config file that controls the settings for the @PLUGIN@ plugin.

graphite.host
:	Hostname of the Graphite server. Defaults to `localhost`.

graphite.port
:	Port number of the Graphite server. Defaults to `2003`.

graphite.prefix
:	Prefix to use when reporting metrics. Defaults to `gerrit.`
	suffixed with the hostname of `localhost`.

graphite.rate
:	Reporting rate in seconds. May be specified in common time
	units such as 'm', 's', 'ms', etc, but will be converted
	to seconds. The lowest supported rate is `1 s`.
	Defaults to `60 s`.
