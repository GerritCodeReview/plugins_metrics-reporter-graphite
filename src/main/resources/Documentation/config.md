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
