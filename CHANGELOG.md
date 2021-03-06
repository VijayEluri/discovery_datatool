# 1.12 - 2012-08-10

  * Updates writing of changeset XML to allow item properties to be empty.
    Specifically impacts handling of JSON columns with null values.
    Previously, a null value in a JSON map value would cause a runtime
    exception while generating the changeset, and the changeset XML would be
    invalid.

# 1.11 - 2012-01-16

  * Adds validation of changeset SQL against the database on startup. May be
    skipped by setting validate_sql=false in datatool.properties.

  * Adds progress logging of changeset creation. Uses same progress log message
    formatting as Discovery Engine.

  * Makes all references to columns in XML configuration attributes
    case-insensitive (e.g. idColumn, provider, kind, discriminator,
    jsonColumns, jsonColumnNames, scopedJsonColumnNames, mergeColumns/@name and
    mergeColumns/@value).

  * Updates discovery_datatool shell script to default the http port to 8089.
    Note that if you want to listen for both HTTP and HTTPS, then you need to
    specify both http_port and https_port in datatool.properties.

  * Removes extraneous chatter and warnings from log output.

# 1.10 - 2012-01-03

  * Simplifies process of setting up init.d script. Startup options are largely
    specified in datatool.properties. See init-script/README for detailed
    instructions. Removes use of jakarta-commons-daemon (jsvc).

  * Changes logging.properties to log to a file with rotation at 10mb keeping
    up to 30 files.

# 1.9 - 2011-12-26

  * Fixes defect in add-to-item configuration parsing: add-to-item was only
    read from delta configuration elements. add-to-item elements in snapshots
    were ignored. delta add-to-item elements were incorrectly used by the
    publisher's snapshots.

# 1.8 - 2011-12-08

  * Adds configuration attribute for item property-case (preserve, lower, upper
    and legacy). Starting with new schema version 7, the default is preserve. Older
    schema versions continue to have the legacy behavior of lowercasing
    property names from column names but not from pivoted values.

    preserve - Use whatever case we get back from the database
    lower    - All property names are converted to locale lower case
    upper    - All property names are converted to locale upper case
    legacy   - Property names from sql column labels are converted to lower case,
               but property names from vertical table values are preserved.

  * Fixes "If a subquery is empty, it should be ignored (and reported) and not
    throw an error."

  * Adds support for unscopedJsonColumns to be able to merge in JSON column
    data to the top level properties. Deprecates property "jsonColumnNames" in
    favor of more explicit "scopedJsonColumns" for the same behavior. Adds new
    property "unscopedJsonColumns" for columns that should be merged into the
    top-level item properties.

  * Makes subqueries use NullDefaultCaseInsensitiveParameterSource. If subquery
    uses a bound parameter that is not present in the main item properties, then
    null is used for the parameter value.

# 1.7 - 2011-11-04
  * Adds support for add-to-item for deltas and snapshots (the Discovery Engine
    does not support add-to-item for bulk or full changesets). The add-to-item
    configuration element supports the same attributes and child configuration
    elements as set-item.
  * Adds another subquery test scenario.
  * Fixes Data Tool Outputs Empty Strings: If a column returns an empty string,
    the data tool still outputs the column value.  Empty strings should not be
    output.
  * Fixes merge-columns should not be case sensitive.

# 1.6 - 2011-09-28

  * Adds support for provider and kind to Discovery Data Tool. The
    configuration elements can now take optional attributes providerColumn and
    kindColumn. If either of these are specified, then the resulting changeset
    set-item and remove-item elements will not have an id attribute, and instead
    will have locator, provider and kind attributes.  The providerColumn and
    kindColumn are not included in the resulting set-item properties, since that
    would be redundant.
  * No longer includes id column in set-item properties, since it is redundant
    to the set-item id attribute.

# 1.5 - 2011-08-04

  * Adds support for "vertical" tables.

# 1.4 - 2011-03-08

  * Creates new version of Data Tool Configuration XML schema to support bulk
    and full changesets and subqueries.
  * Adds support for bulk and full changesets in addition to snapshots.
  * Adds support for a wide variety of subquery types in order to better
    support flattening complex database structures without the use of views or
    stored procedures.
  * Fixes WebServerMain to handle paths with spaces in directory names. Fixes
    problem where datatool cannot be run if its path contains spaces.
  * Updates tests to verify tool continutes to be backwards compatible in
    parsing all versions of the configuration xml.
  * Adds support for column aliases.
  * Makes named SQL parameter matching case insensitive by creating and using
    CaseInsensitveParameterSource (based on Spring's MapSqlParameterSource,
    which unfortunately does not allow plugging in a different Map
    implementation). All tests now pass.
  * Updates logging.properties to log com.t11e.discovery.datatool at FINE. And
    adds a comment explaining how to enable logging sql query execution speed.
    Turns down default level to WARNING (which quiets Spring).
  * Adds test to show that Discovery Data Tool can call stored procedures.

# 1.3 - 2010-10-01

  * Adds forceSnapshot boolean parameter to ChangesetController. If true, then
    start date of null is used and a snapshot is created regardless of the
    profile's last run time.

# 1.2 - 2010-09-07

  * Adds support for protecting incoming connections via SSL and basic
    authentication.
  * Adds throwing an exception in config XML does not have a root config
    element in a recognized namespace.

# 1.1 - 2010-06-11

  * Added createSql support to sqlProfile, used to auto create missing
    changeset profiles.
  * Bumped the schema from http://transparensee.com/schema/datatool-config-1
    to http://transparensee.com/schema/datatool-config-2.
  * Added init script to the built release, depends upon Apache Commons Daemon
    jsvc (http://commons.apache.org/daemon/jsvc.html).
  * Fixed Configuration parsing issue where long text elements in the XML
    were being truncated.

# 1.0 - 2010-06-03

  * Initial release
