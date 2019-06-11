package servlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.Enumeration;

public class MyServlet extends HttpServlet {

    @Override
    public void init(){
        System.out.println("servlet实例初始化了");
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        PrintWriter writer = response.getWriter();

        request.setCharacterEncoding("utf-8");

        response.setContentType("text/html;charset=utf-8");
        response.setCharacterEncoding("utf-8");

        String userId = request.getParameter("userId");
        String pwd = request.getParameter("pwd");
        if ("admin".equals(userId) && "123".equals(pwd)){
            writer.println("success");
        }else{
            writer.println("fail");
        }
        writer.println("<br/>");

        String servletPath = this.getServletContext().getRealPath("/");
        writer.println("servlet name:" + this.getServletName());
        writer.println("servlet path:" + servletPath);
        writer.println("init parameter:" + this.getInitParameter("init"));
        Enumeration<String> keys = this.getInitParameterNames();
        while (keys.hasMoreElements()){
            System.out.println("init parameter:" + keys.nextElement());
        }

        //在ServletContext中设置全局参数，这个项目的所有servlet实例都可以访问到，可以在MyServlet_1中看效果
        this.getServletContext().setAttribute("login", "this is login servlet");
        //获取web项目下指定资源的内容，返回的是字节输入流。
        InputStream is = this.getServletContext().getResourceAsStream("/WEB-INF/web.xml");
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader bf = new BufferedReader(isr);
        String s = null;
        while ((s = bf.readLine()) != null){
            System.out.println(s);
        }
        writer.close();
    }

    @Override
    public void destroy(){
        System.out.println("servlet实例销毁了");
    }

    /***
     * servlet的生命周期是什么？
     * 服务器启动时，如果web.xml中配置load-on-startup=1，那么启动时就会初始化servlet实例，调用init()方法。
     * 如果不配置load-on-startup，那么默认就是load-on-startup=0，那第一次请求该servlet时会初始化一个servlet实例。
     * 一直到服务器关闭时，才会销毁所有的servlet实例，执行destroy()方法。
     */

    /***
     * servlet设计模式
     * servlet是典型的模板方法模式。
     * 其中service方法就是模板方法，整个servlet的顶级逻辑，service方法中调用的各种do方法就是基础方法，让开发选择性的去写逻辑。
     */

    /***
     * Servlet生命周期的三个关键方法，init、service、destroy。
     * 还有另外两个方法
     * 一个getServletConfig()方法来获取ServletConfig对象，ServletConfig对象可以获取到Servlet的一些信息。
     * 一个getServletContext()方法，用来获取servlet上下文。获得了ServletContext对象，就能获取大部分我们需要的信息，比如获取servlet的路径。
     */

    /***
     * 几个重点的对象，ServletConfig、ServletContext、request、response
     *
     * 1.ServletConfig对象
     * 获取途径：getServletConfig();
     * 这个对象能得到四个东西：
     * getServletName();  获取servlet的名称，也就是我们在web.xml中配置的servlet-name
     * getServletContext(); 获取ServletContext对象，该对象的作用看下面讲解
     * getInitParameter(String); 获取在servlet中初始化参数的值。这里注意与全局初始化参数的区分，这个获取的只是在该servlet下在web.xml中的初始化参数。
     * getInitParameterNames(); 获取在servlet中所有web.xml中配置的初始化参数的名字，也就是key值，只有通过key值，才能找到各个初始化参数的value值，注意返回的是枚举类型。
     *
     * 2.ServletContext对象
     * 获取途径：getServletContext(); 、getServletConfig().getServletContext();
     * 这两种获取方式的区别是
     * 第一种是直接拿，在GenericServlet中已经帮我们用getServletConfig().getServletContext();拿到了ServletContext，我们只需要直接获取就行了。
     * 第二种就相当于我们自己在获取一遍，两种读是一样的。
     *
     * 功能：tomcat为每个web项目都创建一个ServletContext实例，tomcat在启动时创建，服务器关闭时销毁。
     * 1)在一个web项目中共享数据
     * 管理web项目资源，为整个web配置公共信息等，通俗点讲，就是一个web项目，就存在一个ServletContext实例，每个Servlet读可以访问到它。
     * setAttribute(String name, Object obj) 在web项目范围内存放内容，以便让在web项目中所有的servlet读能访问到。
     * getAttribute(String name) 通过指定名称获得内容。
     * removeAttribute(String name) 通过指定名称移除内容。
     * 2)整个web项目初始化参数
     * getInitParameter(String name)　　通过指定名称获取初始化值
     * getInitParameterNames()　　获得枚举类型
     * 初始化参数的配置在web.xml文件中
     * 3)获取web项目资源
     * 获取web项目下指定资源的路径：getServletContext().getRealPath("/WEB-INF/web.xml")
     * 获取web项目下指定资源的内容，返回的是字节输入流。InputStream getResourceAsStream(java.lang.String path)
     *
     * 3.request对象
     * 我们知道，request就是将请求文本封装而成的对象，所以通过request能获得请求文本中的所有内容，请求头、请求体、请求行。
     *
     * 请求转发
     * request.getRequestDispatcher(String path).forward(request,response);
     * 转发后跳转的页面，这里不管用不用"/"开头，都是以web项目根开始，因为这是请求转发，请求转发只局限与在同一个web项目下使用，所以这里一直都是从web项目根下开始的。
     * 浏览器中url不会改变，也就是浏览器不知道服务器做了什么，是服务器帮我们跳转页面的，并且在转发后的页面，能够继续使用原先的request，因为是原先的request，所以request域中的属性都可以继续获取到。
     *
     * 4.response对象
     * 重定向(页面跳转)
     * 方式一：手动方案
     * response.setStatus(302);　　//状态码302就代表重定向
     * response.setHeader("location","http://www.baidu.com");
     *
     * 方式二：使用封装好的，通过response.sendRedirect("http://www.baidu.com");
     * 特点：服务器告诉浏览器要跳转的页面，是浏览器主动去跳转的页面，浏览器知道，也浏览器的地址栏中url会变，是浏览器重新发起一个请求到另外一个页面，所以request是重新发起的，跟请求转发不一样。
     */
}
