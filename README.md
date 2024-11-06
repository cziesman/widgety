# build instructions

If you are running on Fedora, docker has been removed in favor of podman. However, the Openshift Maven plugin still wants to talk to docker, so the first two lines are necessary to make the plugin believe that a docker daemon is running.

    export DOCKER_HOST="unix:/run/user/$(id -u)/podman/podman.sock"
    podman system service --time=0 unix:/run/user/$(id -u)/podman/podman.sock &
    
Make sure that the directory `/run/user/$(id -u)/podman` exists, or else create it before running the `podman system service` command. Make sure that the directory is writable.

These commands login to Openshift and select the appropriate project.

    oc login --token=<token> --server=<server_url>
    oc project widgety
    
This command uses Maven to build the deployment JAR locally.

    mvn -e -DskipTests -Dcom.redhat.xpaas.repo.redhatga -Dfabric8.skip=true --batch-mode -Djava.net.preferIPv4Stack=true clean package

This command triggers the S2i build on Openshift and pushes the resulting image to the OCP image repository.

    mvn oc:build

This command creates and configures a DeploymentConfig on Openshift.

    mvn oc:resource

This command creates a service and default route for the application on Openshift, and triggers a deployment using the DeploymentConfig that was created in the previous step.

    mvn oc:apply