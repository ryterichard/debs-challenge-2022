# gRPC

### Local Workspace Setup *(using IntelliJ IDEA)*

1. Add challenger.proto to src/main/resources
2. Edit configuration -> Add new configuration -> Maven -> clean generate-sources compile install
   (Add -Dos.arch=x86_64 parameter to the maven goal on a mac m1 system)
3. Edit configuration -> Add new configuration -> Application