package apr_test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AprTestServlet extends HttpServlet {
    private ExecutorService executorService;

    private String JAVA_EXE_PATH;
    private String APR_EXECUTABLE_PATH;
    private String APR_EXECUTABLE_CURRENT_DIRECTORY;
    
    public void init() throws ServletException {
        super.init();
        
        JAVA_EXE_PATH = getServletConfig().getInitParameter("JAVA_EXE_PATH");
        APR_EXECUTABLE_PATH = getServletConfig().getInitParameter("APR_EXECUTABLE_PATH");
        APR_EXECUTABLE_CURRENT_DIRECTORY = getServletConfig().getInitParameter("APR_EXECUTABLE_CURRENT_DIRECTORY");
        
        executorService = Executors.newCachedThreadPool();
    }
    
    public void destroy() {
        super.destroy();
        executorService.shutdownNow();
    }
    
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("doGet - start");
        
        launchProcess();
        
        final String r = "<html><body>Called!</body></html>";
        
        resp.setContentLength(r.getBytes().length);
        
        PrintWriter writer = resp.getWriter();
        writer.print(r);
        
        System.out.println("doGet - stop");

        writer.flush();
    }

    private void launchProcess() throws IOException {
        executorService.submit(new Runnable() {
            public void run() {
                try {
                    ProcessBuilder pb = headlessProcessBuilder();
                    pb.redirectErrorStream(true);

                    final Process process = pb.start();
                    drainInputStream(process.getInputStream());
                    drainInputStream(process.getErrorStream());
                } catch (IOException e) {
                    System.out.println("IOException while launching process");
                    e.printStackTrace();
                }
            }
        });
    }
    
    
    private ProcessBuilder headlessProcessBuilder() {
        final ProcessBuilder pb = new ProcessBuilder(
                JAVA_EXE_PATH,
                "-cp",
                APR_EXECUTABLE_PATH,
                "-Djava.awt.headless=true",
                "apr_test.Main"
                );
        pb.directory(new File(APR_EXECUTABLE_CURRENT_DIRECTORY));
        return pb;
    }

    
    private void drainInputStream(final InputStream is) {
        new Thread() {
            public void run() {
                try {
                    while (true) {
                        int i = is.read();
                        if (i == -1) break;
                        System.out.print((char) i);
                    }
                } catch (IOException instance) {
                    instance.printStackTrace();
                }
            };
        }.start();
    }
    
}
