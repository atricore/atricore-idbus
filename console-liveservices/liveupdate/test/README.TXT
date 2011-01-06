* This tests simulates a set of versions for a given test distribution.

Due to some maven limitations, each version must be built separated from the others. We defined a maven profile
for each one of them so that you can do this.

The test contains :

* An OSGi bundle with services
* An OSGi bundle with command line components, to execute the service.
* An OSGi bundlen with the liveupdate installer.
* A liveupdate distribution of the componentes
* A liveupdate artifact repository for the components.