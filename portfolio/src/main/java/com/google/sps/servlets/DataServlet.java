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

<<<<<<< HEAD
import com.google.sps.data.LoginInfo;
=======
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
>>>>>>> fe04ab020e08463c2b971addb77d0a00cd79adcf
import com.google.sps.data.Comment;
import com.google.sps.data.Access;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that contains, saves and gets comments from database. 
 *  Uses LoginServlet to determine if a user is logged in to
 *  either show or hide comments.
 */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

    private final Gson gson = new Gson();

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Query query = new Query("comment").addSort("date", SortDirection.DESCENDING);
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery results = datastore.prepare(query);
        UserService credentials = UserServiceFactory.getUserService();
        response.setContentType("application/json;");

        List<Comment> commentHistory = new ArrayList<>();
        LoginInfo userLogin = (LoginInfo) request.getAttribute("userLogin");

        // Display comments if logged in
        if(userLogin.isLoggedIn()) {
            for (Entity entry: results.asIterable()) {
                String content = (String) entry.getProperty("content");
                Date date = (Date) entry.getProperty("date");
                String email = (String) entry.getProperty("email");

                commentHistory.add(new Comment(content, date, email));
            }
        }
        Access access = new Access(commentHistory, userLogin);
        response.getWriter().println(gson.toJson(access));
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String content = request.getParameter("input");
        Date date = new Date();
        UserService credentials = UserServiceFactory.getUserService();
        String email = credentials.getCurrentUser().getEmail();

        //Create Entity of entry
        Entity entry = new Entity("comment");
        entry.setProperty("date", date);
        entry.setProperty("content", content);
        entry.setProperty("email", email);

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(entry);
        response.sendRedirect("/homepage.html");
    }
}
