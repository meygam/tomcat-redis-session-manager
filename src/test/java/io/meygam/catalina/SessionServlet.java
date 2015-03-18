package io.meygam.catalina;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by saravana on 3/16/15.
 */
public class SessionServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(true);
        session.setAttribute("name1","value1");
        session.setAttribute("name2","value2");
        session.setAttribute("name3","value3");
        resp.getWriter().append("Session Id: " + session.getId());
    }
}
