// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that creates a log-in page for users to access comments. */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;");
        PrintWriter out = response.getWriter();

        UserService credentials = UserServiceFactory.getUserService();
        if(credentials.isUserLoggedIn()) {
            String logoutUrl = credentials.createLogoutURL("/homepage.html");
            out.println("<p>Welcome back, " + credentials.getCurrentUser().getEmail() + ".</p>");
            
            out.println("<form action=\"/data\" method=\"POST\">");
            out.println("<ul id=\"comment-history\"></ul><br>");
            out.println("<input name=\"input\" placeholder=\"Leave a comment\"><br>");
            out.println("<input type=\"submit\">");
            out.println("</form>");
            out.println("<p>Number of comments to display:</p>");
            out.println("<input type=\"number\" name=\"num-comments\" id=\"num-comments\" min=\"1\" value=\"10\" onchange=\"updateComments()\"><br><br>");
            out.println("<form action=\"/delete-data\" method=POST>");
            out.println("<button onclick=\"deleteComments()\">Delete Comments</button>");
            out.println("</form>");

            out.println("<p>Logout <a href=\"" + logoutUrl + "\">here.</p>");
        } else {
            String loginUrl = credentials.createLoginURL("/homepage.html");
            out.println("<p>Please login <a href=\"" + loginUrl + "\">here.</a></p>");
        }
    }
}
