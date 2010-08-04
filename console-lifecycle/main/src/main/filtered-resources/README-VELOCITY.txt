Files included here will be filtered by maven,
so properties that match maven definitions will be replaced during
compile time!
The rest will be interpreted by velocity on runtime.

For example:

${myBuildTimeProperty} : will be filtered by maven
$myRuntimeProperty     : will be processed by Velocity


