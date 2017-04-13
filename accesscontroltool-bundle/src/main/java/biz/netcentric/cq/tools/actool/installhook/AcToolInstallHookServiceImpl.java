/*
 * (C) Copyright 2015 Netcentric AG.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package biz.netcentric.cq.tools.actool.installhook;

import java.util.Map;
import java.util.Properties;

import javax.jcr.Session;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.vault.fs.io.Archive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biz.netcentric.cq.tools.actool.aceservice.AceService;
import biz.netcentric.cq.tools.actool.configreader.ConfigFilesRetriever;
import biz.netcentric.cq.tools.actool.installationhistory.AcInstallationHistoryPojo;

@Component
@Service(value = AcToolInstallHookService.class)
public class AcToolInstallHookServiceImpl implements AcToolInstallHookService {

    private static final Logger LOG = LoggerFactory.getLogger(AcToolInstallHookServiceImpl.class);

    @Reference
    private AceService aceService;

    @Reference
    private ConfigFilesRetriever configFilesRetriever;

    @Override
    public AcInstallationHistoryPojo installYamlFilesFromPackage(Archive archive, Session session)
            throws Exception {
        AcInstallationHistoryPojo history = new AcInstallationHistoryPojo();

        Map<String, String> configs = configFilesRetriever.getConfigFileContentFromPackage(archive);
        history.setCrxPackageName(getArchiveName(archive));
        String[] restrictedToPaths = null; // never use path restriction for hook usage for now
        aceService.installConfigurationFiles(history, configs, restrictedToPaths, session);

        return history;
    }

    private String getArchiveName(Archive archive) {
        Properties properties = archive.getMetaInf().getProperties();
        String archiveName = properties != null ? (properties.getProperty("name") + "-" + properties.getProperty("version")) : null;
        return archiveName;
    }
}
