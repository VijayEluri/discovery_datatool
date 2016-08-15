The Discovery Data Tool provides a cross-platform implementation of a Changeset
Publisher. It uses JDBC to connect to the database and is thus compatible
with most major databases.

Documentation can be found at:

  https://transparensee.com/docs

To build the release run:

  ant clean release

To use the release, get the built zip from

  build.ant/discovery_datatool.zip

Unpack it and create a discovery_datatool.xml configuration file. The release
includes example configuration files in the examples directory of the zip file.

Then run the tool by executing the run.sh script.
