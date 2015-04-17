package com.HideoKuzeGits.httpserver.status;

import com.HideoKuzeGits.httpserver.status.handlers.ConnectionCountHandler;
import com.HideoKuzeGits.httpserver.status.logs.ConnectionLog;
import com.HideoKuzeGits.httpserver.status.logs.ConnectionLogLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

public class StatusCashService {

    public String getStatusPage() throws IOException, TemplateException {

        List<ConnectionLog> connectionLogs = new ConnectionLogLoader().load();
        ServerStatus serverStatus = new ServerStatus(connectionLogs);
        serverStatus.setConnectionsCurrentlyOpened(ConnectionCountHandler.getConnectionsCount());

        return compileTemplate(serverStatus);
    }

    private String compileTemplate(ServerStatus serverStatus) throws IOException, TemplateException {

        Configuration cfg = new Configuration();
        cfg.setDirectoryForTemplateLoading(new File("/"));
        URL path = getClass().getResource("/Tables.ftl");
        Template template = cfg.getTemplate(path.getFile());
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("status", serverStatus);
        StringWriter stringWriter = new StringWriter();
        template.process(data, stringWriter);
        return stringWriter.toString();
    }
}
