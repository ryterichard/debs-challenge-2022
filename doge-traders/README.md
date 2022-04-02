
# Doge Traders <img align="center-" alt="Doge Traders" width="75x" src="assets/images/doge.png" style="padding-right:10px;" />

### Local Workspace Setup

1. Edit configuration -> Add new configuration -> Maven -> clean install
2. Edit configuration -> Add new configuration -> Application
    * Modify options -> Add dependencies with provided scope to classpath


#### To Do
 - Fetch SecurityType value once for every batch without adding overhead. Keyby along with symbol using a composite key? Right now value is hardcoded)
 - Handle endbenchmark event. Since it is at the source it ends after putting events on the data stream and does not wait for data processing to be done. Wait till all events are processed and then shutdown system.
 - Optimize implementation
 - Deploy on cluster.
 - Bonus: Visualization using Graphana~~~~