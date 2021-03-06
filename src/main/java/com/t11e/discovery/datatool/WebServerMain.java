package com.t11e.discovery.datatool;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import org.apache.commons.lang.StringUtils;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.handler.ContextHandlerCollection;
import org.mortbay.jetty.security.SslSocketConnector;
import org.mortbay.jetty.webapp.WebAppContext;

public class WebServerMain
{
  private static final Logger logger = Logger.getLogger(WebServerMain.class.getName());

  public static void main(final String[] args)
    throws IOException
  {
    final OptionParser parser = new OptionParser();
    try
    {
      final WebServerMain main = fromArgs(parser, args);
      main.start();
    }
    catch (final Exception e)
    {
      final StringWriter helpMsg = new StringWriter();
      parser.printHelpOn(helpMsg);
      System.err.println(e.getLocalizedMessage());
      System.err.println(helpMsg);
      logger.log(Level.SEVERE, e.getLocalizedMessage(), e);
      logger.log(Level.SEVERE, helpMsg.toString());
      System.exit(1);
    }
  }

  public static WebServerMain fromArgs(final OptionParser parser, final String[] args)
  {
    final OptionSpec<String> bindAddress = parser
      .accepts("bind-address", "Optional interface address on which the Discovery Data Tool should listen.")
      .withRequiredArg().ofType(String.class);
    final OptionSpec<Integer> port = parser
      .accepts("port", "Optional port on which the Discovery Data Tool will listen for HTTP requests.")
      .withRequiredArg().ofType(Integer.class);
    final OptionSpec<Integer> httpsPort = parser
      .accepts("https-port", "Optional port on which the Discovery Data Tool will listen for HTTPS requests.")
      .withRequiredArg().ofType(Integer.class);
    final OptionSpec<String> keystoreFile = parser
      .accepts(
        "keystore-file",
        "Path the a Java Key Store file containing the server certificate and private key. Required if you want to use HTTPS.")
      .withRequiredArg().ofType(String.class);
    final OptionSpec<String> keystorePassword = parser
      .accepts(
        "keystore-pass",
        "Password for the Java Key Store file containing the server certificate and private key. Required if you want to use HTTPS.")
      .withRequiredArg().ofType(String.class);
    final OptionSpec<String> keyPassword = parser
      .accepts("key-pass", "Password for the server private key. Required if your key has a password.")
      .withRequiredArg().ofType(String.class);
    final OptionSpec<String> truststoreFile = parser
      .accepts("truststore-file", " Optional path to a Java Key Store file containing trusted client certificates.")
      .withRequiredArg().ofType(String.class);
    final OptionSpec<String> truststorePassword = parser
      .accepts("truststore-pass", "Password for the trust store. Required if using client certificates.")
      .withRequiredArg().ofType(String.class);

    OptionSet options = null;
    options = parser.parse(args);
    if ((!options.has(port) && !options.has(httpsPort)) ||
        (options.has(httpsPort) && (!options.has(keystoreFile) || !options.has(keystorePassword))))
    {
      throw new IllegalArgumentException(
        "You must specify --port or --https-port, --keystore-file, and --keystore-pass");
    }
    final WebServerMain main = new WebServerMain();
    main.address = options.valueOf(bindAddress);
    main.httpPort = options.valueOf(port);
    main.httpsPort = options.valueOf(httpsPort);
    main.keystoreFile = options.valueOf(keystoreFile);
    main.keyStorePassword = options.valueOf(keystorePassword);
    main.keyPassword = options.valueOf(keyPassword);
    main.trustStore = options.valueOf(truststoreFile);
    main.trustStorePassword = options.valueOf(truststorePassword);
    main.warPath = findWarPath();
    main.init();
    return main;
  }

  private Server server;
  private String address;
  private Integer httpPort;
  private Integer httpsPort;
  private String keystoreFile;
  private String keyStorePassword;
  private String keyPassword;
  private String trustStore;
  private String trustStorePassword;
  private String warPath;

  private WebServerMain()
  {
  }

  private void init()
  {
    server = new Server();
    final ContextHandlerCollection contexts = new ContextHandlerCollection();
    server.setHandler(contexts);

    if (httpPort != null)
    {
      final SocketConnector connector = new SocketConnector();
      connector.setPort(httpPort);
      setAddress(address, connector);
      server.addConnector(connector);
    }
    if (httpsPort != null)
    {
      final SslSocketConnector connector = new SslSocketConnector();
      connector.setPort(httpsPort);
      if (StringUtils.isNotBlank(keystoreFile))
      {
        connector.setKeystore(keystoreFile);
        connector.setPassword(keyStorePassword);
        if (StringUtils.isNotBlank(keyPassword))
        {
          connector.setKeyPassword(keyPassword);
        }
      }
      if (StringUtils.isNotBlank(trustStore))
      {
        connector.setTruststore(trustStore);
        connector.setTrustPassword(trustStorePassword);
      }
      setAddress(address, connector);
      server.addConnector(connector);
    }

    final WebAppContext webapp = new WebAppContext();
    webapp.setWar(warPath);
    webapp.setContextPath("/");
    {
      // Allow SLF4J to leak through from system classpath into web app classpath so that we can exclude SLF4J from the web-inf/lib
      // and avoid the SLF4J warning that "Multiple bindings were found on the class path"
      // See http://www.slf4j.org/codes.html#multiple_bindings and http://docs.codehaus.org/display/JETTY/Classloading for details.
      final List<String> serverClasses = new ArrayList<String>(Arrays.asList(webapp.getServerClasses()));
      serverClasses.remove("org.slf4j.");
      webapp.setServerClasses(serverClasses.toArray(new String[serverClasses.size()]));
    }
    contexts.addHandler(webapp);
  }

  private static void setAddress(
    final String address,
    final SocketConnector connector)
  {
    if (StringUtils.isNotBlank(address))
    {
      connector.setHost(address);
    }
  }

  public void start()
    throws Exception
  {
    server.start();
    final String msg = "Discovery Data Tool"
      + (StringUtils.isNotBlank(address) ? " bound to " + address : "")
      + " listening"
      + (httpPort != null ? " for HTTP on " + httpPort : "")
      + (httpsPort != null ? (httpPort != null ? " and" : "") + " for HTTPS on " + httpsPort : "");
    logger.info(msg);
  }

  public void stop()
    throws Exception
  {
    server.stop();
  }

  public void destroy()
  {
    server.destroy();
    server = null;
  }

  private static String findWarPath()
  {
    final String resourceName = WebServerMain.class.getName().replace('.', '/') + ".class";
    final URL url = WebServerMain.class.getClassLoader().getResource(resourceName);
    final String warPath;
    try
    {
      warPath = new URI(url.getPath()).getPath().replaceFirst("^file:", "").replaceFirst("!.*$", "");
    }
    catch (final URISyntaxException e)
    {
      throw new RuntimeException(e);
    }
    return warPath;
  }
}
