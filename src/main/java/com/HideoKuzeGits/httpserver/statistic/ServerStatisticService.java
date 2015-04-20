package com.HideoKuzeGits.httpserver.statistic;

import com.HideoKuzeGits.httpserver.statistic.handlers.ConnectionCountHandler;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;

/**
 * Process server statistic and return html page.
 */

//Single instance per server.
public class ServerStatisticService {

    private ServerStatistic serverStatistic;
    private ConnectionCountHandler connectionCountHandler;

    /**
     * Freemarker statistic page template.
     */
    private Template template;


    public ServerStatisticService() {

        Configuration cfg = new Configuration();
        cfg.setClassForTemplateLoading(this.getClass(), "/");
        try {
            template = cfg.getTemplate("Tables.ftl");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @return server statistic html.
     */
    public String getStatisticPage() throws IOException, TemplateException {

        serverStatistic.setConnectionsCurrentlyOpened(connectionCountHandler.getConnectionsCount());
        return compileTemplate(serverStatistic);
    }


    /**
     *
     * Compile freemarker template.
     *
     * @param serverStatistic server statistic object to put in model.
     * @return statistic page html.
     */
    private String compileTemplate(ServerStatistic serverStatistic) throws IOException, TemplateException {

        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("statistic", serverStatistic);
        StringWriter stringWriter = new StringWriter();
        template.process(data, stringWriter);
        return stringWriter.toString();
    }

    public void setServerStatistic(ServerStatistic serverStatistic) {
        this.serverStatistic = serverStatistic;
    }

    public void setConnectionCountHandler(ConnectionCountHandler connectionCountHandler) {
        this.connectionCountHandler = connectionCountHandler;
    }
}
