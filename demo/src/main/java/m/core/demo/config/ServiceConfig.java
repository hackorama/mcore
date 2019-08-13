package m.core.demo.config;

import org.aeonbits.owner.Config;

public interface ServiceConfig extends Config {

    @Key("service.environment.host")
    String environmentServerHost();

    @Key("service.environment.port")
    @DefaultValue("0")
    int environmentServerPort();

    @Key("service.environment.url")
    String environmentServiceURL();

    @Key("service.group.host")
    String groupServerHost();

    @Key("service.group.port")
    @DefaultValue("0")
    int groupServerPort();

    @Key("service.group.url")
    String groupServiceURL();

    @Key("service.workspace.host")
    String workspaceServerHost();

    @Key("service.workspace.port")
    @DefaultValue("0")
    int workspaceServerPort();

    @Key("datastore.location")
    String datastoreLocation();

}
