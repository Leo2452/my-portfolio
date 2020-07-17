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

import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;
import com.google.sps.data.Comment;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
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
@WebServlet("/comments")
public class CommentsServlet extends HttpServlet {

    private final Gson gson = new Gson();
    private final UserService credentials = UserServiceFactory.getUserService();
    private final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    // How to try/catch JavaIOException as a private final variable
    private final LanguageServiceClient languageService = LanguageServiceClient.create();

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Query query = new Query("comment").addSort("date", SortDirection.DESCENDING);
        PreparedQuery results = datastore.prepare(query);
        response.setContentType("application/json;");

        if(!credentials.isUserLoggedIn()) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        } else {
            //Construct an array of comment history
            List<Comment> commentHistory = new ArrayList<>();
            for (Entity entry: results.asIterable()) {
                String content = (String) entry.getProperty("content");
                Date date = (Date) entry.getProperty("date");
                String email = (String) entry.getProperty("email");
                double score = (double) entry.getProperty("score");

                commentHistory.add(new Comment(content, date, email, score));
            }
            response.getWriter().println(gson.toJson(commentHistory));
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String content = request.getParameter("input");
        Date date = new Date();
        String email = credentials.getCurrentUser().getEmail();

        Document doc =
            Document.newBuilder().setContent(content).setType(Document.Type.PLAIN_TEXT).build();
        Sentiment sentiment = languageService.analyzeSentiment(doc).getDocumentSentiment();
        double score = sentiment.getScore();

        //Create Entity of entry
        Entity entry = new Entity("comment");
        entry.setProperty("date", date);
        entry.setProperty("content", content);
        entry.setProperty("email", email);
        entry.setProperty("score", score);

        datastore.put(entry);
        response.sendRedirect("/homepage.html");
    }

    @Override
    public void destroy() {
        languageService.close();
    }
}
