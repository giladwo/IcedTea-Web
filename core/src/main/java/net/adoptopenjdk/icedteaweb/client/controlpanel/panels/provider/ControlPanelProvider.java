package net.adoptopenjdk.icedteaweb.client.controlpanel.panels.provider;

import net.sourceforge.jnlp.config.DeploymentConfiguration;

import javax.swing.JComponent;

public interface ControlPanelProvider {

    String getTitle();

    String getName();

    int getOrder();

    JComponent createPanel(DeploymentConfiguration config);

    default boolean isActive(final DeploymentConfiguration config) {
        return true;
    }
}
